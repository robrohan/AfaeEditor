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
package com.rohanclan.afae.editor;

/**
 * This class represents items in the document that will be listed in the
 * jumper pull down at the top of the document. Much like Xcode or in other Mac OS X
 * editors
 * @author robrohan
 */
public class DocumentJumpItem implements Comparable
{
	public static final byte TYPE_METHOD = 0;
	public static final byte TYPE_CLASS = 1;
	
	private String itemName = "";
	private byte type = TYPE_METHOD;
	private int offset = 0;
	private int length = 0;
	
	public DocumentJumpItem(int offset, int length)
	{
		this.offset = offset;
		this.length = length;
	}

	public boolean equals(Object o)
	{
		if((o instanceof DocumentJumpItem))
		{
			return ( ((DocumentJumpItem)o).hashCode() == this.hashCode() );
		}
		return false;
	}
	
	public int compare(Object o1, Object o2)
	{
		return ((DocumentJumpItem)o1).compareTo((DocumentJumpItem)o2);
	}
	
	public int compareTo(Object o)
	{
		if(o == null)
			throw new NullPointerException("Null!");
		
		if(o instanceof DocumentJumpItem)
		{
			//lowercase for the createobject hack
			return ((DocumentJumpItem)o).hashCode() - this.hashCode();
		}
		
		return 0;
	}
	
	public String getItemName() 
	{
		return itemName;
	}

	public void setItemName(String itemName) 
	{
		this.itemName = itemName;
	}

	public int getLength() 
	{
		return length;
	}

	public void setLength(int length) 
	{
		this.length = length;
	}

	public int getOffset() 
	{
		return offset;
	}

	public void setOffset(int offset) 
	{
		this.offset = offset;
	}

	public byte getType() 
	{
		return type;
	}

	public void setType(byte type) 
	{
		this.type = type;
	}
	
	public int hashCode()
	{
		return this.offset + this.length;
	}
	
	public String toString()
	{
		return this.itemName;
	}
}
