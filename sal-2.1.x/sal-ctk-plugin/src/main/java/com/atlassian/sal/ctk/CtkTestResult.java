package com.atlassian.sal.ctk;

public class CtkTestResult
{
    private final Result result;
    private final String message;
    private final String testClass;

    public CtkTestResult(final Result result, final String testClass, final String message)
    {
        this.result = result;
        this.message = message;
        this.testClass = testClass;
    }

    public CtkTestResult(final boolean pass, final String testClass, final String message)
    {
        this.result = pass ? Result.PASS : Result.FAIL;
        this.message = message;
        this.testClass = testClass;
    }

    public Result getResult()
    {
        return result;
    }

    public String getMessage()
    {
        return message;
    }

    public String toHtml()
    {
        final StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        sb.append("<td class=\"").append(result==Result.PASS ? "pass" : result==Result.FAIL ? "fail" : Result.WARN).append("\">").append(result).append("</td>");
        sb.append("<td>").append(testClass).append("</td>");
        sb.append("<td>").append(message).append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

    public String getTestClass()
    {
        return testClass;
    }
}
