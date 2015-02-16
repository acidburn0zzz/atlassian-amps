package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.fugue.Option;
import com.atlassian.maven.plugins.amps.util.ClassUtils;
import com.atlassian.maven.plugins.amps.util.WiredTestInfo;
import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.PluginXmlRewriter;
import com.atlassian.plugins.codegen.util.ClassnameUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;
import org.junit.Test;

import static com.atlassian.maven.plugins.amps.util.FileUtils.file;


/**
 * Jars the tests into an OSGi bundle.  Only builds the jar if the {@link #buildTestPlugin} flag is set or it detects
 * an atlassian-plugin.xml file in the target/test-classes directory.
 *
 * Note, this test jar will not have its resources filtered or a manifest generated for it.  If no manifest is present,
 * a dummy manifest that does a dynamic package import on everything will be used.
 *
 * @since 3.3
 */
@Mojo(name = "test-jar",requiresDependencyResolution = ResolutionScope.TEST)
public class TestJarMojo extends AbstractAmpsMojo
{

    /**
     * The final name for the test plugin, without the "-tests" suffix.
     */
    @Parameter(property = "project.build.finalName")
    private String finalName;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        MavenProject prj = getMavenContext().getProject();
        File testClassesDir = file(prj.getBuild().getTestOutputDirectory());
        
        if (shouldBuildTestPlugin())
        {
            File mf = file(testClassesDir, "META-INF", "MANIFEST.MF");
            if (!mf.exists())
            {
                try
                {
                    String symbolicName = prj.getGroupId() + "." + prj.getArtifactId() + "-tests";
                    FileUtils.writeStringToFile(mf,
                           "Manifest-Version: 1.0\n" +
                           "Bundle-SymbolicName: " + symbolicName + "\n" +
                           "Bundle-Version: 1.0\n" +
                           "Bundle-Name: " + finalName + "-tests\n" +
                           "DynamicImport-Package: *\n" +
                           "Atlassian-Plugin-Key: " + symbolicName + "\n"
                    );
                }
                catch (IOException e)
                {
                    throw new MojoFailureException("Unable to write manifest");
                }
            }
            
            File pluginXml = file(testClassesDir,"atlassian-plugin.xml");
            File itPackageDir = file(testClassesDir,"it");
            
            if(pluginXml.exists() && itPackageDir.exists())
            {
                PluginProjectChangeset changes = new PluginProjectChangeset();
                
                Collection<File> classFiles = FileUtils.listFiles(itPackageDir, new String[]{"class"}, true);
                try
                {
                    for(File classFile : classFiles)
                    {
                        String className = ClassUtils.getClassnameFromFile(classFile, prj.getBuild().getTestOutputDirectory());
                        WiredTestInfo wiredInfo = ClassUtils.getWiredTestInfo(classFile);
                        if(wiredInfo.isWiredTest())
                        {
                            getLog().info("found Test: " + className + ", adding to plugin.xml...");

                            Map<String,String> serviceProps = new HashMap<String, String>();
                            serviceProps.put("inProductTest","true");
                            
                            String simpleClassname = StringUtils.substringAfterLast(className,".");
                            
                            ComponentDeclaration component = ComponentDeclaration.builder(ClassId.fullyQualified(className),ClassnameUtil.camelCaseToDashed(simpleClassname).toLowerCase())
                                    .interfaceId(Option.some(ClassId.fullyQualified(className)))
                                    .visibility(ComponentDeclaration.Visibility.PUBLIC)
                                    .serviceProperties(serviceProps)
                                    .application(Option.some(wiredInfo.getApplicationFilter()))
                                    .build();
                           
                            changes = changes.with(component);
                        }
                    }

                    new PluginXmlRewriter(pluginXml).applyChanges(changes);
                }
                catch (MalformedURLException e)
                {
                    throw new MojoExecutionException("unable to convert test classes folder to URL",e);
                }
                catch (DocumentException e)
                {
                    throw new MojoExecutionException("unable to modify plugin.xml",e);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("unable to modify plugin.xml",e);
                }
            }
            
            getMavenGoals().jarTests(finalName);
        }
    }

    private boolean hasTestMethod(Class<?> testClass)
    {
        boolean hasTestAnno = false;
        
        Method[] methods = testClass.getMethods();
        for(Method m : methods)
        {
            if(m.isAnnotationPresent(Test.class))
            {
                hasTestAnno = true;
                break;
            }
        }

        return hasTestAnno;
    }

    
}

