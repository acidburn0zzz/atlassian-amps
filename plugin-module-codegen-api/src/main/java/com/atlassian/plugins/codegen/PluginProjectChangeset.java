package com.atlassian.plugins.codegen;

import com.google.common.collect.ImmutableList;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.isEmpty;

/**
 * Describes changes that should be applied to the project.  These may include changes
 * to the POM, the plugin XML file, and any other files within the project.  Implementations
 * of {@link com.atlassian.plugins.codegen.modules.PluginModuleCreator} return an instance
 * of this class rather than performing the changes directly.
 * <p>
 * This class is immutable; all of its non-getter methods return new instances.
 * <p>
 * This class also contains static factory methods for all supported change types.
 */
public final class PluginProjectChangeset
{
    private static final PluginProjectChangeset EMPTY = new PluginProjectChangeset();
    
    private final ImmutableList<PluginProjectChange> changes;
    
    public static PluginProjectChangeset changeset()
    {
        return EMPTY;
    }
    
    public PluginProjectChangeset()
    {
        this(ImmutableList.<PluginProjectChange>of());
    }
    
    private PluginProjectChangeset(Iterable<PluginProjectChange> changes)
    {
        this.changes = ImmutableList.copyOf(changes);
    }

    /**
     * Returns all changes in the changeset.
     */
    public Iterable<PluginProjectChange> getItems()
    {
        return changes;
    }
    
    /**
     * Returns only the changes of the specified class.
     */
    public <T extends PluginProjectChange> Iterable<T> getItems(Class<T> itemClass)
    {
        return filter(changes, itemClass);
    }
    
    /**
     * Returns true if the changeset contains any items of the specified class.
     */
    public boolean hasItems(Class<? extends PluginProjectChange> itemClass)
    {
        return !isEmpty(getItems(itemClass));
    }
    
    /**
     * Returns a copy of this changeset with the specified item(s) added.
     */
    public PluginProjectChangeset with(PluginProjectChange... newChanges)
    {
        return new PluginProjectChangeset(concat(changes, ImmutableList.copyOf(newChanges)));
    }

    /**
     * Returns a copy of this changeset with the specified item(s) added.
     */
    public PluginProjectChangeset with(Iterable<? extends PluginProjectChange> newChanges)
    {
        return new PluginProjectChangeset(concat(changes, ImmutableList.copyOf(newChanges)));
    }
    
    /**
     * Returns a changeset consisting of this changeset plus all items from another changeset.
     */
    public PluginProjectChangeset with(PluginProjectChangeset other)
    {
        return new PluginProjectChangeset(concat(changes, other.changes));
    }
}
