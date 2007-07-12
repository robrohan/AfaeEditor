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
package com.rohanclan.afae.partition;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.TypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

import com.rohanclan.afae.AfaePlugin;

/**
 * 
 * @author robrohan
 */
public class AfaePartitioner extends FastPartitioner 
{
	/**
	 * 
	 * @param scanner
	 * @param legalContentTypes
	 */
	public AfaePartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes) 
	{
		super(scanner, legalContentTypes);
	}

	/*
	 * 
	 */
    public ITypedRegion[] computePartitioning(int offset, int length, boolean includeZeroLengthPartitions) 
    {
    		AfaePlugin.logDebug("computePartitioning offset: " + offset + " length: "+length,null,AfaePartitioner.class);
    	
    		if(fDocument == null) 
    			return new ITypedRegion[0];
    		
		List<TypedRegion> list = new ArrayList<TypedRegion>();
		
		try 
		{
			int endOffset = offset + length;
			Position[] category = fDocument.getPositions(getManagingPositionCategories()[0]);
			TypedPosition previous = null, current = null;
			int start, end, gapOffset, gapOffsetStop;
			Position gap = null;
		
			for(int i = 0; i < category.length; i++) 
			{
				current = (TypedPosition)category[i];
				
				AfaePlugin.logDebug(
					"Looking at partition type: "+current.getType().toString() + "(" + offset + " " + length + ")",null,this.getClass()
				);
//				if(previous != null)
//				{
//					try 
//					{
//						String p = fDocument.getContentType(previous.offset);
//						AfaePlugin.logDebug("and previous is: " + p,null, this.getClass());
//					} 
//					catch (BadLocationException e) 
//					{
//						AfaePlugin.logError("computePartitioning",e,AfaePartitioner.class);
//					}
//				}
				
				gapOffset = (previous != null) ? previous.getOffset() + previous.getLength() : 0;
				
				if(gapOffset > current.getOffset()) 
				{
					if(current != null) 
					{
						fDocument.removePosition(getManagingPositionCategories()[0], current);
					}
					continue;
				}
				
				gap = new Position(gapOffset, current.getOffset() - gapOffset);
				
				if(gap.getLength() > 0 && gap.overlapsWith(offset, length)) 
				{
					start = Math.max(offset, gapOffset);
					end = Math.min(endOffset, gap.getOffset() + gap.getLength());
					list.add(new TypedRegion(start, end - start, IDocument.DEFAULT_CONTENT_TYPE));
				}
				
				if (current.overlapsWith(offset, length)) 
				{
					start = Math.max(offset, current.getOffset());
					end = Math.min(endOffset, current.getOffset() + current.getLength());
					
					list.add(new TypedRegion(start, end - start, current.getType()));
				}
				previous = current;
			}
			
			if (previous != null) 
			{
				gapOffset = previous.getOffset() + previous.getLength();
				gapOffsetStop = fDocument.getLength() - gapOffset;
				
				if (gapOffsetStop < 0) 
					gapOffsetStop = 0;
				
				//if(gapOffset > gapOffsetStop)
					gap = new Position(gapOffset, gapOffsetStop);
				//else
				//	gap = new Position(gapOffsetStop, gapOffset);
					
				if(gap.getLength() > 0 && gap.overlapsWith(offset, length)) 
				{
					start = Math.max(offset, gapOffset);
					end = Math.min(endOffset, fDocument.getLength());
					list.add(new TypedRegion(start, end - start, IDocument.DEFAULT_CONTENT_TYPE));
				}
			}
			
			if (list.isEmpty())
				list.add(new TypedRegion(offset, length, IDocument.DEFAULT_CONTENT_TYPE));
			
		} 
		catch (BadPositionCategoryException x) 
		{
			AfaePlugin.logError("computePartitioning offset: " + offset + " length: "+length,x,AfaePartitioner.class);
		}
		
		TypedRegion[] result = new TypedRegion[list.size()];
		
		list.toArray(result);
		return result;
	}
}