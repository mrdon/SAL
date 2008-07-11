package com.atlassian.sal.ctk;

import java.util.List;
import java.util.ArrayList;

public class CtkTestResults
{
    List<CtkTestResult> results = new ArrayList<CtkTestResult>();

    public void assertTrue(String message, boolean val)
    {
        results.add(new CtkTestResult(val, message));
    }

    public void fail(String s)
    {
        results.add(new CtkTestResult(false, s));
    }

    public List<CtkTestResult> getResults()
    {
        return results;
    }
}
