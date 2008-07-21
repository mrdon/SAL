package com.atlassian.sal.ctk;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.atlassian.plugin.predicate.ModuleDescriptorPredicate;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.PluginParseException;

@Component
public class CtkServlet extends HttpServlet
{
    ModuleDescriptorPredicate pred;
    private final CtkTestSuite suite;
    private final PluginController controller;

    public CtkServlet(CtkTestSuite suite, PluginController controller)
    {
        this.suite = suite;
        this.controller = controller;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        try
        {
            int num = controller.scanForNewPlugins();
            if (num > 0)
            {
                resp.sendRedirect(req.getRequestURI());
                return;
            }
        } catch (PluginParseException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalStateException ex)
        {
            // unloaded current servlet, try again
            resp.sendRedirect(req.getRequestURI());
            return;
        }

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
