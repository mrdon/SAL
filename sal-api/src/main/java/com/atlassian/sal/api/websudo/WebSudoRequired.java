package com.atlassian.sal.api.websudo;

import java.lang.annotation.*;

/**
 * Elements marked with this annotaion will require WebSudo protection.
 *
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface WebSudoRequired
{
}
