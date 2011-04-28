package com.atlassian.sal.api.net;

import java.io.File;
import javax.activation.MimetypesFileTypeMap;

/**
 * @since v2.5
 */
public class RequestFilePart
{
    private String contentType;
    private String fileName;
    private final File file;
    private final String parameterName;

    public RequestFilePart(final String contentType, final String fileName, final File file, final String parameterName)
    {
        this.contentType = contentType;
        this.fileName = fileName;
        this.file = file;
        this.parameterName = parameterName;
    }

    public RequestFilePart(File file, final String parameterName)
    {
        this.file = file;
        this.parameterName = parameterName;
    }

    public String getFileName()
    {
        return (fileName != null) ? fileName : file.getName();
    }

    public String getContentType()
    {
        MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
        return (contentType != null) ? contentType : mimetypesFileTypeMap.getContentType(file);
    }

    public File getFile()
    {
        return file;
    }

    public String getParameterName()
    {
        return parameterName;
    }
}
