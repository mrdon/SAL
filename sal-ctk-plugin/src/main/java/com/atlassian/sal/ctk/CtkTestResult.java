package com.atlassian.sal.ctk;

public class CtkTestResult
{
    private final boolean passed;
    private final String message;

    public CtkTestResult(boolean passed, String message)
    {
        this.passed = passed;
        this.message = message;
    }

    

    public boolean isPassed()
    {
        return passed;
    }

    public String getMessage()
    {
        return message;
    }

    public String toHtml()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr class=").append(passed ? "pass" : "fail").append("\">");
        sb.append("<td>").append(passed ? "PASS" : "FAIL").append("</td>");
        sb.append("<td>").append(message).append("</td>");
        sb.append("</tr>");
        return sb.toString();
    }

}
