package com.atlassian.maven.plugins.amps.minifier.strategies.googleclosure;

import com.atlassian.maven.plugins.amps.code.Sources;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;

public class GoogleClosureJsMinifierTest {
    @Mock
    Log log;

    @Test
    public void minification() {
        final HashMap<String, String> closureOptions = new HashMap<>();
        final String code = "var a = 1;"
            + "\n"
            + "\n"
            + "\nif (false) {"
            + "\n    console.log('whoops!');"
            + "\n}"
            + "\nvar b = 2;";
        Sources compiled = GoogleClosureJsMinifier.compile(code, closureOptions, log);
        assertThat(compiled.getContent(), equalTo("var a=1,b=2;"));
        assertThat(compiled.getSourceMapContent(), not(isEmptyString()));
    }
}
