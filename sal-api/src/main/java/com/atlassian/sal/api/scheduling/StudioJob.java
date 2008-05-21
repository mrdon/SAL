package com.atlassian.sal.api.scheduling;

import java.util.Map;

public interface StudioJob
{
    public void execute(Map jobDataMap);
}
