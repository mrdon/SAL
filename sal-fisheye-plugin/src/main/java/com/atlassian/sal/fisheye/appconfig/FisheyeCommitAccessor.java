package com.atlassian.sal.fisheye.appconfig;

import com.atlassian.fisheye.event.CommitEvent;
import com.cenqua.fisheye.rep.ChangeSet;

public interface FisheyeCommitAccessor
{
    ChangeSet getCommitChangeSet(final CommitEvent commitEvent);
}
