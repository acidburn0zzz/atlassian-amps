package com.atlassian.maven.plugins.amps.util.minifier;

import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class GoogleClosureJSMinifierTest
{
    @Mock
    Log log;

    @Test
    public void minification()
    {
        GoogleClosureJSMinifier.CompiledSourceWithSourceMap compiled =
            GoogleClosureJSMinifier.compile("var a = 1;" + "\nvar b = 2;", "/path-to-source", new HashMap<>(), log);
        assertThat(compiled.getCompiled(), not(isEmptyString()));
        assertThat(compiled.getSourceMap(), not(isEmptyString()));
    }
}