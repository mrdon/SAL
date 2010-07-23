package com.atlassian.sal.ctk;

import java.util.ArrayList;
import java.util.List;

public class CtkTestResults
{
    private String currentTest;
    List<CtkTestResult> results = new ArrayList<CtkTestResult>();

    public void assertTrue(final String message, final boolean val)
    {
        results.add(new CtkTestResult(val, currentTest, message));
    }

    public void assertTrueOrWarn(final String message, final boolean val)
    {
        results.add(new CtkTestResult((val ? Result.PASS : Result.WARN), currentTest, message));
    }

    public void fail(final String s)
    {
        results.add(new CtkTestResult(false, currentTest, s));
    }

    public List<CtkTestResult> getResults()
    {
        return results;
    }

    public void pass(final String s)
    {
        results.add(new CtkTestResult(true, currentTest, s));
    }

    public void warn(final String s)
    {
        results.add(new CtkTestResult(true, currentTest, s));
    }

    public void setCurrentTest(final CtkTest currentTest)
    {
        this.currentTest = currentTest.getClass().getSimpleName();
    }
}
