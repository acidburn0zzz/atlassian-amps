package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ProjectRewriter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.XmlStreamWriter;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.shade.pom.PomWriter;
import org.apache.maven.project.MavenProject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Applies any changes from a {@link PluginProjectChangeset} that affect the POM of a Maven project.
 * These include dependencies and bundle instructions.  (Bundle instructions are not yet implemented)
 */
public class MavenProjectRewriter implements ProjectRewriter
{
    private final MavenProject project;
    private final Log log;
    
    public MavenProjectRewriter(MavenProject project, Log log)
    {
        this.project = checkNotNull(project, "project");
        this.log = checkNotNull(log, "log");
    }
    
    @Override
    public void applyChanges(PluginProjectChangeset changes) throws Exception
    {
        Iterable<ArtifactDependency> descriptors = changes.getDependencies();
        boolean modifyPom = false;
        List<Dependency> originalDependencies = project.getModel().getDependencies();
        for (ArtifactDependency descriptor : descriptors)
        {
            Dependency alreadyExisting = (Dependency) CollectionUtils.find(originalDependencies,
                                                                           new DependencyPredicate(descriptor));
            if (null == alreadyExisting)
            {
                modifyPom = true;

                Dependency newDependency = new Dependency();
                newDependency.setGroupId(descriptor.getGroupId());
                newDependency.setArtifactId(descriptor.getArtifactId());
                newDependency.setVersion(descriptor.getVersion());
                newDependency.setScope(descriptor.getScope().name().toLowerCase());

                project.getOriginalModel()
                        .addDependency(newDependency);
            }
        }

        if (modifyPom)
        {
            File pom = project.getFile();
            XmlStreamWriter writer = null;
            try
            {
                writer = new XmlStreamWriter(pom);
                PomWriter.write(writer, project.getOriginalModel(), true);
            }
            catch (IOException e)
            {
                log.warn("Unable to write plugin-module dependencies to pom.xml", e);
            }
            finally
            {
                if (writer != null)
                {
                    IOUtils.closeQuietly(writer);
                }
            }
        }
    }

    private static class DependencyPredicate implements Predicate
    {
        private ArtifactDependency depToCheck;
    
        private DependencyPredicate(ArtifactDependency depToCheck)
        {
            this.depToCheck = depToCheck;
        }
    
        @Override
        public boolean evaluate(Object o)
        {
            Dependency d = (Dependency) o;
            return (depToCheck.getGroupId()
                    .equals(d.getGroupId())
                    && depToCheck.getArtifactId()
                    .equals(d.getArtifactId()));
        }
    }
}
