package com.atlassian.maven.plugins.amps.product.jira;

import java.lang.reflect.InvocationTargetException;

import com.atlassian.maven.plugins.amps.DataSource;

import org.apache.maven.plugin.MojoExecutionException;

public final class JiraDatabaseFactory
{
    private static JiraDatabaseFactory instance = null;

    private JiraDatabaseFactory()
    {
    }

    public static JiraDatabaseFactory getJiraDatabaseFactory()
    {
        if (null == instance)
        {
            synchronized (JiraDatabaseFactory.class)
            {
                if (null == instance)
                {
                    instance = new JiraDatabaseFactory();
                }
            }
        }
        return instance;
    }

    public JiraDatabase getJiraDatabase(DataSource dataSource) throws MojoExecutionException
    {
        JiraDatabaseType databaseType = JiraDatabaseType.getDatabaseType(dataSource.getUrl(), dataSource.getDriver());
        if (null == databaseType)
        {
            return null;
        }
        String databaseTypeName = databaseType.toString();
        // same sql syntax
        if(JiraDatabaseType.MSSQL_JTDS.toString().equals(databaseTypeName))
        {
            databaseTypeName = JiraDatabaseType.MSSQL.toString();
        }
        String databaseName = databaseTypeName.substring(0, 1) + databaseTypeName.substring(1).toLowerCase();

        String classImplName = "JiraDatabase" + databaseName + "Impl";
        // all implementation of interface JiraDatabase same this package
        final JiraDatabase jiraDatabase;
        try
        {
            Class<?> classImpl = Class.forName(this.getClass().getPackage().getName() + "." + classImplName);
            jiraDatabase = (JiraDatabase) classImpl.getConstructor(DataSource.class).newInstance(dataSource);
        }
        catch (InstantiationException e)
        {
            throw new MojoExecutionException("Database type " + databaseName + " has not supported yet", e);
        }
        catch (IllegalAccessException e)
        {
            throw new MojoExecutionException("Database type " + databaseName + " has not supported yet", e);

        }
        catch (InvocationTargetException e)
        {
            throw new MojoExecutionException("Database type " + databaseName + " has not supported yet", e);

        }
        catch (NoSuchMethodException e)
        {
            throw new MojoExecutionException("Database type " + databaseName + " has not supported yet", e);

        }
        catch (ClassNotFoundException e)
        {
            throw new MojoExecutionException("Database type " + databaseName + " has not supported yet", e);
        }
        return jiraDatabase;
    }
}
