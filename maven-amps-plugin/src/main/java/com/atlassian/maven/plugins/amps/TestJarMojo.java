package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import com.atlassian.fugue.Option;
import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.ComponentDeclaration;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.PluginXmlRewriter;
import com.atlassian.plugins.codegen.util.ClassnameUtil;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;

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
                    FileUtils.writeStringToFile(mf,
                           "Manifest-Version: 1.0\n" +
                           "Bundle-SymbolicName: plugin-tests\n" +
                           "Bundle-Version: 1.0\n" +
                           "Bundle-Name: " + finalName + "-tests\n" +
                           "DynamicImport-Package: *\n");
                }
                catch (IOException e)
                {
                    throw new MojoFailureException("Unable to write manifest");
                }
            }
            
            File pluginXml = file(testClassesDir,"atlassian-plugin.xml");
            //File itPackageDir = file(testClassesDir,"it");
            
            if(pluginXml.exists() && testClassesDir.exists())
            {
                PluginProjectChangeset changes = new PluginProjectChangeset();
                
                Collection<File> classFiles = FileUtils.listFiles(testClassesDir, new String[]{"class"}, true);
                try
                {
                    Set<String> classpathPaths = new HashSet<String>();
                    classpathPaths.add(testClassesDir.getPath());
                    classpathPaths.addAll(prj.getCompileClasspathElements());
                    classpathPaths.addAll(prj.getRuntimeClasspathElements());
                    classpathPaths.addAll(prj.getSystemClasspathElements());
                    
                    Set<URL> classpathUrlSet = new HashSet<URL>(classpathPaths.size());
                    for(String path : classpathPaths)
                    {
                        File cpFile = new File(path);
                        if(cpFile.exists())
                        {
                            classpathUrlSet.add(cpFile.toURI().toURL());
                        }
                    }

                    URL[] classpathUrls = classpathUrlSet.toArray(new URL[0]);
                    URLClassLoader ucl = new URLClassLoader(classpathUrls,this.getClass().getClassLoader());
                    
                    for(File classFile : classFiles)
                    {
                        String className = getClassnameFromFile(classFile,prj.getBuild().getTestOutputDirectory());
                        Class itClass = ucl.loadClass(className);
                        if(isPluginTest(itClass))
                        {
                            getLog().info("found Test: " + itClass.getName() + ", adding to plugin.xml...");

                            Map<String,String> serviceProps = new HashMap<String, String>();
                            serviceProps.put("inProductTest","true");

                            ComponentDeclaration component = ComponentDeclaration.builder(ClassId.fullyQualified(className),ClassnameUtil.camelCaseToDashed(itClass.getSimpleName()).toLowerCase())
                                    .interfaceId(Option.some(ClassId.fullyQualified(className)))
                                    .visibility(ComponentDeclaration.Visibility.PUBLIC)
                                    .serviceProperties(serviceProps)
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
                catch (ClassNotFoundException e)
                {
                    throw new MojoExecutionException("unable to load test class",e);
                }
                catch (DocumentException e)
                {
                    throw new MojoExecutionException("unable to modify plugin.xml",e);
                }
                catch (IOException e)
                {
                    throw new MojoExecutionException("unable to modify plugin.xml",e);
                }
                catch (DependencyResolutionRequiredException e)
                {
                    throw new MojoExecutionException("unable to build test jar classpath",e);
                }

            }
            
            getMavenGoals().jarTests(finalName);
        }
    }

    private boolean isPluginTest(Class testClass)
    {
        Annotation[] annos = testClass.getAnnotations();

        for(Annotation anno : annos)
        {

            if(anno.annotationType().equals(RunWith.class) && ((RunWith)anno).value().equals(AtlassianPluginsTestRunner.class))
            {
                return true;
            }
        }
        return false;
        
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

    private String getClassnameFromFile(File classFile, String removePrefix)
    {
        return StringUtils.removeEnd(
                StringUtils.removeStart(
                        StringUtils.removeStart(
                                classFile.getAbsolutePath(), removePrefix)
                                   .replaceAll(File.separator, ".")
                        , ".")
                , ".class");
    }
}

