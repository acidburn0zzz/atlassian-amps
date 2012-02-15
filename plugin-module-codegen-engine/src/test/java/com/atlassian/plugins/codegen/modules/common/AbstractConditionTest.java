package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.common.web.AbstractConditionsProperties;

import org.junit.Test;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @since 3.6
 */
public abstract class AbstractConditionTest<T extends AbstractConditionsProperties> extends AbstractModuleCreatorTestCase<T>
{
    public static final String XPATH_ALL_CONDITIONS = "//conditions";
    public static final String XPATH_TOP_CONDITIONS = "/atlassian-plugin/*[not(self::conditions)]/conditions";
    public static final String XPATH_CONDITIONS_RELATIVE = "conditions";
    public static final String XPATH_ALL_CONDITION = "//condition";
    public static final String XPATH_TOP_CONDITION = "/atlassian-plugin/*[not(self::conditions)]/condition";
    public static final String XPATH_CONDITION_RELATIVE = "condition";
    public static final String JIRA_GLOBAL_PERMISSION = "com.atlassian.jira.plugin.webfragment.conditions.JiraGlobalPermissionCondition";
    public static final String JIRA_HAS_ISSUE_PERMISSION = "com.atlassian.jira.plugin.webfragment.conditions.HasIssuePermissionCondition";
    public static final String XPATH_PARAM_RELATIVE = "param";

    protected AbstractConditionTest(String moduleType, PluginModuleCreator<T> creator)
    {
        super(moduleType, creator);
    }
    
    @Test
    public void emptyConditionsAreNotIncluded() throws Exception
    {
        assertEquals(0, getGeneratedModule().selectNodes("//condition").size());
    }

    @Test
    public void singleConditionAdded() throws Exception
    {
        props.getConditions().add(new Condition(JIRA_GLOBAL_PERMISSION));
        
        assertNotNull(getGeneratedModule().selectSingleNode("//condition"));
    }

    @Test
    public void conditionHasClass() throws Exception
    {
        props.getConditions().add(new Condition(JIRA_GLOBAL_PERMISSION));
        
        assertEquals(JIRA_GLOBAL_PERMISSION, getGeneratedModule().selectSingleNode("//condition/@class").getText());
    }
    
    @Test
    public void conditionIsNotInverted() throws Exception
    {
        props.getConditions().add(new Condition(JIRA_GLOBAL_PERMISSION));
        
        assertNull(JIRA_GLOBAL_PERMISSION, getGeneratedModule().selectSingleNode("//condition/@invert"));
    }
    
    @Test
    public void invertedConditionHasAttribute() throws Exception
    {
        Condition jiraCondition = new Condition(JIRA_GLOBAL_PERMISSION);
        jiraCondition.setInvert(true);

        props.getConditions().add(jiraCondition);
        
        assertEquals("true", getGeneratedModule().selectSingleNode("//condition/@invert").getText());
    }

    @Test
    public void singleANDConditionsAdded() throws Exception
    {
        createSingleConditions(Conditions.AND);

        assertNotNull(getGeneratedModule().selectSingleNode("//conditions"));
    }
    
    @Test
    public void singleANDConditionsHasType() throws Exception
    {
        createSingleConditions(Conditions.AND);

        assertEquals(Conditions.AND, getGeneratedModule().selectSingleNode("//conditions/@type").getText());
    }
    
    @Test
    public void singleANDConditionsHasCondition() throws Exception
    {
        createSingleConditions(Conditions.AND);

        assertNotNull(getGeneratedModule().selectSingleNode("//conditions/condition"));
    }

    @Test
    public void singleORConditionsAdded() throws Exception
    {
        createSingleConditions(Conditions.OR);

        assertNotNull(getGeneratedModule().selectSingleNode("//conditions"));
    }

    @Test
    public void singleORConditionsHasType() throws Exception
    {
        createSingleConditions(Conditions.OR);

        assertEquals(Conditions.OR, getGeneratedModule().selectSingleNode("//conditions/@type").getText());
    }

    @Test
    public void nestedMixedConditionsAdded() throws Exception
    {
        createNestedMixedConditions();

        assertNotNull(getGeneratedModule().selectSingleNode("//conditions"));
    }
    
    @Test
    public void nestedMixedConditionsHasType() throws Exception
    {
        createNestedMixedConditions();

        assertEquals(Conditions.AND, getGeneratedModule().selectSingleNode("//conditions/@type").getText());
    }
    
    @Test
    public void nestedMixedConditionsHasNestedType() throws Exception
    {
        createNestedMixedConditions();

        assertEquals(Conditions.OR, getGeneratedModule().selectSingleNode("//conditions/conditions/@type").getText());
    }
    
    @Test
    public void nestedMixedConditionsHasClass() throws Exception
    {
        createNestedMixedConditions();

        assertEquals(JIRA_GLOBAL_PERMISSION, getGeneratedModule().selectSingleNode("//conditions/condition/@class").getText());
    }

    @Test
    public void nestedMixedConditionsHasNestedClass() throws Exception
    {
        createNestedMixedConditions();

        assertEquals(JIRA_HAS_ISSUE_PERMISSION, getGeneratedModule().selectSingleNode("//conditions/conditions/condition/@class").getText());
    }

    @Test
    public void conditionCanBeSiblingOfConditions() throws Exception
    {
        Conditions conditionsRoot = new Conditions(Conditions.AND);
        conditionsRoot.addCondition(new Condition(JIRA_GLOBAL_PERMISSION));

        Condition singleCondition = new Condition(JIRA_HAS_ISSUE_PERMISSION);

        props.getConditions().add(conditionsRoot);
        props.getConditions().add(singleCondition);

        assertNotNull(getGeneratedModule().selectSingleNode("//condition"));
    }
    
    @Test
    public void conditionsCanBeSiblingOfConditions() throws Exception
    {
        Conditions conditionsRoot = new Conditions(Conditions.AND);
        conditionsRoot.addCondition(new Condition(JIRA_GLOBAL_PERMISSION));

        Condition singleCondition = new Condition(JIRA_HAS_ISSUE_PERMISSION);

        props.getConditions().add(conditionsRoot);
        props.getConditions().add(singleCondition);

        assertNotNull(getGeneratedModule().selectSingleNode("//conditions/condition"));
    }

    @Test
    public void conditionParamsAreAdded() throws Exception
    {
        Condition condition = new Condition(JIRA_GLOBAL_PERMISSION);
        condition.getParams().put("permission", "admin");
        condition.getParams().put("username", "user");

        props.getConditions().add(condition);

        assertEquals(2, getGeneratedModule().selectNodes("//condition/param").size());
    }

    @Test
    public void conditionParamHasName() throws Exception
    {
        Condition condition = new Condition(JIRA_GLOBAL_PERMISSION);
        condition.getParams().put("permission", "admin");

        props.getConditions().add(condition);

        assertEquals("permission", getGeneratedModule().selectSingleNode("//condition/param/@name").getText());
    }

    @Test
    public void conditionParamHasValue() throws Exception
    {
        Condition condition = new Condition(JIRA_GLOBAL_PERMISSION);
        condition.getParams().put("permission", "admin");

        props.getConditions().add(condition);

        assertEquals("admin", getGeneratedModule().selectSingleNode("//condition/param/@value").getText());
    }
    
    protected void createSingleConditions(String type)
    {
        Conditions conditionsRoot = new Conditions(type);
        conditionsRoot.addCondition(new Condition(JIRA_GLOBAL_PERMISSION));

        props.getConditions().add(conditionsRoot);
    }
    
    protected void createNestedMixedConditions()
    {
        Conditions conditionsRoot = new Conditions(Conditions.AND);
        conditionsRoot.addCondition(new Condition(JIRA_GLOBAL_PERMISSION));

        Conditions nestedConditions = new Conditions(Conditions.OR);
        nestedConditions.addCondition(new Condition(JIRA_HAS_ISSUE_PERMISSION));

        conditionsRoot.addCondition(nestedConditions);
        props.getConditions().add(conditionsRoot);
    }
}
