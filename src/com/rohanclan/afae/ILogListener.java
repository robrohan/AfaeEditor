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

/**
 * This is the interface for a log extension point. any of the parameters could
 * be null so you'll want to check them before you use them. If and implementer
 * thows an error it will be removed from the log listeners
 * @author robrohan
 */
public interface ILogListener 
{
	/**
	 * Handle a debug message
	 * @param message the string message could be null
	 * @param e the exception could be null
	 * @param klass the class that had this event could be null
	 */
	public void logDebug(String message, Exception e, Class klass);
	/**
	 * Handle a warn message
	 * @param message the string message could be null
	 * @param e the exception could be null
	 * @param klass the class that had this event could be null
	 */
	public void logWarn(String message, Exception e, Class klass);
	/**
	 * Handle a error message
	 * @param message the string message could be null
	 * @param e the exception could be null
	 * @param klass the class that had this event could be null
	 */
	public void logError(String message, Exception e, Class klass);
	/**
	 * Handle a info message
	 * @param message the string message could be null
	 * @param e the exception could be null
	 * @param klass the class that had this event could be null
	 */
	public void logInfo(String message, Exception e, Class klass);
}
