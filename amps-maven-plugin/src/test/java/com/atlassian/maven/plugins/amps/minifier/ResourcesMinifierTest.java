package com.atlassian.maven.plugins.amps.minifier;

import org.apache.commons.compress.utils.Lists;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResourcesMinifierTest {
    @Test
    public void testPreMinifiedFilesAreCopied() throws Exception {
        Log log = mock(Log.class);
        MinifierParameters params = new MinifierParameters(true, true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        Path dir = Files.createTempDirectory("resources");
        Path out = Files.createTempDirectory("out");

        Files.createTempFile(dir, "test", "myapp1-min.css");
        Files.createTempFile(dir, "test", "myapp2.min.js");
        Files.createTempFile(dir, "test.some.awkward.dots", "myapp3-min.js");
        Files.createTempFile(dir, "test.notminified", "myapp-minnope.js");
        Resource resource = mock(Resource.class);
        when(resource.getDirectory()).thenReturn(dir.toString());
        when(resource.getIncludes()).thenReturn(Lists.newArrayList());
        when(resource.getExcludes()).thenReturn(Lists.newArrayList());

        ResourcesMinifier.minify(Collections.singletonList(resource), out.toString(), params);

        List<String> results = Arrays.stream(out.toFile().listFiles()).map(File::getName).collect(Collectors.toList());
        assertThat(results, hasItems(
                endsWith("myapp1-min.css"),
                endsWith("myapp2-min.js"),
                endsWith("myapp3-min.js"),
                endsWith("myapp-minnope-min.js")
        ));
    }
}
