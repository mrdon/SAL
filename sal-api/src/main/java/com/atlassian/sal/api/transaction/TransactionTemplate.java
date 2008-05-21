package com.atlassian.sal.api.transaction;

/**
 * This allows applications greater control over the transaction in which operations may be executed.
 * This really mimicks {@link org.springframework.transaction.support.TransactionTemplate}, however
 * since JIRA doesn't know about Spring and doesn't support transactions we need to have our own implementation
 * of this interface here.
 */
public interface TransactionTemplate
{
    Object execute(TransactionCallback action);
}
