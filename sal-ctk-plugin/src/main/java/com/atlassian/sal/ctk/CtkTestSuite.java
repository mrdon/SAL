package com.atlassian.sal.ctk;

import org.springframework.stereotype.Component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

@Component
public class CtkTestSuite
{

    private final List<CtkTest> tests;
    private static Log log = LogFactory.getLog(CtkTestSuite.class);

    public CtkTestSuite(List<CtkTest> tests)
    {
        this.tests = tests;
    }


    List<CtkTestResult> execute()
    {
        CtkTestResults results = new CtkTestResults();
        for (CtkTest test : tests)
        {
            results.setCurrentTest(test);
            try
            {
                test.execute(results);
            } catch (Exception e)
            {
                results.fail("Unable to execute tests: "+e.getMessage());
                log.error(e, e);
            }
        }


        return results.getResults();
    }
}
