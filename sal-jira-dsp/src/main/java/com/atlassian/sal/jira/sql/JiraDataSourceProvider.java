package com.atlassian.sal.jira.sql;

import com.atlassian.sal.api.sql.DataSourceProvider;
import org.ofbiz.core.entity.ConnectionFactory;
import org.ofbiz.core.entity.GenericEntityException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public final class JiraDataSourceProvider implements DataSourceProvider
{
    private final DataSource ds;

    public JiraDataSourceProvider()
    {
        ds = new OfBizDataSource("defaultDS");
    }

    public DataSource getDataSource()
    {
        return ds;
    }

    private static class OfBizDataSource extends AbstractDataSource
    {
        private final String helperName;

        public OfBizDataSource(String helperName)
        {
            this.helperName = helperName;
        }

        public Connection getConnection() throws SQLException
        {
            try
            {
                return ConnectionFactory.getConnection(helperName);
            }
            catch (GenericEntityException e)
            {
                throw new SQLException(e.getMessage());
            }
        }

        public Connection getConnection(String username, String password) throws SQLException
        {
            throw new IllegalStateException("Not allowed to get a connection for non default username/password");
        }
    }

    private static abstract class AbstractDataSource implements DataSource
    {
        /**
         * Returns 0, indicating to use the default system timeout.
         */
        public int getLoginTimeout() throws SQLException
        {
            return 0;
        }

        /**
         * Setting a login timeout is not supported.
         */
        public void setLoginTimeout(int timeout) throws SQLException
        {
            throw new UnsupportedOperationException("setLoginTimeout");
        }

        /**
         * LogWriter methods are not supported.
         */
        public PrintWriter getLogWriter()
        {
            throw new UnsupportedOperationException("getLogWriter");
        }

        /**
         * LogWriter methods are not supported.
         */
        public void setLogWriter(PrintWriter pw) throws SQLException
        {
            throw new UnsupportedOperationException("setLogWriter");
        }
    }
}
