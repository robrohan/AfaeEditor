/*
 * cbg.editor Eclipse plugin
 * Copyright(c) 2002, Chris Grindstaff
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
package com.rohanclan.afae.rules;

// import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.modes.Rule;
import com.rohanclan.afae.modes.Type;

public class DelegateToken extends CToken {
	protected Rule delegate;

	protected String end;

	protected boolean consumed;

	public DelegateToken(Type type, Rule delegate, String end) {
		super(type);

		// AfaePlugin.logDebug(
		// "Got a delegate call: " + type.getContentType()
		// + " " + delegate.getTypes().size()
		// + " " + end,
		// null,
		// DelegateToken.class
		// );

		if (delegate == null)
			throw new IllegalArgumentException("delegate is null");

		this.delegate = delegate;
		this.end = end;

		consumed = false;
	}

	public Object getData() {
		return delegate.getName() + super.getData();
	}

	public String getEnd() {
		return end;
	}
}
