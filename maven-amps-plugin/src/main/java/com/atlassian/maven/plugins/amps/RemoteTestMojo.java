package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.atlassian.maven.plugins.amps.util.ClassUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.ManifestException;

@Mojo(name = "remote-test", requiresDependencyResolution = ResolutionScope.TEST, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
@Execute(phase = LifecyclePhase.PACKAGE)
public class RemoteTestMojo extends AbstractProductHandlerMojo
{

    /**
     * The Jar archiver.
     */
    @Component(role = Archiver.class, hint = "jar")
    private JarArchiver jarArchiver;

    /**
     * The archive configuration to use. See <a href="http://maven.apache.org/shared/maven-archiver/index.html">Maven
     * Archiver Reference</a>.
     */
    @Parameter
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * Pattern for to use to find integration tests.  Only used if no test groups are defined.
     */
    @Parameter(property = "functional.test.pattern")
    private String functionalTestPattern = "it/**";

    /**
     * The directory containing generated test classes of the project being tested.
     */
    @Parameter(property = "project.build.testOutputDirectory", required = true)
    private File testClassesDirectory;

    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    @Parameter(property = "maven.test.skip", defaultValue = "false")
    private boolean testsSkip = false;

    @Parameter(property = "skipTests", defaultValue = "false")
    private boolean skipTests = false;

    /**
     * Skip the integration tests along with any product startups
     */
    @Parameter(property = "skipITs", defaultValue = "false")
    private boolean skipITs = false;

    /**
     * HTTP port for the servlet containers
     */
    @Parameter(property = "http.port", defaultValue = "80")
    protected int httpPort;

    /**
     * Username of user that will install the plugin
     */
    @Parameter(property = "pdk.username", defaultValue = "admin")
    protected String pdkUsername;

    /**
     * Password of user that will install the plugin
     */
    @Parameter(property = "pdk.password", defaultValue = "admin")
    protected String pdkPassword;

    protected void doExecute() throws MojoExecutionException
    {
        if (StringUtils.isBlank(server))
        {
            getLog().error("server is not set!");
            return;
        }

        if (null == contextPath || StringUtils.trim(contextPath).equals("/"))
        {
            contextPath = "";
        }
        
        if(httpPort < 1)
        {
            httpPort = 80;
        }

        if (!shouldBuildTestPlugin())
        {
            getLog().info("shouldBuildTestPlugin is false... skipping test run!");
            return;
        }

        final MavenProject project = getMavenContext().getProject();

        // workaround for MNG-1682/MNG-2426: force maven to install artifact using the "jar" handler
        project.getArtifact().setArtifactHandler(artifactHandlerManager.getArtifactHandler("jar"));

        if (!new File(testClassesDirectory, "it").exists())
        {
            getLog().info("No integration tests found");
            return;
        }

        if (skipTests || testsSkip || skipITs)
        {
            getLog().info("Integration tests skipped");
            return;
        }

        final MavenGoals goals = getMavenGoals();
        final String pluginJar = targetDirectory.getAbsolutePath() + "/" + finalName + ".jar";

        runTestsForTestGroup(NO_TEST_GROUP, goals, pluginJar, copy(systemPropertyVariables));

    }

    private Map<String, Object> copy(Map<String, Object> systemPropertyVariables)
    {
        return new HashMap<String, Object>(systemPropertyVariables);
    }

    private String getBaseUrl(String server, int actualHttpPort, String contextPath)
    {
        String port = actualHttpPort != 80 ? ":" + actualHttpPort : "";
        server = server.startsWith("http") ? server : "http://" + server;
        if (!contextPath.startsWith("/") && StringUtils.isNotBlank(contextPath))
        {
            contextPath = "/" + contextPath;
        }
        return server + port + contextPath;
    }

    private void runTestsForTestGroup(String testGroupId, MavenGoals goals, String pluginJar, Map<String, Object> systemProperties) throws MojoExecutionException
    {
        try
        {
            MavenContext ctx = getMavenContext();
            Build build = ctx.getProject().getBuild();
            File buildDir = new File(build.getDirectory());
            File testClassesDir = new File(build.getTestOutputDirectory());

            List<String> wiredTestClasses = getWiredTestClassnames(testClassesDir);
            
            if(wiredTestClasses.isEmpty())
            {
                getLog().info("No wired integration tests found, skipping remote testing...");
                return;
            }
            
            List<String> includes = new ArrayList<String>(wiredTestClasses.size());
            for(String wiredClass : wiredTestClasses)
            {
                String includePath = wiredClass.replaceAll("\\.", "/");
                includes.add(includePath + "*");
            }
            
            List<String> excludes = Collections.emptyList();

            systemProperties.put("http.port", httpPort);
            systemProperties.put("context.path", contextPath);
            systemProperties.put("plugin.jar", pluginJar);

            // yes, this means you only get one base url if multiple products, but that is what selenium would expect
            if (!systemProperties.containsKey("baseurl"))
            {
                systemProperties.put("baseurl", getBaseUrl(server, httpPort, contextPath));
            }

            List<File> obrFiles = getFrameworkFiles();
            File junit = null;
            
            //we MUST install junit first!
            for(File obrFile : obrFiles)
            {
                if(obrFile.getName().startsWith("com.springsource.org.junit"))
                {
                    junit = obrFile;
                    obrFiles.remove(obrFile);
                    break;
                }
            }
            
            File mainPlugin = new File(buildDir, finalName + ".jar");
            File testPlugin = new File(buildDir, finalName + "-tests.jar");

            obrFiles.add(mainPlugin);
            obrFiles.add(testPlugin);
            
            if(null == junit || !junit.exists())
            {
                throw new MojoExecutionException("couldn't find junit!!!!");
            }

            installPluginFile(junit);
            
            for(File pluginFile : obrFiles)
            {
                installPluginFile(pluginFile);
            }

            // Actually run the tests
            goals.runIntegrationTests("group-" + testGroupId, "remote", includes, excludes, systemProperties, targetDirectory);
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error running remote tests...", e);
        }

    }

    private void installPluginFile(File pluginFile) throws MojoExecutionException
    {
        getLog().info("trying to install plugin with the following properties:");
        getLog().info("pluginFile: " + pluginFile.getAbsolutePath());
        getLog().info("pluginKey: " + pluginFile.getName());
        getLog().info("server: " + server);
        getLog().info("httpPort: " + httpPort);
        getLog().info("contextPath: " + contextPath);
        getLog().info("username: " + pdkUsername);

        getMavenGoals().installPlugin(new PdkParams.Builder()
                .pluginFile(pluginFile.getAbsolutePath())
                .pluginKey(pluginFile.getName())
                .server(server)
                .port(httpPort)
                .contextPath(contextPath)
                .username(pdkUsername)
                .password(pdkPassword)
                .build());
    }

    private List<String> getWiredTestClassnames(File testClassesDir) throws Exception
    {
        MavenProject prj = getMavenContext().getProject();
        List<String> wiredClasses = new ArrayList<String>();
        
        if (testClassesDir.exists())
        {
            Collection<File> classFiles = FileUtils.listFiles(testClassesDir, new String[]{"class"}, true);

            for (File classFile : classFiles)
            {
                String className = ClassUtils.getClassnameFromFile(classFile, prj.getBuild().getTestOutputDirectory());
                if (ClassUtils.isWiredPluginTestClass(classFile))
                {
                    wiredClasses.add(className);
                }
            }

        }

        return wiredClasses;
    }

    private List<File> getFrameworkFiles() throws MojoExecutionException
    {
        try
        {
            File tmpDir = new File(getMavenContext().getProject().getBuild().getDirectory(), "tmp-artifacts");
            FileUtils.forceMkdir(tmpDir);
            FileUtils.cleanDirectory(tmpDir);

            getMavenGoals().copyPlugins(tmpDir, testFrameworkPlugins);

            return new ArrayList<File>(Arrays.asList(tmpDir.listFiles()));
            
        }
        catch (Exception e)
        {
            throw new MojoExecutionException("Error copying framework files", e);
        }
    }

    private File generateObrZip(File obrDir, File outputDirectory, String obrName) throws MojoExecutionException
    {
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        File outputFile = new File(outputDirectory, obrName + ".obr");
        final MavenProject mavenProject = getMavenContext().getProject();
        try
        {
            archiver.getArchiver().addDirectory(obrDir, "");
            archiver.setOutputFile(outputFile);

            archive.setAddMavenDescriptor(false);

            // todo: be smarter about when this is updated
            archive.setForced(true);

            archiver.createArchive(mavenProject, archive);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (ArchiverException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (DependencyResolutionRequiredException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }
        catch (ManifestException e)
        {
            throw new MojoExecutionException("Error creating obr archive: " + e.getMessage(), e);
        }

        return outputFile;

    }

    /**
     * Creates a directory containing the files that will be in the obr artifact.
     *
     * @param deps         The dependencies for this artifact
     * @param mainArtifact The main artifact file
     * @return The directory containing the future obr zip contents
     * @throws IOException            If the files cannot be copied
     * @throws MojoExecutionException If the dependencies cannot be retrieved
     */
    private File layoutObr(List<File> deps, File mainArtifact) throws MojoExecutionException, IOException
    {
        // create directories
        File obrDir = new File(getMavenContext().getProject().getBuild().getDirectory(), "obr");
        obrDir.mkdir();
        File depDir = new File(obrDir, "dependencies");
        depDir.mkdir();

        // Copy in the dependency plugins for the obr generation
        for (File dep : deps)
        {
            FileUtils.copyFileToDirectory(dep, depDir, true);
        }

        // Generate the obr xml
        File obrXml = new File(obrDir, "obr.xml");
        for (File dep : depDir.listFiles())
        {
            getMavenGoals().generateObrXml(dep, obrXml);
        }

        // Copy the main artifact over
        File mainArtifactCopy = new File(obrDir, mainArtifact.getName());
        FileUtils.copyFile(mainArtifact, mainArtifactCopy);

        // Generate the obr xml for the main artifact
        // The File must be the one copied into the obrDir (see AMPS-300)
        getMavenGoals().generateObrXml(mainArtifactCopy, obrXml);

        return obrDir;
    }

}
