package com.atlassian.amps.test.it;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.log.*;

public class ItServlet extends HttpServlet
{
		
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.getWriter().write(Logger.class.getName());
    	res.getWriter().close();
        
    }
}
