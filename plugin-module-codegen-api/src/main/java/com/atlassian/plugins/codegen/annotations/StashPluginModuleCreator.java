package com.atlassian.plugins.codegen.annotations;

import java.lang.annotation.*;

/**
 * @since 3.10.3
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface StashPluginModuleCreator
{
}
