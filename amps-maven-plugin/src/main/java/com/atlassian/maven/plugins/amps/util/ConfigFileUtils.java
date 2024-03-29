package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import com.google.common.base.Preconditions;

public class ConfigFileUtils
{
    public static void replace(List<File> files, List<Replacement> replacements, boolean inverted, Log log) throws MojoExecutionException
    {
        for (File file : files)
        {
            replace(file, replacements, inverted, log);
        }
    }

    /**
     * @param cfgFile the file
     * @param replacements the list of keys to replace with values
     * @param inverted if you want to swap values with keys. Be aware that the list is processed in order,
     * so that if 2 keys have the same value, the first key will be chosen. The Replacement records with
     * reversible set to false will not be reversed. Default: false.
     */
    public static void replace(File cfgFile, List<Replacement> replacements, boolean inverted, Log log) throws MojoExecutionException
    {
        if (!cfgFile.exists())
        {
            return;
        }
        try
        {
            String config = FileUtils.readFileToString(cfgFile, StandardCharsets.UTF_8);
            if (!inverted)
            {
                for (Replacement replacement : replacements)
                {
                    if (replacement.applyWhenUnzipping())
                    {
                        config = replacement.replace(config);
                    }
                }
            }
            else
            {
                for (Replacement replacement : replacements)
                {
                    config = replacement.reverse(config);
                }
            }
            FileUtils.writeStringToFile(cfgFile, config, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to replace " + cfgFile, ex);
        }
    }

    public static void replaceAll(File cfgFile, String pattern, String replacement) throws MojoExecutionException
    {
        if (!cfgFile.exists())
        {
            return;
        }
        try
        {
            String config = FileUtils.readFileToString(cfgFile, StandardCharsets.UTF_8);
            config = config.replaceAll(pattern, replacement); // obeys regex
            FileUtils.writeStringToFile(cfgFile, config, StandardCharsets.UTF_8);
        }
        catch (IOException ex)
        {
            throw new MojoExecutionException("Unable to replace " + cfgFile, ex);
        }
    }

    public static void setProperties(File propertiesFile, Map<String, String> newProperties)
    {
        try (InputStream in = new FileInputStream(propertiesFile))
        {
            Properties props = new Properties();
            props.load(in);
            in.close();

            newProperties.forEach(props::setProperty);

            try (OutputStream out = new FileOutputStream(propertiesFile))
            {
                props.store(out, "Processed by AMPS");
            }
        }
        catch (IOException ignored)
        {

        }
    }

    /**
     * Represents a replacement in a configuration file or set of files.
     */
    public static class Replacement implements Comparable<Replacement>
    {
        private final String key;
        private final String value;

        /**
         * Replace the key with the value when unzipping a home. This is the normal meaning of
         * a replacement, {@code key -> value}
         */
        private final boolean applyWhenUnzipping;

        /**
         * Detect the value and replace it with the key when zipping a home directory
         */
        private final boolean reversible;

        /**
         * Represents a key to be replaced in the configuration files.
         *
         * <p/>
         * <b>Important</b>: If your value is short, such as "/", "", "true", "false", please set reversible=false.
         * When zipping a home, config files are parsed and everything is replaced back with keys, such as %PRODUCT_HOME_DIR%.
         * If you provide a string with false positives, you may parametrise too many variables.
         *
         * @param key the key to be replaced. Must not be null.
         * @param value the value to be replaced. Must not be null. <b>Important</b>: If short, such as / or "", please set reversible=false.
         */
        public Replacement(String key, String value)
        {
            this(key, value, true);
        }

        /**
         * Represents a key to be replaced in the configuration files.
         *
         * @param key the key to be replaced. Must not be null.
         * @param value the value to be replaced. Must not be null.
         * @param reversible true if the value should be replaced with the key before
         * preparing a snapshot. Default is true. Use false when:<ul>
         * <li>the value is non-unique, e.g. "%BAMBOO_ENABLED% = true" should not be reversible.</li>
         * <li>we only support the value for legacy, but we wouldn't re-wrap a snapshot with this key</li>
         * </ul>
         */
        public Replacement(String key, String value, boolean reversible)
        {
            this(key, value, true, reversible);
        }

        /**
         * @param key the key, never null
         * @param value the value, never null
         * @param applyWhenUnzipping apply when unzipping a home. Defaults to true.
         * @param applyWhenZipping apply when zipping a home. Defaults to true.
         */
        Replacement(String key, String value, boolean applyWhenUnzipping, boolean applyWhenZipping)
        {
            Preconditions.checkArgument(key != null, "key must not be null");
            Preconditions.checkArgument(value != null, "value must not be null");
            this.key = key;
            this.value = value;
            this.applyWhenUnzipping = applyWhenUnzipping;
            this.reversible = applyWhenZipping;
        }

        public static Replacement onlyWhenCreatingSnapshot(String key, String value)
        {
            return new Replacement(key, value, true, false);
        }

        public final String replace(String s)
        {
            return replace(s, key, value);
        }

        public final String reverse(String s)
        {
            return reversible ? replace(s, value, key) : s;
        }

        protected String replace(String s, String target, String replacement)
        {
            return s.replace(target, replacement);
        }

        public final boolean applyWhenUnzipping()
        {
            return applyWhenUnzipping;
        }

        @Override
        public int compareTo(Replacement other)
        {
            int length1 = this.value.length();
            int length2 = other.value.length();
            return length2 - length1;
        }

        @Override
        public String toString()
        {
            String operation;

            if (applyWhenUnzipping && reversible)
            {
                operation = " <-> ";
            }
            else if (applyWhenUnzipping && !reversible)
            {
                operation = " -> ";
            }
            else if (!applyWhenUnzipping && reversible)
            {
                operation = " <- ";
            }
            else // !applyWhenUnzipping && !reversible
            {
                operation = " (nop) ";
            }

            return key + operation + value;
        }
    }

    public static final class RegexReplacement extends Replacement
    {
        public RegexReplacement(String key, String value)
        {
            super(key, value, false);
        }

        @Override
        protected String replace(String s, String target, String replacement)
        {
            return s.replaceAll(target, replacement);
        }
    }
}
