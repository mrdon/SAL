package com.atlassian.sal.ctk;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CtkTestSuite
{

    private final List<CtkTest> tests;

    public CtkTestSuite(List<CtkTest> tests)
    {
        this.tests = tests;
    }


    List<CtkTestResult> execute()
    {
        CtkTestResults results = new CtkTestResults();
        for (CtkTest test : tests)
        {
            test.execute(results);
        }


        return results.getResults();
    }
}
