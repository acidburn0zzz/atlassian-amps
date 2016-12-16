package com.atlassian.maven.plugins.amps;

import com.atlassian.maven.plugins.amps.product.AbstractProductHandler;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.AmpsCreatePluginPrompter;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Creates a new plugin
 */
@Mojo(name = "create", requiresProject = false)
public class CreateMojo extends AbstractProductHandlerMojo
{
    @Component
    private AmpsCreatePluginPrompter ampsCreatePluginPrompter;

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException
    {
        trackFirstRunIfNeeded();

        getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN);


        // this is the name of a product (refapp, jira, confluence, etc)
        String pid = getProductId();
        String stableVersion, stableDataVersion;

        // first try to get manual version
        Application a = getManualVersion(pid);
        if (a != null) {
            getLog().info("using latest stable product version: " + a.latest);
            getLog().info("using latest stable data version: " + a.data);
            stableVersion = a.latest;
            stableDataVersion = a.data;
        } else {
            // use the old way (grab version from artifact)
            Product ctx = getProductContexts().get(pid);
            AbstractProductHandler handler = createProductHandler(pid);
            getLog().info("determining latest stable product version...");
            stableVersion = getStableProductVersion(handler,ctx);
            if (StringUtils.isNotBlank(stableVersion))
            {
                getLog().info("using latest stable product version: " + stableVersion);
            }

            getLog().info("determining latest stable data version...");
            stableDataVersion = getStableDataVersion(handler,ctx);
            if (StringUtils.isNotBlank(stableDataVersion))
            {
                getLog().info("using latest stable data version: " + stableDataVersion);
            }
        }
        getMavenContext().getExecutionEnvironment().getMavenSession().getExecutionProperties().setProperty(pid + "Version", stableVersion);
        getMavenContext().getExecutionEnvironment().getMavenSession().getExecutionProperties().setProperty(pid + "DataVersion", stableDataVersion);


        getMavenGoals().createPlugin(getProductId(), ampsCreatePluginPrompter);
    }

    /**
     * @param searchString  The name of a product (refapp, jira, confluence, etc)
     * @return  An Application object holding latest stable/data version strings
     *          and some other bits of information.
     */
    private Application getManualVersion(String searchString) {

        XMLInputFactory f = XMLInputFactory.newInstance();
        try {
            XMLEventReader r = f.createXMLEventReader(CreateMojo.class.getClassLoader().getResourceAsStream("application-versions.xml"));
            Application a = new Application();
            String name = "";
            while (r.hasNext()) {
                XMLEvent e = r.nextEvent();
                switch (e.getEventType()) {
                    case XMLStreamConstants.START_ELEMENT:
                        name = e.asStartElement().getName().getLocalPart();
                        if (name.equalsIgnoreCase("application"))
                            a = new Application();
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        if (e.asEndElement().getName().getLocalPart().equalsIgnoreCase("application") &&
                                a.name.equals(searchString))
                            return a;
                        name = "";
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        String s = e.asCharacters().getData().trim();
                        if (name.equalsIgnoreCase("name"))
                            a.name = s;
                        else if (name.equalsIgnoreCase("latest"))
                            a.latest = s;
                        else if (name.equalsIgnoreCase("data"))
                            a.data = s;
                        else if (name.equalsIgnoreCase("mvn-artifact"))
                            a.mvnArtifact = s;
                        break;
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class Application {
        public String name;
        public String latest;
        public String data;
        public String mvnArtifact;

        public String toString() {
            return "name=" + name + " latest=" + latest + " data=" + data + " mvn-artifact=" + mvnArtifact;
        }
    }

    protected String getStableProductVersion(AbstractProductHandler handler, Product ctx) throws MojoExecutionException
    {
        ProductArtifact artifact = handler.getArtifact();
        
        if(null == artifact)
        {
            return "";    
        }
        
        Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), "LATEST");
        
        return ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);
    }

    protected String getStableDataVersion(AbstractProductHandler handler, Product ctx) throws MojoExecutionException
    {
        ProductArtifact artifact = handler.getTestResourcesArtifact();

        if(null == artifact)
        {
            return "";
        }
        
        Artifact warArtifact = artifactFactory.createProjectArtifact(artifact.getGroupId(), artifact.getArtifactId(), "LATEST");

        return ctx.getArtifactRetriever().getLatestStableVersion(warArtifact);
    }

    protected AbstractProductHandler createProductHandler(String productId)
    {
        return (AbstractProductHandler) ProductHandlerFactory.create(productId, getMavenContext(), getMavenGoals(), artifactFactory);
    }
}
