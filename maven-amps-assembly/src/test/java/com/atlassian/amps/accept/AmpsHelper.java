package com.atlassian.amps.accept;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Enumeration;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;

import junit.framework.Assert;

public class AmpsHelper
{
    public static File setupAmps(File baseDir) throws IOException
    {
        File ampsZip = new File(System.getProperty("amps.zip"));

        baseDir.mkdirs();
        unzip(ampsZip, baseDir);
        return new File(baseDir, ampsZip.getName().substring(0, ampsZip.getName().length() - "-bin.zip".length()));
    }

    public static void runAmpsScript(File ampsHome, File baseDir, String scriptName, String... args)
            throws IOException
    {
        String extension = isWindows() ? ".bat" : "";
        File bin = new File(ampsHome, "bin");

        ExecRunner runner = new ExecRunner();
        File command = new File(bin, scriptName + extension);
        if (!isWindows())
        {
            runner.run(baseDir, Arrays.asList(
                    "/bin/chmod",
                    "755",
                    ampsHome.getAbsolutePath() + "/apache-maven/bin/mvn",
                    command.getAbsolutePath()), Collections.<String, String>emptyMap());
        }
        List<String> cmdlist = new ArrayList<String>(Arrays.asList(args));
        cmdlist.add(0, command.getAbsolutePath());

        Assert.assertEquals(0, runner.run(baseDir, cmdlist, new HashMap<String, String>()
        {{
            put("MAVEN_OPTS", "-Xmx256m");
        }}));
    }

    public static boolean isWindows()
    {
        String myos = System.getProperty("os.name");
        return (myos.toLowerCase(Locale.ENGLISH).indexOf("windows") > -1);
    }

    private static void unzip(File zipfile, File baseDir) throws IOException
    {
        ZipFile zip = new ZipFile(zipfile);
        for (Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) zip.entries(); entries.hasMoreElements();)
        {
            ZipEntry entry = entries.nextElement();
            File target = new File(baseDir, entry.getName());
            if (entry.isDirectory())
            {
                target.mkdirs();
            }
            else
            {
                IOUtils.copy(zip.getInputStream(entry), new FileOutputStream(target));
            }
        }
    }

}
