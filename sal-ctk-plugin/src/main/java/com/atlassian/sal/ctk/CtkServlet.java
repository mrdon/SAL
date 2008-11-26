package com.atlassian.sal.ctk;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CtkServlet extends HttpServlet
{
    private final CtkTestSuite suite;

    public CtkServlet(CtkTestSuite suite)
    {
        this.suite = suite;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<CtkTestResult> results = suite.execute();
        PrintWriter out = resp.getWriter();

        out.print("<html><head><style>.PASS{background-color: green;} .FAIL{background-color: red;}.WARN{background-color: yellow;}</style></head>");
        out.print("<body><h1>CTK Results</h1>");
        out.print("<table><tr><th>Result</th><th>Test Group</th><th>Message</th></tr>");
        for (CtkTestResult result : results)
        {
            out.print(result.toHtml());
        }
        out.print("</table></body></html>");
        out.close();
    }
}
