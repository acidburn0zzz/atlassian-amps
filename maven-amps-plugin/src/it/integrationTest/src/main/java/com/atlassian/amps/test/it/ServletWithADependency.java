package com.atlassian.amps.test.it;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log.Logger;

/**
 * This servlet has a dependency to Apache Avalon logkit for
 * testing that libartifacts are deployed correctly. (Could also 
 * be used to test other ways of providing dependencies).
 * 
 * You will get a 500 server error if the dependency is missing.
 * 
 * @author ekaukonen
 *
 */

public class ServletWithADependency extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.getWriter().write("I have a dependency to: " + Logger.class.getName());
        res.getWriter().close();
    }
}
