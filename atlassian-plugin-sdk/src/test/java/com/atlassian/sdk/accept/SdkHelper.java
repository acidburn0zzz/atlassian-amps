package com.atlassian.sdk.accept;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.atlassian.maven.plugins.amps.util.ZipUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import junit.framework.Assert;

public class SdkHelper
{
    public static File setupSdk(File baseDir) throws IOException
    {
        File sdkZip = isWindows() ? new File(System.getProperty("sdk.zip")) : new File(System.getProperty("sdk.tar"));
        String extension = isWindows() ? ".zip" : ".tar.gz";
        
        baseDir.mkdirs();
        unzip(sdkZip, baseDir);

        return new File(baseDir, StringUtils.substringBefore(sdkZip.getName(),extension));
    }

    public static void runSdkScript(File sdkHome, File baseDir, String scriptName, String... args)
            throws IOException, InterruptedException
    {
        String extension = isWindows() ? ".bat" : "";
        File bin = new File(sdkHome, "bin");

        ExecRunner runner = new ExecRunner();
        File command = new File(bin, scriptName + extension);

        if (!isWindows())
        {
            runner.run(baseDir, Arrays.asList(
                    "/bin/chmod",
                    "755",
                    sdkHome.getAbsolutePath() + "/apache-maven/bin/mvn",
                    command.getAbsolutePath()), Collections.<String, String>emptyMap());
        }
        List<String> cmdlist = new ArrayList<String>(Arrays.asList(args));
        cmdlist.add("-Dallow.google.tracking=false");
        cmdlist.add(0, command.getAbsolutePath());
        cmdlist.add("-s");
        cmdlist.add(file(sdkHome, "apache-maven", "conf", "settings.xml").getPath());

        Assert.assertEquals(0, runner.run(baseDir, cmdlist, new HashMap<String, String>()
        {{
                put("MAVEN_OPTS", "-Xmx256m");
                put("JAVA_HOME", System.getProperty("java.home"));
                put("PATH", System.getenv("PATH"));
            }}));
    }

    public static boolean isWindows()
    {
        String myos = System.getProperty("os.name");
        return (myos.toLowerCase(Locale.ENGLISH).indexOf("windows") > -1);
    }

    private static void unzip(File zipfile, File baseDir) throws IOException
    {
        if (FilenameUtils.isExtension(zipfile.getName(), "zip"))
        {
            ZipUtils.unzip(zipfile, baseDir.getAbsolutePath());
        }
        else
        {
            ZipUtils.untargz(zipfile, baseDir.getAbsolutePath());
        }
    }

    public static File file(File parent, String... kids)
    {
        File cur = parent;
        for (String kid : kids)
        {
            cur = new File(cur, kid);
        }
        return cur;
    }

    public static File createPlugin(String productId, File baseDir, File sdkHome, String prefix) throws IOException, InterruptedException
    {
        final String artifactId = prefix + "-" + productId + "-plugin";
        final File appDir = new File(baseDir, artifactId);
        FileUtils.deleteDirectory(appDir);

        runSdkScript(sdkHome, baseDir, "atlas-create-" + productId + "-plugin",
                "-a", artifactId,
                "-g", "com.example",
                "-p", "com.example.foo",
                "-v", "1.0-SNAPSHOT",
                "--non-interactive");

        Assert.assertTrue(appDir.exists());
        return appDir;
    }
}
