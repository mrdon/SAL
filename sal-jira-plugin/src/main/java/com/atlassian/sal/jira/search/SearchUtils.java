package com.atlassian.sal.jira.search;

import com.atlassian.bonnie.search.summary.Summarizer;
import com.atlassian.bonnie.search.summary.Summary;
import com.opensymphony.util.TextUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * the original version of this required Nutch, removing the dependency until we decide if we re-implement or replace.
 * The following dependencies were required
 * <ul>
 * <li>import com.atlassian.spring.container.ContainerManager;
 * <li>import org.apache.nutch.searcher.Summarizer;
 * <li>import org.apache.nutch.searcher.Query;
 * <li>import org.apache.nutch.searcher.Summary;
 * </ul>
 */
public class SearchUtils
{
    public static final int UNBROKEN_TEXT_LIMIT = 120;
    public static final int EXCERPT_MAX = 500;

    private static final Logger log = Logger.getLogger(SearchUtils.class);

    /**
     * Return the safe summary of the text
     *
     * @param txt
     * @param queryString
     * @return an HTML escaped version of the text with the query string highlighted.
     */
    public static String escapeAndSummarize(String txt, String queryString)
    {
        txt = TextUtils.plainTextToHtml(txt);
        txt = txt.replaceAll("<br>", "");
        return summarize(txt, queryString);
    }

    public static String summarize(String txt, String queryString)
    {
        final StringBuilder buffer = new StringBuilder();
        final Summary summary;
        try
        {
            summary = new Summarizer(new StandardAnalyzer()).getSummary(txt, queryString);
        }
        catch (IOException e)
        {
            log.error(e);
            return txt;
        }

        for (Summary.Fragment fragment : summary.getFragments())
        {
            if (fragment.isHighlight())
            {
                buffer.append("<span id=\"highlight\">").append(fragment.getText()).append("</span>");
            }
            else
            {
                buffer.append(fragment);
            }
        }
        return buffer.toString();
    }

    public static String stripLongUnbrokenText(String txt)
    {
        StringBuffer txtBuffer = new StringBuffer();
        StringBuffer longStringBuffer = new StringBuffer();

        for (int i = 0; i < txt.length(); i++)
        {
            char c = txt.charAt(i);
            if (c != ' ')
            {
                longStringBuffer.append(c);
            }
            else
            {
                if (longStringBuffer.length() < UNBROKEN_TEXT_LIMIT)
                {
                    txtBuffer.append(longStringBuffer);
                }

                txtBuffer.append(c); // append white space back on
                longStringBuffer = longStringBuffer.delete(0, longStringBuffer.length()); // reset buffer
            }
        }

        return txtBuffer.toString();
    }

    public static String getTimeDisplayValue(long timeInMillis)
    {
        //less than 10 milli seconds, result must have come from the cache...
        if (timeInMillis < 10)
        {
            return "< 0.01 seconds";
        }
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format((double) timeInMillis / 1000) + " seconds";

    }
}