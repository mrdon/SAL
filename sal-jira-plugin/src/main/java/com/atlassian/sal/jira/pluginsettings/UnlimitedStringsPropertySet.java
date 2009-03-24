package com.atlassian.sal.jira.pluginsettings;

import com.opensymphony.module.propertyset.PropertySet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;

/**
 * Wrapper around PropertySet that splits Strings longer than 255 into multiple Strings shorter than 255.
 */
public class UnlimitedStringsPropertySet
{

    public static PropertySet create(final PropertySet propertySet)
    {
        InvocationHandler handler = new PropertySetInvocationHandler(propertySet);
        return (PropertySet) Proxy.newProxyInstance(PropertySet.class.getClassLoader(), new Class[] { PropertySet.class }, handler );
    }
    
    static class PropertySetInvocationHandler implements InvocationHandler
    {
        private final PropertySet propertySet;
        private static final String MARKER = "#-#-#";
        private static final int MAX_LENGTH = 255;
        public PropertySetInvocationHandler(PropertySet propertySet)
        {
            this.propertySet = propertySet;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
            if (method.getName().equals("setString"))
            {
                setString((String)args[0], (String)args[1]);
                return null;
            } else if (method.getName().equals("getString"))
            {
                return getString((String) args[0]);
            } else if (method.getName().equals("remove"))
            {
                remove((String) args[0]);
                return null;
            } else
            {
                return method.invoke(propertySet, args);
            }
        }

        private String getString(String name)
        {
            String value = propertySet.getString(name);
            if (value==null || !value.startsWith(MARKER))
            {
                return value;
            }
            int piecesCount = Integer.parseInt(value.substring(MARKER.length()));
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < piecesCount; i++)
            {
                builder.append(propertySet.getString(name+MARKER+i));
            }
            return builder.toString();
        }

        private void setString(String name, String value)
        {
            if (value.length()<=MAX_LENGTH)
            {
                propertySet.setString(name, value);
                return;
            }
            String [] pieces = split(value);
            propertySet.setString(name, MARKER+pieces.length);
            for (int i = 0; i < pieces.length; i++)
            {
                propertySet.setString(name+MARKER+i, pieces[i]);
            }
        }

        private void remove(String name)
        {
            String value = propertySet.getString(name);
            propertySet.remove(name);
            if (value==null || !value.startsWith(MARKER))
            {
                return;
            }
            int piecesCount = Integer.parseInt(value.substring(MARKER.length()));
            for (int i = 0; i < piecesCount; i++)
            {
                propertySet.remove(name+MARKER+i);
            }
        }
    
        private String[] split(String value)
        {
            ArrayList<String> pieces = new ArrayList<String>();
            while (value.length()>MAX_LENGTH)
            {
                pieces.add(value.substring(0, MAX_LENGTH));
                value = value.substring(MAX_LENGTH);
            }
            if (value.length()>0)
            {
                pieces.add(value);
            }
            return pieces.toArray(new String[0]);
        }
    };
    
    
}
