package com.atlassian.sal.api.sql;

import javax.sql.DataSource;

/**
 * Gives access to the host application data source.
 */
public interface DataSourceProvider
{
    /**
     * @return the host application data source
     */
    DataSource getDataSource();
}
