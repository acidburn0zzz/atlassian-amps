package com.atlassian.plugins.codegen.modules.common.keyboard;

import com.atlassian.plugins.codegen.modules.BasicNameModuleProperties;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import java.util.Collections;
import java.util.List;

/**
 * @since 3.6
 */
public abstract class AbstractKeyboardShortcutProperties extends BasicNameModuleProperties
{

    public static final String HIDDEN = "HIDDEN";
    public static final String ORDER = "ORDER";
    public static final String SHORTCUT = "SHORTCUT";
    public static final String OPERATION_TYPE = "OPERATION_TYPE";
    public static final String OPERATION_VALUE = "OPERATION_VALUE";
    public static final String CONTEXT = "CONTEXT";

    public static final List<String> OPERATIONS = ImmutableList.of(
            "click", "evaluate", "execute", "followLink", "goTo", "moveToAndClick",
            "moveToAndFocus", "moveToNextItem", "moveToPrevItem");

    private static final List<String> XPRODUCT_CONTEXTS = ImmutableList.of("global");

    private final List<String> allowedContexts;

    public AbstractKeyboardShortcutProperties()
    {
        this("My Keyboard Shortcut");
    }

    public AbstractKeyboardShortcutProperties(String moduleName)
    {
        super(moduleName);
        setHidden(false);
        setOrder(10);
        setContext("global");
        allowedContexts = ImmutableList.copyOf(Iterables.concat(XPRODUCT_CONTEXTS, getAdditionalContexts()));
    }

    public List<String> getAllowedContexts()
    {
        return allowedContexts;
    }

    public void setHidden(boolean hidden)
    {
        setProperty(HIDDEN, Boolean.toString(hidden));
    }

    public boolean isHidden()
    {
        return Boolean.valueOf(getProperty(HIDDEN));
    }

    public void setOrder(int order)
    {
        setProperty(ORDER, Integer.toString(order));
    }

    public String getOrder()
    {
        return getProperty(ORDER);
    }

    public int getOrderAsInt()
    {
        return Integer.parseInt(getProperty(ORDER));
    }

    public void setShortcut(String s)
    {
        setProperty(SHORTCUT, s);
    }

    public String getShortcut()
    {
        return getProperty(SHORTCUT);
    }

    public void setContext(String s)
    {
        setProperty(CONTEXT, s);
    }

    public String getContext()
    {
        return getProperty(CONTEXT);
    }

    public void setOperationType(String s)
    {
        setProperty(OPERATION_TYPE, s);
    }

    public String getOperationType()
    {
        return getProperty(OPERATION_TYPE);
    }

    public void setOperationValue(String s)
    {
        setProperty(OPERATION_VALUE, s);
    }

    public String getOperationValue()
    {
        return getProperty(OPERATION_VALUE);
    }

    /**
     * @return additional contexts allowed by the specific product the plugin is targeting
     */
    protected List<String> getAdditionalContexts()
    {
        return Collections.emptyList();
    }
}
