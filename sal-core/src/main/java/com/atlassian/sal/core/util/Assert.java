package com.atlassian.sal.core.util;

public class Assert
{
    /**
     * Check that {@code reference} is not {@code null}. If it is, throw a
     * {@code IllegalArgumentException}.
     *
     * @param reference
     *             reference to check is {@code null} or not
     * @return {@code reference} so it may be used
     * @throws IllegalArgumentException
     *             if {@code reference} is {@code null}
     */
    public static <T> T notNull(final T reference)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException();
        }
        return reference;
    }

    /**
     * Check that {@code reference} is not {@code null}. If it is, throw a
     * {@code IllegalArgumentException}.
     *
     * @param reference
     *            reference to check is {@code null} or not
     * @param errorMessage
     *            message passed to the {@code IllegalArgumentException} constructor
     *            to give more context when debugging
     * @return {@code reference} so it may be used
     * @throws IllegalArgumentException
     *             if {@code reference} is {@code null}
     */
    public static <T> T notNull(final T reference, final Object errorMessage)
    {
        if (reference == null)
        {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
        return reference;
    }
}