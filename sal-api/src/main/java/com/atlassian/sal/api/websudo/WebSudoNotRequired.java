package com.atlassian.sal.api.websudo;

import java.lang.annotation.*;

/**
 * Any Element marked with this annotation will bypass the WebSudo protection.
 *
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface WebSudoNotRequired
{
}