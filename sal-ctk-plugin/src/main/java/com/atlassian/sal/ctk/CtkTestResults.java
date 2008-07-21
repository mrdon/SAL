package com.atlassian.sal.ctk;

import java.util.List;
import java.util.ArrayList;

public class CtkTestResults
{
    private String currentTest;
    List<CtkTestResult> results = new ArrayList<CtkTestResult>();

    public void assertTrue(String message, boolean val)
    {
        results.add(new CtkTestResult(val, currentTest, message));
    }

    public void assertTrueOrWarn(String message, boolean val)
    {
        results.add(new CtkTestResult((val ? Result.PASS : Result.WARN), currentTest, message));
    }

    public void fail(String s)
    {
        results.add(new CtkTestResult(false, currentTest, s));
    }

    public List<CtkTestResult> getResults()
    {
        return results;
    }

    public void pass(String s)
    {
        results.add(new CtkTestResult(true, currentTest, s));
    }

    public void warn(String s)
    {
        results.add(new CtkTestResult(true, currentTest, s));
    }

    public void setCurrentTest(CtkTest currentTest)
    {
        this.currentTest = currentTest.getClass().getSimpleName();
    }
}
