package com.atlassian.sal.api.message;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

/**
 * This interface is responsible for resolving a message or key/argument pairs to
 * their internationalized message.
 */
public interface I18nResolver
{
    /**
     * Given a key and a list of arguments this method returns the i18ned text if it can be resolved.
     * Arguments may also be of the form {@link Message} which means they will be resolved as well before
     * being included as an argument.
     *
     * @param key       key for the i18ned message
     * @param arguments Optional list of arguments for the message.
     * @return I18ned string
     */
    String getText(String key, Serializable... arguments);

    /**
     * Does the same as {@link #getText(String, java.io.Serializable[])} however it is needed for velocity.
     * @param key       key for the i18ned message
     * @return I18ned string
     */
    String getText(String key);

    /**
     * Given a {@link Message} this method returns the i18ned text if it can be resolved.
     * @param message The message to i18n
     * @return I18ned string
     */
    String getText(Message message);
    
    /**
	 * Creates an instance of Message.
	 * 
     * @param key
     * @param arguments
     * @return
     */
    Message createMessage(String key, Serializable... arguments);
    
    /**
     * Creates an instance of MessageCollection.
     * 
     * @return
     */
    MessageCollection createMessageCollection();

    /**
     * Given a prefix, this method will return all translations where the key starts with the given prefix as key ->
     * value mappings.
     *
     * @param prefix The prefix for a particular key to start with. May not be null. Empty string will match everything, which may be slow.
     * @param locale The locale for which to lookup translations. May not be null.
     * @return A Map of i18nKey -> translation mappings where i18nKey starts with the prefix. Empty map if no matches.
     */
    Map<String, String> getAllTranslationsForPrefix(String prefix, Locale locale);
}
