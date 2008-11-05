package com.atlassian.sal.crowd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException
	{
		final String content = "<html><body>Ahoj</body></html>";
    	response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentLength(content.getBytes("UTF-8").length);
        response.getWriter().print(content);
	}
}
