package com.atlassian.sal.api.search.parameter;

/**
 * SearchParameter to apply a maximum search limit.  a value of -1 means no limit.
 */
public class MaxhitsSearchParameter extends BasicSearchParameter
{
    public MaxhitsSearchParameter(String queryString)
    {        
        super(queryString);
    }

    public MaxhitsSearchParameter(int maxHits)
    {
        super(MAXHITS, String.valueOf(maxHits));
    }
}
