package com.atlassian.sal.ctk;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class CtkTestSuite
{
    private final List<CtkTest> tests;
    private static Log log = LogFactory.getLog(CtkTestSuite.class);

    public CtkTestSuite(final List<CtkTest> tests)
    {
        this.tests = tests;
    }

    List<CtkTestResult> execute()
    {
        final CtkTestResults results = new CtkTestResults();
        for (final CtkTest test : tests)
        {
            results.setCurrentTest(test);
            try
            {
                test.execute(results);
            } catch (final Exception e)
            {
                results.fail("Unable to execute tests: "+e.getMessage());
                log.error(e, e);
            }
        }


        return results.getResults();
    }
}
