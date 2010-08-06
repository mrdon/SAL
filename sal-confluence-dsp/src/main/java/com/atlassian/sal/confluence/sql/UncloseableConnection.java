package com.atlassian.sal.confluence.sql;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * <p>A connection that can't be closed.</p>
 * <p>All calls to Active Objects wihtin Confluence (i.e within a plugin installed on a Confluence instance) must happen
 * within a transaction. For this transactions to be successful, we can't let ActiveObjects close the connection in the
 * middle of it.</p>
 */
final class UncloseableConnection implements Connection
{
    private final Connection connection;

    UncloseableConnection(final Connection connection)
    {
        this.connection = connection;
    }

    public Statement createStatement() throws SQLException
    {
        return connection.createStatement();
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
        return connection.prepareStatement(sql);
    }

    public CallableStatement prepareCall(String sql) throws SQLException
    {
        return connection.prepareCall(sql);
    }

    public String nativeSQL(String sql) throws SQLException
    {
        return connection.nativeSQL(sql);
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException
    {
        connection.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException
    {
        return connection.getAutoCommit();
    }

    public void commit() throws SQLException
    {
//                connection.commit();
    }

    public void rollback() throws SQLException
    {
//                connection.rollback();
    }

    public void close() throws SQLException
    {
        // do nothing
    }

    public boolean isClosed() throws SQLException
    {
        return false;
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        return connection.getMetaData();
    }

    public void setReadOnly(boolean readOnly) throws SQLException
    {
        connection.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException
    {
        return connection.isReadOnly();
    }

    public void setCatalog(String catalog) throws SQLException
    {
        connection.setCatalog(catalog);
    }

    public String getCatalog() throws SQLException
    {
        return connection.getCatalog();
    }

    public void setTransactionIsolation(int level) throws SQLException
    {
        connection.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException
    {
        return connection.getTransactionIsolation();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        return connection.getWarnings();
    }

    public void clearWarnings() throws SQLException
    {
        connection.clearWarnings();
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    public Map<String, Class<?>> getTypeMap() throws SQLException
    {
        return connection.getTypeMap();
    }

    public void setTypeMap(Map<String, Class<?>> map) throws SQLException
    {
        connection.setTypeMap(map);
    }

    public void setHoldability(int holdability) throws SQLException
    {
        connection.setHoldability(holdability);
    }

    public int getHoldability() throws SQLException
    {
        return connection.getHoldability();
    }

    public Savepoint setSavepoint() throws SQLException
    {
        return connection.setSavepoint();
    }

    public Savepoint setSavepoint(String name) throws SQLException
    {
        return connection.setSavepoint(name);
    }

    public void rollback(Savepoint savepoint) throws SQLException
    {
        connection.rollback(savepoint);
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException
    {
        connection.releaseSavepoint(savepoint);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return connection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
    {
        return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
    {
        return connection.prepareStatement(sql, autoGeneratedKeys);
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
    {
        return connection.prepareStatement(sql, columnIndexes);
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
    {
        return connection.prepareStatement(sql, columnNames);
    }
}

