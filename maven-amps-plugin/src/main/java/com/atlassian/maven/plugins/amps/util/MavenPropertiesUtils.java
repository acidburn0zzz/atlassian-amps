package com.atlassian.maven.plugins.amps.util;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugin.descriptor.Parameter;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Validation helper methods for Mojos
 */
public class MavenPropertiesUtils
{
    /** Number of names we suggest when a user misspelled them **/
    private static final int ADVICE_COUNT = 4;

    /**
     * Reflection - Finds the field represented by propertyName
     * 
     * @return the field or null.
     * @throws MojoExecutionException
     *             if the field exists but is not assignable with a system property
     */
    private static Field findField(Object target, String propertyName)
    {
        Class<?> clazz = target.getClass();
        Field field = null;
        while (field == null && clazz != null)
        {
            try
            {
                field = clazz.getDeclaredField(propertyName);
            }
            catch (NoSuchFieldException nsee)
            {
                // Look at the parent too
                Type superClass = clazz.getGenericSuperclass();
                if (superClass instanceof Class)
                {
                    clazz = (Class<?>) superClass;
                }
                else
                {
                    clazz = null;
                }
            }
        }
        return field;
    }
    
    /**
     * Check that plugin is defined in the pom, i.e. avoid mvn amps:run when maven-jira-plugin
     * is defined in the pom.
     * @throws MojoExecutionException if the plugin being wrong is obviously wrong
     */

    public static void checkUsingTheRightAmpsPlugin(MavenContext mavenContext) throws MojoExecutionException
    {
        MavenProject project = mavenContext.getProject();
        MojoExecution mojoExecution = mavenContext.getMojoExecution();
        MojoDescriptor mojoDescriptor = mojoExecution.getMojoDescriptor();
        PluginDescriptor pluginDescriptor = mojoDescriptor.getPluginDescriptor();
        Xpp3Dom configuration = project.getGoalConfiguration(pluginDescriptor.getGroupId(), pluginDescriptor.getArtifactId(),
                mojoExecution.getExecutionId(), mojoDescriptor.getGoal());
        
        if (configuration == null)
        {
            // The current plugin doesn't seem defined in the pom.xml. Is there any other Amps plugin defined in the pom.xml?
            String suggestedAmpsArtifact = detectAmpsProduct(project);
            if (suggestedAmpsArtifact != null)
            {
                // Check it's not the current plugin
                if (!suggestedAmpsArtifact.equals(pluginDescriptor.getGoalPrefix()))
                {
                    // If there's another Amps plugin, it's fine to blow up, because we're not in a situation where the pom.xml is missing.
                    throw new MojoExecutionException(String.format("You are using %s:%s but maven-%s-plugin is defined in the pom.xml. Use %s:%s, or define %s in your pom.xml.",
                            pluginDescriptor.getGoalPrefix(),
                            mojoDescriptor.getGoal(),
                            suggestedAmpsArtifact,
                            suggestedAmpsArtifact,
                            mojoDescriptor.getGoal(),
                            pluginDescriptor.getArtifactId()
                            ));
                }
            }
            else if (!"run-standalone".equals(mojoExecution.getGoal()))
            {
                mavenContext.getLog().info("No <configuration> is defined for " + pluginDescriptor.getArtifactId() + ". " +
                		"Amps will work successfully, but you could set some parameters using a <configuration> tag in your pom.xml.");
            }
        }
    }

    /**
     * Check that {@literal <configuration>} elements are all used. If not, issue a warning.
     * Only check top-level elements, not deeper structures.
     * 
     * @param currentMojo
     *            the mojo being executed.
     * @param pluginDescriptor
     *            the plugin of the current mojo, to check whether parameters are declared in other mojos
     * @param configuration
     *            the configuration of the mojo
     */
    public static void checkUnusedConfiguration(final AbstractMojo currentMojo, MavenContext mavenContext)
    {
        MavenProject project = mavenContext.getProject();
        MojoExecution mojoExecution = mavenContext.getMojoExecution();
        MojoDescriptor mojoDescriptor = mojoExecution.getMojoDescriptor();
        PluginDescriptor pluginDescriptor = mojoDescriptor.getPluginDescriptor();
        Xpp3Dom configuration = project.getGoalConfiguration(pluginDescriptor.getGroupId(), pluginDescriptor.getArtifactId(),
                mojoExecution.getExecutionId(), mojoDescriptor.getGoal());
        
        if (configuration == null)
        {
            return;
        }
        
        List<String> unverifiedConfiguration = Lists.newArrayList();
        for (String parameter : firstLevelElementNames(configuration))
        {
            if (findField(currentMojo, parameter) == null)
            {
                unverifiedConfiguration.add(parameter);
            }
        }
        
        // Search whether the remaining parameters are used in other mojos of the same plugin
        Set<String> allParameters = findParametersOfAllMojos(pluginDescriptor);
        for (String name : unverifiedConfiguration)
        {
            if (!allParameters.contains(name))
            {
                // We issue a warning
                String message = "Unused element in <configuration>: "
                        + configuration.getChild(name).toString().replaceAll("<\\?.*\\?>\n", "");
                String advice = getAdviceWithClosestNames(name, allParameters, ADVICE_COUNT);
                if (!StringUtils.isBlank(advice))
                {
                    message += '\n' + advice;
                }
                currentMojo.getLog().warn(message);
            }
        }
    }


    /**
     * Find one build plugin which extends maven-amps-plugin. 
     * @param project the MavenProject
     * @return the goal prefix for the defined maven-*-plugin.
     */
    public final static String detectAmpsProduct(MavenProject project)
    {
        List<Plugin> buildPlugins = project.getBuildPlugins();

        Set<String> possiblePluginTypes = new HashSet<String>(ProductHandlerFactory.getIds());
        possiblePluginTypes.add("amps");

        if (buildPlugins != null)
        {
            for (Plugin pomPlugin : buildPlugins)
            {
                if ("com.atlassian.maven.plugins".equals(pomPlugin.getGroupId()))
                {
                    for (String type : possiblePluginTypes)
                    {
                        if (("maven-" + type + "-plugin").equals(pomPlugin.getArtifactId()))
                        {
                            return type;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static Set<String> findParametersOfAllMojos(PluginDescriptor pluginDescriptor)
    {
        Set<String> allParameters = Sets.newHashSet();
        for (MojoDescriptor otherMojo : (List<MojoDescriptor>) pluginDescriptor.getMojos())
        {
            // allParameters.add().getParameters();
            for (Parameter parameter : (List<Parameter>) otherMojo.getParameters())
            {
                allParameters.add(parameter.getName());
            }
        }
        return allParameters;
    }

    private static String getAdviceWithClosestNames(final String name, Set<String> list, int max)
    {
        final String lowerCaseName = name.toLowerCase(Locale.ENGLISH);
        // Unique elments ordered by decreasing distance
        List<String> closest = Lists.newArrayList(new TreeSet<String>(new Comparator<String>()
        {
            @Override
            public int compare(String first, String second)
            {
                return distance(first.toLowerCase(), lowerCaseName) - distance(second.toLowerCase(), lowerCaseName);
            }
        }));
        closest.subList(0, Math.min(ADVICE_COUNT, closest.size()));
        String advice;
        if (closest.size() > 1)
        {
            advice = "The closest matching names are '" + StringUtils.join(closest, "', '") + "'.";
        }
        else if (closest.size() == 1)
        {
            advice = "The closest matching name is '" + closest.get(0) + "'.";
        }
        else
        {
            advice = "";
        }
        
        return advice;
    }

    /**
     * Distance between two strings;<ul>
     * <li>0 when equal</li>
     * <li>1 when equal, case insensitive and without dots</li>
     * <li>2 when they contain each other, case insensitive and without dots</li>
     * <li>3 + percentage of different characters in other situations</li>
     * </ul>
     * @param left
     * @param right
     * @return the distance between the two strings
     */
    public static int distance(String left, String right)
    {
        if (left.equals(right))
        {
            return 0;
        }
        if (left.length() == 0)
        {
            return 3 + right.length();
        }
        if (right.length() == 0)
        {
            return 3 + left.length();
        }
        // 'i' stands for case-insensitive and without dots
        String iLeft = left.toLowerCase(Locale.ENGLISH).replaceAll("\\.", "");
        String iRight = right.toLowerCase(Locale.ENGLISH).replaceAll("\\.", "");
        if (iLeft.equals(iRight))
        {
            return 1;
        }
        if (iLeft.contains(iRight))
        {
            return 2;
        }
        if (iRight.contains(iLeft))
        {
            return 2;
        }
        return 3 + 100 * StringUtils.getLevenshteinDistance(iLeft, iRight) / Math.max(iLeft.length(), iRight.length());
    }

    private static List<String> firstLevelElementNames(Xpp3Dom configuration)
    {
        List<String> elementNames = Lists.newArrayList();
        for (Xpp3Dom child : configuration.getChildren())
        {
            String name = child.getName();
            if (child.getChildCount() == 0 && !name.contains("."))
            {
                elementNames.add(name);
            }
        }
        return elementNames;
    }

}