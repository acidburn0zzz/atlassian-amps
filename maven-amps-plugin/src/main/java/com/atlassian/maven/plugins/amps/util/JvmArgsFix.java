package com.atlassian.maven.plugins.amps.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @since 3.8
 */
public class JvmArgsFix
{
    private final Map<String, String> defaultParams;

    private JvmArgsFix(Map<String, String> initial)
    {
        defaultParams = Maps.newLinkedHashMap(initial);
    }

    private JvmArgsFix()
    {
        defaultParams = Maps.newLinkedHashMap();
    }

    public static JvmArgsFix defaults()
    {
        return new JvmArgsFix(ImmutableMap.<String, String>of("-Xmx", "512m", "-XX:MaxPermSize=", "256m"));
    }

    public static JvmArgsFix empty()
    {
        return new JvmArgsFix();
    }

    public JvmArgsFix with(String param, String value)
    {
        defaultParams.put(param, value);
        return this;
    }

    public String apply(String jvmArgs)
    {
        final List<String> args = StringUtils.isNotBlank(jvmArgs) ? Lists.newArrayList(jvmArgs) : Lists.<String>newArrayList();

        for (Map.Entry<String, String> param : defaultParams.entrySet())
        {
            if (!StringUtils.contains(jvmArgs, param.getKey()))
            {
                args.add(param.getKey() + param.getValue());
            }
        }

        return StringUtils.join(args, ' ');
    }
}
