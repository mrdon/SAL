package com.atlassian.sal.api.scheduling;

import java.util.Map;
import java.util.Date;

public interface StudioScheduler
{
     public void scheduleJob(String name, Class<? extends StudioJob> job, Map jobDataMap, Date startTime,
        long repeatInterval);
}
