package com.atlassian.sal.api.net;

import java.io.IOException;

import com.atlassian.sal.api.net.auth.Authenticator;


/**
 * Interface Request represents a request to retrieve data. To execute a request call {@link Request#execute(ResponseHandler)}. 
 *
 * @param <T>
 */
public interface Request<T extends Request<?>>
{
    /**
     *	Represents type of network request
     */
    public static enum MethodType
    {
        GET, POST, PUT, DELETE, HEAD, TRACE, OPTIONS;
    }

    /**
     * Sets the body of the request. In default implementation only requests of type {@link MethodType#POST} and {@link MethodType#POST} can have request body.
     *
     * @param requestBody the body of the request
     * @return a reference to this object.
     */
    T setRequestBody(String requestBody);
    /**
     * Sets Content-Type of the body of the request. In default implementation only requests of type {@link MethodType#POST} and {@link MethodType#POST} can have request body.
     * 
     * @param contentType the contentType of the request
     * @return a reference to this object.
     */
    T setRequestContentType(String contentType);
    /**
     * Sets parameters of the request. In default implementation only requests of type {@link MethodType#POST} and {@link MethodType#POST} can have parameters. 
     * For other requests include parameters in url. This method accepts odd number of arguments, in form of name, value, name, value, name, value, etc
     * 
     * @param params parameters of the request in as name, value pairs
     * @return a reference to this object.
     */
    T addRequestParameters(String... params);
	
    /**
     * Adds generic Authenticator to the request.
     * @param authenticator
     * @return a reference to this object.
     */
    T addAuthentication(Authenticator authenticator);
    /**
     * Adds TrustedTokenAuthentication to the request. Trusted token authenticator use current user to make a trusted application call. 
     * @return a reference to this object.
     */
    T addTrustedTokenAuthentication();
    /**
     * Adds basic authentication to the request.
     * 
     * @param username
     * @param password
     * @return a reference to this object.
     */
    T addBasicAuthentication(String username, String password);
    /**
     * Adds seraph authentication to the request.
     * 
     * @param username
     * @param password
     * @return a reference to this object.
     */
    T addSeraphAuthentication(String username, String password);

	/**
	 * Executes the request.
	 * @param responseHandler Callback handler of the response.
	 * @throws IOException
	 */
	void execute(ResponseHandler responseHandler) throws IOException;
	
	
	/**
	 * Executes a request and if response is successful, returns response as a string. @see {@link Response#getResponseBodyAsString()}
	 * @return response as String
	 * @throws IOException
	 */
	String execute() throws IOException;
	
	
}
