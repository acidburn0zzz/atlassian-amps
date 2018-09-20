package com.atlassian.plugins.codegen;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import io.atlassian.fugue.Option;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.atlassian.fugue.Option.none;

/**
 * Describes a &lt;component&gt; element that should be added to the plugin XML file.
 * This is provided in addition to {@link ModuleDescriptor} because some other types of
 * modules may also need to create component declarations.
 * <p>
 * Unlike other classes in this package, this class uses a builder pattern due to the
 * large number of optional properties. 
 */
public final class ComponentDeclaration implements PluginProjectChange
{
    public enum Visibility
    {
        PUBLIC,
        PRIVATE
    };

    private final ClassId classId;
    private final String key;
    private final Visibility visibility;
    private final Option<String> name;
    private final Option<String> nameI18nKey;
    private final Option<String> description;
    private final Option<String> descriptionI18nKey;
    private final Option<ClassId> interfaceId;
    private final Option<String> alias;
    private final Option<String> application;
    private final ImmutableMap<String, String> serviceProperties;
    
    public static Builder builder(ClassId classId, String key)
    {
        return new Builder(classId, key);
    }
    
    public static ComponentDeclaration componentDeclaration(ClassId classId, String key)
    {
        return builder(classId, key).build();
    }
    
    private ComponentDeclaration(Builder builder)
    {
        this.classId = builder.classId;
        this.visibility = builder.visibility;
        this.key = builder.key;
        this.name = builder.name;
        this.nameI18nKey = builder.nameI18nKey;
        this.description = builder.description;
        this.descriptionI18nKey = builder.descriptionI18nKey;
        this.interfaceId = builder.interfaceId;
        this.alias = builder.alias;
        this.application = builder.application;
        this.serviceProperties = ImmutableMap.copyOf(builder.serviceProperties);
    }

    public ClassId getClassId()
    {
        return classId;
    }
    
    public String getKey()
    {
        return key;
    }

    public Option<ClassId> getInterfaceId()
    {
        return interfaceId;
    }
    
    public Visibility getVisibility()
    {
        return visibility;
    }

    public Option<String> getName()
    {
        return name;
    }

    public Option<String> getNameI18nKey()
    {
        return nameI18nKey;
    }
    
    public Option<String> getDescription()
    {
        return description;
    }
    
    public Option<String> getDescriptionI18nKey()
    {
        return descriptionI18nKey;
    }
    
    public Option<String> getAlias()
    {
        return alias;
    }

    public Option<String> getApplication()
    {
        return application;
    }
    
    public ImmutableMap<String, String> getServiceProperties()
    {
        return serviceProperties;
    }
    
    @Override
    public String toString()
    {
        return "[component: " + classId + "]";
    }
    
    public static class Builder
    {
        private final ClassId classId;
        private final String key;
        private Visibility visibility = Visibility.PRIVATE;
        private Option<String> name = none();
        private Option<String> nameI18nKey = none();
        private Option<String> description = none();
        private Option<String> descriptionI18nKey = none();
        private Option<ClassId> interfaceId = none();
        private Option<String> alias = none();
        private Option<String> application = none();
        private Map<String, String> serviceProperties = Maps.newHashMap();
    
        public Builder(ClassId classId, String key)
        {
            this.classId = checkNotNull(classId, "classId");
            this.key = checkNotNull(key, "key");
        }
        
        public ComponentDeclaration build()
        {
            return new ComponentDeclaration(this);
        }
        
        public Builder interfaceId(Option<ClassId> interfaceId)
        {
            this.interfaceId = checkNotNull(interfaceId, "interfaceId");
            return this;
        }
        
        public Builder visibility(Visibility visibility)
        {
            this.visibility = checkNotNull(visibility, "visibility");
            return this;
        }

        public Builder name(Option<String> name)
        {
            this.name = checkNotNull(name, "name");
            return this;
        }

        public Builder nameI18nKey(Option<String> nameI18nKey)
        {
            this.nameI18nKey = checkNotNull(nameI18nKey, "nameI18nKey");
            return this;
        }

        public Builder description(Option<String> description)
        {
            this.description = checkNotNull(description, "description");
            return this;
        }

        public Builder descriptionI18nKey(Option<String> descriptionI18nKey)
        {
            this.descriptionI18nKey = checkNotNull(descriptionI18nKey, "descriptionI18nKey");
            return this;
        }

        public Builder alias(Option<String> alias)
        {
            this.alias = checkNotNull(alias, "alias");
            return this;
        }

        public Builder application(Option<String> application)
        {
            this.application = checkNotNull(application, "application");
            return this;
        }
        
        public Builder serviceProperties(Map<String, String> serviceProperties)
        {
            this.serviceProperties.putAll(checkNotNull(serviceProperties, "serviceProperties"));
            return this;
        }
    }
}
