package com.atlassian.maven.plugins.amps.util;

import com.atlassian.maven.plugins.amps.ProductArtifact;
import org.junit.Test;

import java.util.List;

import static com.atlassian.maven.plugins.amps.util.ProductHandlerUtil.toArtifacts;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProductHandlerUtilTest
{
    @Test
    public void toArtifactsShouldReturnEmptyArrayForBlankString() throws Exception
    {
        assertThat(toArtifacts(null).size(), equalTo(0));
        assertThat(toArtifacts(" ").size(), equalTo(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toArtifactsThrowExceptionForIncompleteArtifactPattern()
    {
        toArtifacts("groupId:");
    }

    @Test(expected = IllegalArgumentException.class)
    public void toArtifactsThrowExceptionForInvalidArtifactPattern()
    {
        toArtifacts("groupId:artifactId:version:more");
    }

    @Test
    public void toArtifactsVersionDefaultToLatestIfMissing()
    {
        final ProductArtifact artifact = toArtifacts("groupId:artifactId").get(0);
        assertThat(artifact.getGroupId(), equalTo("groupId"));
        assertThat(artifact.getArtifactId(), equalTo("artifactId"));
        assertThat(artifact.getVersion(), equalTo("LATEST"));
    }

    @Test
    public void toArtifactsShouldReturnMultipleArtifactsFromString()
    {
        final List<ProductArtifact> productArtifacts = toArtifacts("group1:artifact1:11,group2:artifact2:22,group3:artifact3:33");
        assertThat(productArtifacts.size(), equalTo(3));

        assertThat(productArtifacts.get(0).getGroupId(), equalTo("group1"));
        assertThat(productArtifacts.get(0).getArtifactId(), equalTo("artifact1"));
        assertThat(productArtifacts.get(0).getVersion(), equalTo("11"));

        assertThat(productArtifacts.get(1).getGroupId(), equalTo("group2"));
        assertThat(productArtifacts.get(1).getArtifactId(), equalTo("artifact2"));
        assertThat(productArtifacts.get(1).getVersion(), equalTo("22"));

        assertThat(productArtifacts.get(2).getGroupId(), equalTo("group3"));
        assertThat(productArtifacts.get(2).getArtifactId(), equalTo("artifact3"));
        assertThat(productArtifacts.get(2).getVersion(), equalTo("33"));
    }

    /**
     * Just to make helen happy
     */
    @Test
    public void toArtifactsShouldReturnMultipleArtifactsFromStringWithSpacePadding()
    {
        final List<ProductArtifact> productArtifacts = toArtifacts("group1:artifact1:11, group2:artifact2:22 , group3 : artifact3: 33");
        assertThat(productArtifacts.size(), equalTo(3));

        assertThat(productArtifacts.get(0).getGroupId(), equalTo("group1"));
        assertThat(productArtifacts.get(0).getArtifactId(), equalTo("artifact1"));
        assertThat(productArtifacts.get(0).getVersion(), equalTo("11"));

        assertThat(productArtifacts.get(1).getGroupId(), equalTo("group2"));
        assertThat(productArtifacts.get(1).getArtifactId(), equalTo("artifact2"));
        assertThat(productArtifacts.get(1).getVersion(), equalTo("22"));

        assertThat(productArtifacts.get(2).getGroupId(), equalTo("group3"));
        assertThat(productArtifacts.get(2).getArtifactId(), equalTo("artifact3"));
        assertThat(productArtifacts.get(2).getVersion(), equalTo("33"));
    }
}