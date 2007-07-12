/*
 * Copyright (c) 2005 Rob Rohan 
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.rohanclan.afae;

public class DefaultLogger implements ILogListener
{
	/** to toggle debug messages */
	public static final boolean DEBUG_ON 	= true;
	/** to toggle warn messages */
	public static final boolean WARN_ON 	= true;
	/** to toggle info messages */
	public static final boolean INFO_ON 	= true;
	/** to toggle error messages */
	public static final boolean ERROR_ON 	= true;
	
	public void logDebug(String message, Exception e, Class klass) {
		if(DEBUG_ON)
			System.out.println("DEBUG: " + buildSafeString(message, e, klass));
	}

	public void logWarn(String message, Exception e, Class klass) {
		if(WARN_ON)
			System.out.println("WARN: " + buildSafeString(message, e, klass));
	}

	public void logError(String message, Exception e, Class klass) {
		if(ERROR_ON)
			System.err.println("ERROR: " + buildSafeString(message, e, klass));
	}

	public void logInfo(String message, Exception e, Class klass) {
		if(INFO_ON)
			System.out.println("INFO: " + buildSafeString(message, e, klass));
	}
	
	private String buildSafeString(String message, Exception e, Class klass)
	{
		String buffer = " ";
		
		if(message != null)
		{
			buffer += message;
		}
		
		if(klass != null)
		{
			buffer = klass.getName() + buffer;
		}
		
		if(e != null)
		{
			buffer += e.getMessage();
		}
		return buffer;
	}

}
