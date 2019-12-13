package com.atlassian.maven.plugins.amps.util.minifier;

import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.parsing.parser.FeatureSet;
import org.apache.maven.plugin.logging.Log;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GoogleClosureOptionsHandlerTest {
    @Mock
    private Log log;

    private GoogleClosureOptionsHandler opts;

    @Before
    public void setup() {
        opts = new GoogleClosureOptionsHandler(log);
    }

    @Test
    public void hasSensibleDefaults() {
        CompilerOptions results = opts.getCompilerOptions();

        assertThat(results.getLanguageIn(), equalTo(CompilerOptions.LanguageMode.ECMASCRIPT_2018));
        assertThat(results.shouldEmitUseStrict(), equalTo(true));
    }

    @Test
    public void canSetLanguageIn() {
        opts.setOption("languageIn", "ECMASCRIPT3");
        CompilerOptions results = opts.getCompilerOptions();

        assertThat(results.getLanguageIn(), equalTo(CompilerOptions.LanguageMode.ECMASCRIPT3));
    }

    @Test
    public void canSetLanguageOut() {
        opts.setOption("languageOut", "ECMASCRIPT3");
        CompilerOptions results = opts.getCompilerOptions();

        assertThat(results.getOutputFeatureSet(), equalTo(FeatureSet.ES3));
    }

    @Test
    public void canSetUseStrict() {
        opts.setOption("emitUseStrict", "false");
        CompilerOptions results = opts.getCompilerOptions();

        assertThat(results.shouldEmitUseStrict(), equalTo(false));
    }

    @Test
    public void canSetOptionsCaseInsensitive() {
        opts.setOption("LANGUAGEin", "ECMASCRIPT3");
        opts.setOption("lAnGuAgEoUt", "ECMASCRIPT3");
        opts.setOption("emitusestrict", "FALSE");
        CompilerOptions results = opts.getCompilerOptions();

        assertThat(results.getLanguageIn(), equalTo(CompilerOptions.LanguageMode.ECMASCRIPT3));
        assertThat(results.getOutputFeatureSet(), equalTo(FeatureSet.ES3));
        assertThat(results.shouldEmitUseStrict(), equalTo(false));
    }

    @Test
    public void ignoresUnknownOptions() {
        opts.setOption("nope", "foo");

        verify(log).warn("nope is not configurable. Ignoring this option.");
    }

}
