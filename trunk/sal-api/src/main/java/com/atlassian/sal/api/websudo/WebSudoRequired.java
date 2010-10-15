package com.atlassian.sal.api.websudo;

import java.lang.annotation.*;

/**
 * Elements marked with this annotation will require WebSudo protection.
 * <p/>
 * This annotation can be applied to:
 * <ul>
 * <li>REST resources</li>
 * </ul>
 * <p/>
 * If an element is marked as @WebSudoRequired the host application ensures that it will only be accessed as part of
 * a WebSudo session if the host application supports WebSudo.
 * <p/>
 * <p/>
 * Annotations can be applied on the package, type (class, interface and enum) and method level.
 * Annotations on more specific elements (method < type < package) have precedence over annotations applied to more general elements.
 * E.g. an annotation applied to a method overrides the annotation applied to the whole package.
 *
 *
 * @see com.atlassian.sal.api.websudo.WebSudoNotRequired
 * @since 2.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface WebSudoRequired
{
}
