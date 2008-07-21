package com.atlassian.sal.ctk;

import com.atlassian.sal.api.net.ResponseException;

public interface CtkTest
{
    void execute(CtkTestResults results) throws Exception;
}
