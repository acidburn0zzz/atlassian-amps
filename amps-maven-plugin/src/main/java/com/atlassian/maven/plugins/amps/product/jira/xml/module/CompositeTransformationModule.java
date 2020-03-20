package com.atlassian.maven.plugins.amps.product.jira.xml.module;

import org.apache.maven.plugin.MojoExecutionException;

import java.util.Arrays;
import java.util.Collection;

public class CompositeTransformationModule<T> implements TransformationModule<T> {
    Collection<TransformationModule> transformationModules;

    public CompositeTransformationModule(TransformationModule... transformationModules) {
        this.transformationModules = Arrays.asList(transformationModules);
    }

    @Override
    public boolean transform(T entity) throws MojoExecutionException {
        boolean accumulator = false;
        for (TransformationModule transformationModule : transformationModules) {
            accumulator |= transformationModule.transform(entity);
        }
        return accumulator;
    }
}
