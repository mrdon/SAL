package com.atlassian.sal.confluence.spi;

import org.springframework.transaction.PlatformTransactionManager;

import com.atlassian.sal.spring.component.SpringHostContextAccessor;

public class ConfluenceHostContextAccessor extends SpringHostContextAccessor
{
    public ConfluenceHostContextAccessor(final PlatformTransactionManager platformTransactionManager)
    {
        super(platformTransactionManager);
    }
}
