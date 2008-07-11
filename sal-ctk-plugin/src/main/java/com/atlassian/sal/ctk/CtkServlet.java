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

@Component
public class CtkServlet extends HttpServlet
{
    ModuleDescriptorPredicate pred;
    private final CtkTestSuite suite;

    public CtkServlet(CtkTestSuite suite) {this.suite = suite;}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        List<CtkTestResult> results = suite.execute();
        PrintWriter out = resp.getWriter();

        out.print("<html><h1>CTK Results</h1>");
        out.print("<table><tr><th>Result</th><th>Message</th></tr>");
        for (CtkTestResult result : results)
        {
            out.print(result.toHtml());
        }
        out.print("</table></html>");
        out.close();
    }
}
