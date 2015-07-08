package com.atlassian.maven.plugins.fecru;

import com.atlassian.maven.plugins.amps.CreateMojo;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "create", requiresProject = false)
public class FeCruCreateMojo extends CreateMojo {
    @Override
    protected String getDefaultProductId() throws MojoExecutionException {
        return ProductHandlerFactory.FECRU;
    }

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        //due to AMPS-1260
        getLog().error("!!! Important information !!!");
        getLog().error("FeCru does not support java8 yet so this command is temporarily switched off. Try using an earlier amps version or creating a default amps plugin instead: atlas-create-plugin");
        getLog().error("");
        throw new MojoFailureException("FeCru does not support java8 yet so this command is temporarily switched off. Try using an earlier amps version or creating a default amps plugin instead: atlas-create-plugin");
    }
}