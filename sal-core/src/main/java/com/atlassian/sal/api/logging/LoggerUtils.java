package com.atlassian.sal.api.logging;

/**
 * Logging utility methods
 */
public class LoggerUtils {

    /**
     * Formats messages using parameters. For example, the call:
     * 
     * <pre>
     * format("foo #1", "bob");
     * </pre>
     * 
     * will return:
     * <pre>
     * foo bob
     * </pre>
     * 
     * @param msg The message
     * @param args A list of arguments.  A maximum of 10 are supported.
     * @return The formatted string
     */
    public static String format(String msg, String... args) {
        if (msg != null && msg.length() > 0 && msg.indexOf('#') > -1) {
            StringBuilder sb = new StringBuilder();
            boolean isArg = false;
            for (int x = 0; x < msg.length(); x++) {
                char c = msg.charAt(x);
                if (isArg) {
                    isArg = false;
                    if (Character.isDigit(c)) {
                        int val = Character.getNumericValue(c);
                        if (val >= 0 && val < args.length) {
                            sb.append(args[val]);
                            continue;
                        }
                    }
                    sb.append('#');
                }
                if (c == '#') {
                    isArg = true;
                    continue;
                }
                sb.append(c);
            }
            
            if (isArg) {
                sb.append('#');
            }
            return sb.toString();
        }
        return msg;
        
    }

}
