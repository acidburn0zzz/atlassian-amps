package com.atlassian.maven.plugins.amps.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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
import org.jfrog.maven.annomojo.annotations.MojoParameter;

import com.atlassian.maven.plugins.amps.AbstractProductHandlerMojo;
import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
     */
    private static Field findField(Object target, String propertyName)
    {
        
        Class<?> clazz = target.getClass();
        while (clazz != null)
        {
            for (Field declaredField : clazz.getDeclaredFields())
            {
                if (propertyName.equalsIgnoreCase(declaredField.getName()))
                {
                    return declaredField;
                }
            }
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
        return null;
    }
    
    public static void checkUsingTheRightLifecycle(MavenContext mavenContext) throws MojoExecutionException
    {
        String packaging = mavenContext.getProject().getPackaging();
        if (!packaging.equals("atlassian-plugin"))
        {
            mavenContext.getLog().info("You are not using <packaging>atlassian-plugin</packaging> in your pom.xml, therefore this build will not create an Atlassian Plugin artifact.");
        }
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
            // No maven-*-plugin in pom.xml. Search for other Amps plugins in pom.xml.
            String suggestedAmpsArtifact = detectAmpsProduct(project);
            if (suggestedAmpsArtifact != null)
            {
                // Check it's not the current plugin
                if (!suggestedAmpsArtifact.contains(pluginDescriptor.getGoalPrefix()))
                {
                    String goal = mojoDescriptor.getGoal();
                    String suggestedPrefix = getGoalPrefix(suggestedAmpsArtifact);
                    
                    String advice = "";
                    if ("run".equals(goal)) 
                    {
                        advice = "Did you want to run mvn " + pluginDescriptor.getGoalPrefix() + ":run-standalone ?";
                    }
                    
                    // If there's another Amps plugin, it's fine to blow up, because we're not in a situation where the pom.xml is missing.
                    throw new MojoExecutionException(String.format("You are using %s:%s but %s is defined in the pom.xml. Please use mvn %s:%s, or define %s in your pom.xml. %s",
                            pluginDescriptor.getGoalPrefix(),
                            goal,
                            suggestedAmpsArtifact,
                            suggestedPrefix,
                            goal,
                            pluginDescriptor.getArtifactId(),
                            advice
                            ));
                }
            }
            // No Amps plugin in pom.xml. Just tell the user.
            else if (!mojoExecution.getMojoDescriptor().isProjectRequired())
            {
                mavenContext.getLog().info("No <configuration> is defined for " + pluginDescriptor.getArtifactId() + ". " +
                		"Amps will work successfully, but you could set some parameters using a <configuration> tag in your pom.xml.");
            }
        }
    }
    
    static String getGoalPrefix(String pluginName)
    {
        // We need to do this manually because one of the maven versions doesn't return it.
        List<String> parts = Lists.newArrayList(pluginName.split("-"));
        parts.remove("maven");
        parts.remove("plugin");
        if (parts.size() > 0)
        {
            return StringUtils.join(parts, "-");
        }
        return pluginName;
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
        List<String> possiblePluginTypes = Lists.newArrayList("maven-amps-plugin");
        for (String type : ProductHandlerFactory.getIds())
        {
            possiblePluginTypes.add("maven-" + type + "-plugin");
        }
        if (buildPlugins != null)
        {
            for (Plugin pomPlugin : buildPlugins)
            {
                if ("com.atlassian.maven.plugins".equals(pomPlugin.getGroupId()))
                {
                    if (possiblePluginTypes.contains(pomPlugin.getArtifactId()))
                    {
                        return pomPlugin.getArtifactId();
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

    static String getAdviceWithClosestNames(final String name, Set<String> list, int max)
    {
        final String lowerCaseName = name.toLowerCase(Locale.ENGLISH);
        ScoreSheet closestMatching = new ScoreSheet();
        for (String item : list)
        {
            closestMatching.putScore(item, distance(item.toLowerCase(), lowerCaseName));
        }
        
        List<String> firstThree = closestMatching.head(max);
        
        String advice;
        if (firstThree.size() > 1)
        {
            advice = "The closest matching names are '" + StringUtils.join(firstThree, "', '") + "'.";
        }
        else if (firstThree.size() == 1)
        {
            advice = "The closest matching name is '" + firstThree.get(0) + "'.";
        }
        else
        {
            advice = "";
        }
        
        return advice;
    }
    
    /**
     * Representation of a score sheet which allows taking the top n elements. 
     */
    static class ScoreSheet
    {
        Map<String, Integer> scoresMap = Maps.newHashMap();

        public void putScore(String item, int score)
        {
            scoresMap.put(item, score);
        }

        public List<String> head(int count)
        {
            // Put elements into an ordered set
            TreeSet<Map.Entry<String, Integer>> highestScores = new TreeSet<Map.Entry<String, Integer>>(new Comparator<Map.Entry<String, Integer>>()
            {
                @Override
                public int compare(Map.Entry<String, Integer> first, Map.Entry<String, Integer> second)
                {
                    return first.getValue() - second.getValue();
                }
            });
            highestScores.addAll(scoresMap.entrySet());

            List<String> topItems = Lists.newArrayList();
            Iterator<Entry<String, Integer>> topScoresIterator = highestScores.iterator();
            int index = 0;
            while (topScoresIterator.hasNext() && index++ < count)
            {
                topItems.add(topScoresIterator.next().getKey());
            }
            return topItems;
        }
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
        if (left.equalsIgnoreCase(right))
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
            if (child.getChildCount() == 0)
            {
                elementNames.add(name);
            }
        }
        return elementNames;
    }

    /**
     * Reads the System Properties starting with 'amps.' and applies them to the configuration.
     * 
     * @throws MojoExecutionException
     */
    public static void applySystemProperties(AbstractProductHandlerMojo mojo, Properties systemProperties) throws MojoExecutionException
    {
        MojoExecutionException lastException = null;
        List<String> messages = Lists.newArrayList();
        for (Object key : systemProperties.stringPropertyNames())
        {
            if (StringUtils.isNotBlank((String) key) && ((String) key).startsWith("amps."))
            {
                try
                {
                    applySystemProperty(mojo, ((String) key).substring(5), systemProperties.getProperty((String) key));
                }
                catch (MojoExecutionException e)
                {
                    messages.add(String.format("-D%s is invalid: %s", key, e.getMessage()));
                    lastException = e;
                }
            }
        }
        
        if (lastException != null)
        {
            throw new MojoExecutionException(StringUtils.join(messages, "\n"), lastException);
        }
    }


    /**
     * Recursively applies the system property to an object.
     * 
     * @param target
     *            the target object on which the value must be set
     * @param key
     *            the name of the field in the format "path.to.field", 'path' being the field of 'target' and 'to' and 'field' the name
     *            of the fields on the underlying object.
     * @param value
     *            the value to set.
     * 
     * @throws MojoExecutionException
     *             if the property couldn't be assigned
     */
    private static void applySystemProperty(Object target, String key, String value) throws MojoExecutionException
    {
        String caseInsensitiveKey = key.toLowerCase(Locale.ENGLISH);
        int firstDotPosition = caseInsensitiveKey.indexOf(".");

        /**
         * True if 'key' is a path to a field of another object,
         * false if 'key' is a field of the current object.
         */
        boolean keyIsAPath = firstDotPosition != -1;
        String head = keyIsAPath ? caseInsensitiveKey.substring(0, firstDotPosition) : caseInsensitiveKey;
        String tail = keyIsAPath ? caseInsensitiveKey.substring(firstDotPosition + 1) : "";
        Field field = findField(target, head);

        if (field != null)
        {
            if (field.isAnnotationPresent(MojoParameter.class) && field.getAnnotation(MojoParameter.class).readonly())
            {
                throw new MojoExecutionException(field.getName() + " can't be assigned");
            }
            field.setAccessible(true);
            if (keyIsAPath)
            {
                assignValue(target, field, tail, value);
            }
            else
            {
                assignValue(target, field, value);
            }
        }
        else
        {
            // There is no such field. If 'target' is an AbstractProductHandlerMojo,
            // then search for an instanceId with this name.
            boolean keyIsAnInstanceName = false;
            if (target instanceof AbstractProductHandlerMojo)
            {
                keyIsAnInstanceName = assignValueToInstance(target, value, head, tail);
                if (!keyIsAnInstanceName)
                {
                    String advice = getAdviceWithClosestNames(head, getAllFieldNames(target.getClass()), ADVICE_COUNT);

                    // The property was neither a name of field neither an instanceId => tell the user
                    throw new MojoExecutionException(String.format("No property '%s' on %s or product with instanceId='%s'. %s",
                            head, target.getClass().getSimpleName(), head, advice));
                }
            }
            else
            {
                String advice = getAdviceWithClosestNames(head, getAllFieldNames(target.getClass()), ADVICE_COUNT);

                // The property was neither a name of field neither an instanceId => tell the user
                throw new MojoExecutionException(String.format("No property '%s' on %s. %s",
                        head, target.getClass().getSimpleName(), advice));
            }
        }
    }

    /**
     * Reflection - Assigns 'value' to the field 'field' of the instance 'target'
     * 
     * @param key
     *            the full name of the key
     */
    private static void assignValue(Object target, Field field, String value) throws MojoExecutionException
    {
        Class<?> clazz = field.getType();
        field.setAccessible(true);
        if (clazz.isAssignableFrom(String.class))
        {
            try
            {
                field.set(target, value);
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
            catch (IllegalAccessException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
        }
        else if (clazz.isAssignableFrom(Integer.TYPE))
        {
            // It's the primitive type, int
            try
            {
                field.set(target, Integer.valueOf(value.toString()));
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
            catch (IllegalAccessException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
        }
        else if (clazz.isAssignableFrom(Boolean.TYPE))
        {
            // It's the primitive type, boolean
            try
            {
                field.set(target, Boolean.valueOf(value.toString()));
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
            catch (IllegalAccessException e)
            {
                throw new MojoExecutionException("Can't set the value " + value + " to " + target);
            }
        }
        else
        {
            // Use the Constructor(String) if exists.
            Constructor<?> ctor;
            try
            {
                ctor = clazz.getConstructor(String.class);
                ctor.setAccessible(true);
                Object fieldValue = ctor.newInstance(value);
                field.set(target, fieldValue);
            }
            catch (SecurityException e)
            {
                throw new MojoExecutionException("Can't construct new " + clazz.getCanonicalName() + "(\"" + value + "\")", e);
            }
            catch (NoSuchMethodException e)
            {
                throw new MojoExecutionException("Can't construct new " + clazz.getCanonicalName() + "(\"" + value + "\")", e);
            }
            catch (InstantiationException e)
            {
                throw new MojoExecutionException("Can't instantiate " + clazz.getCanonicalName() + "(" + value + ")", e);
            }
            catch (IllegalArgumentException e)
            {
                throw new MojoExecutionException("Programming error while assigning the value", e);
            }
            catch (IllegalAccessException e)
            {
                throw new MojoExecutionException("Can't set the field or access the constructor of the field " + field.getName(), e);
            }
            catch (InvocationTargetException e)
            {
                throw new MojoExecutionException("The constructor threw an exception while instantiating " + field.getName(), e);
            }
        }
    }

    private static void assignValue(Object target, Field field, String tail, String value) throws MojoExecutionException
    {
        try
        {
            Object fieldValue = field.get(target);
            if (fieldValue == null)
            {
                fieldValue = field.getType().newInstance();
                field.set(target, fieldValue);
            }
            applySystemProperty(fieldValue, tail, value);
        }
        catch (IllegalArgumentException iae)
        {
            throw new MojoExecutionException("Error when getting/assigning a value to/from " + field.getName() + " on " + target.toString(), iae);
        }
        catch (IllegalAccessException iae)
        {
            // Can only be thrown by newInstance()
            throw new MojoExecutionException("No constructor to instantiate a value for the field " + field.getName() + " on " + target.toString(), iae);
        }
        catch (InstantiationException ie)
        {
            throw new MojoExecutionException("Error when instantiating a value for the field " + field.getName() + " on " + target.toString(), ie);
        }
    }

    private static boolean assignValueToInstance(Object target, String value, String head, String tail) throws MojoExecutionException
    {
        Field productsField = findField(target, "products");
        if (productsField == null)
        {
            throw new MojoExecutionException("Error: there should be a field 'products' on the Mojos");
        }
        productsField.setAccessible(true);
        List<Product> products;
        try
        {
            products = (List<Product>) productsField.get(target);
            Product instance = findProduct(products, head);
            if (instance != null)
            {
                applySystemProperty(instance, tail, value);
                return true;
            }
            return false;
        }
        catch (IllegalArgumentException e)
        {
            throw new MojoExecutionException("Error: the field Mojo.products should be assignable.");
        }
        catch (IllegalAccessException e)
        {
            throw new MojoExecutionException("Error: the field Mojo.products should be assignable.");
        }
    }

    private static Set<String> getAllFieldNames(final Class<?> clazz)
    {
        Set<String> allFieldNames = Sets.newHashSet(Iterables.transform(Iterables.filter(
                Lists.newArrayList(clazz.getDeclaredFields()),
                new Predicate<Field>()
                {
                    @Override
                    public boolean apply(Field field)
                    {
                        MojoParameter annotation = field.getAnnotation(MojoParameter.class);
                        if (annotation != null)
                        {
                            return !annotation.readonly();
                        }
                        // @MojoParameter is only required for mojo-level properties
                        return clazz.isAssignableFrom(AbstractMojo.class) ? false : true;
                    }
                }),
                new Function<Field, String>()
                {
                    @Override
                    public String apply(Field field)
                    {
                        return field.getName();
                    }
                }));
        Class<?> superclass = clazz.getSuperclass();
        if (superclass != null)
        {
            allFieldNames.addAll(getAllFieldNames(superclass));
        }
        return allFieldNames;
    }

    /**
     * Finds the product identified by instanceId in mojo#products.
     * 
     * @return the product if defined, otherwise null.
     */
    private static Product findProduct(List<Product> products, String instanceId)
    {
        Product candidate = null;
        for (Product product : products)
        {
            if (instanceId.equalsIgnoreCase(product.getInstanceId()))
            {
                // Mr. Perfect.
                return product;
            }
            if (candidate == null && product.getId().equalsIgnoreCase(instanceId))
            {
                // instanceId is a product name, so whichever product with this name is candidate
                candidate = product;
            }
        }
        // Return eponymous product, otherwise null
        return candidate;
    }
}