package com.atlassian.sal.api.timezone;

import java.util.TimeZone;

/**
 *
 * @since v2.6.0
 */
public interface TimeZoneManager
{

    /**
     * Returns the time zone of the logged in user.
     * NB: This is guaranteed to return a non-null value.
     * If no user is logged in (anonymous user) or the system doesn't support time zone configuration or no specific time zone is configured,
     * it should still return a time zone. (e.g. the default time zone for the system).
     *
     * @return the user's time zone. Should never return null.
     */
    TimeZone getUserTimeZone();

    /**
     * Returns the default time zone for the application.
     * NB: This is guaranteed to return a non-null value.
     *
     * If the system doesn't support time zone configuration, it should still return a time zone (e.g. the JVM time zone).
     *
     * @return the default time zone of the system. Should never return null.
     */
    TimeZone getDefaultTimeZone();
}
