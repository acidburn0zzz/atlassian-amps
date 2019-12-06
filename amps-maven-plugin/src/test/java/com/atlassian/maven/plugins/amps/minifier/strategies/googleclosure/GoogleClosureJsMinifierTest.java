package com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure;

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
        GoogleClosureJsMinifier.CompiledSourceWithSourceMap compiled =
            GoogleClosureJsMinifier.compile("var a = 1;" + "\nvar b = 2;", "/path-to-source", new HashMap<>(), log);
        assertThat(compiled.getCompiled(), not(isEmptyString()));
        assertThat(compiled.getSourceMap(), not(isEmptyString()));
    }
}
