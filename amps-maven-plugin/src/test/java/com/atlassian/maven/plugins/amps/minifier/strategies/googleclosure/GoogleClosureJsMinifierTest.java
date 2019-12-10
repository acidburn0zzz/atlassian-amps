package com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure;

import com.atlassian.maven.plugins.amps.code.Sources;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class GoogleClosureJsMinifierTest
{
    @Mock
    Log log;

    @Test
    public void minification()
    {
        final HashMap<String, String> closureOptions = new HashMap<>();
        Sources compiled = GoogleClosureJsMinifier.compile("var a = 1;" + "\nvar b = 2;", closureOptions, log);
        assertThat(compiled.getContent(), not(isEmptyString()));
        assertThat(compiled.getSourceMapContent(), not(isEmptyString()));
    }
}
