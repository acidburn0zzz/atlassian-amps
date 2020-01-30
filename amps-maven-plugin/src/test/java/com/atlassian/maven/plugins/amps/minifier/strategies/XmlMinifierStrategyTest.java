package com.atlassian.maven.plugins.amps.minifier.strategies;

import com.atlassian.maven.plugins.amps.code.Sources;
import com.atlassian.maven.plugins.amps.minifier.MinifierParameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith (MockitoJUnitRunner.class)
public class XmlMinifierStrategyTest {
    @Mock
    MinifierParameters params;

    @Test
    public void removesBadWhitespace() {
        Sources input = new Sources("    <?xml  version=\"1.0\"  encoding=\"UTF-8\" ?>     "
            + "\n"
            + "\n       "
            + "\n       <thing>"
            + "\n    <foo  >     bar    </foo  >"
            + "\n     "
            + "\n"
            + "\n"
            + "   ");
        Sources result = new XmlMinifierStrategy().minify(input, params);
        assertThat(result.getContent(), equalTo("<?xml version=\"1.0\" encoding=\"UTF-8\" ?><thing><foo>     bar    </foo>"));
    }
}
