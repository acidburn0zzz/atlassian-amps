package com.atlassian.maven.plugins.amps.minifier;

import org.apache.commons.compress.utils.Lists;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.logging.Log;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith (MockitoJUnitRunner.class)
public class ResourcesMinifierTest {
    @Mock
    private Log log;
    @Rule
    public TemporaryFolder dir = new TemporaryFolder();
    @Rule
    public TemporaryFolder out = new TemporaryFolder();

    // I know this is not the best behaviour, but alas, it's how it has always been...
    @Test
    public void testXmlGetsMinifiedInPlace() throws Exception {
        MinifierParameters params = new MinifierParameters(true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        dir.newFile("myapp1.xml");
        Resource resource = getBaseResourceMock();

        new ResourcesMinifier(params).minify(resource, out.getRoot().toString());

        List<String> results = getFilesystemFor(out);
        assertThat(results, not(hasItems(endsWith("myapp1-min.xml"))));
        assertThat(results, hasItems(endsWith("myapp1.xml")));
    }

    @Test
    public void testCssGetsMinified() throws Exception {
        MinifierParameters params = new MinifierParameters(true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        dir.newFile("myapp1.css");
        Resource resource = getBaseResourceMock();

        new ResourcesMinifier(params).minify(resource, out.getRoot().toString());

        List<String> results = getFilesystemFor(out);
        assertThat(results, hasItems(endsWith("myapp1-min.css")));
        assertThat(results, not(hasItems(endsWith("myapp1.css"))));
    }

    @Test
    public void testJsGetsMinified() throws Exception {
        MinifierParameters params = new MinifierParameters(true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        dir.newFile("myapp1.js");
        Resource resource = getBaseResourceMock();

        new ResourcesMinifier(params).minify(resource, out.getRoot().toString());

        List<String> results = getFilesystemFor(out);
        assertThat(results, hasItems(endsWith("myapp1-min.js")));
        assertThat(results, not(hasItems(endsWith("myapp1.js"))));
    }

    @Test
    public void testPreMinifiedFilesAreCopied() throws Exception {
        MinifierParameters params = new MinifierParameters(true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        dir.newFile("test" + "myapp1-min.css");
        dir.newFile("test" + "myapp2.min.js");
        dir.newFile("test.some.awkward.dots" + "myapp3-min.js");
        dir.newFile("test.notminified" + "myapp-minnope.js");

        Resource resource = getBaseResourceMock();
        new ResourcesMinifier(params).minify(resource, out.getRoot().toString());

        List<String> results = getFilesystemFor(out);
        assertThat(results, hasItems(
            endsWith("myapp1-min.css"),
            endsWith("myapp2-min.js"),
            endsWith("myapp3-min.js"),
            endsWith("myapp-minnope-min.js")
        ));
    }

    @Test
    public void testDirectoryStructurePreservedInOutput() throws Exception {
        MinifierParameters params = new MinifierParameters(true, true, StandardCharsets.UTF_8, log, new HashMap<>());

        File subdir1 = dir.newFolder("subfolder");
        File subdir2 = dir.newFolder("deep", "nested", "folder");

        new File(dir.getRoot(), "main.js").createNewFile();
        new File(subdir1, "sub-feature.js").createNewFile();
        new File(subdir2, "deep-feature.js").createNewFile();

        Resource resource = getBaseResourceMock();
        new ResourcesMinifier(params).minify(resource, out.getRoot().toString());

        List<String> results = getFilesystemFor(out);
        assertThat(results, hasItems(
            endsWith("main-min.js"),
            endsWith("subfolder/sub-feature-min.js"),
            endsWith("deep/nested/folder/deep-feature-min.js")
        ));
    }

    private Resource getBaseResourceMock() {
        Resource resource = mock(Resource.class);
        when(resource.getDirectory()).thenReturn(dir.getRoot().toString());
        when(resource.getIncludes()).thenReturn(Lists.newArrayList());
        when(resource.getExcludes()).thenReturn(Lists.newArrayList());
        return resource;
    }

    private List<String> getFilesystemFor(TemporaryFolder out) throws IOException {
        return Files.walk(out.getRoot().toPath())
            .map(String::valueOf)
            .sorted()
            .collect(Collectors.toList());
    }
}
