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
package com.rohanclan.afae.editor;

import java.util.Map;
import java.util.TreeMap;

import com.rohanclan.afae.modes.CatalogReader;
import com.rohanclan.afae.modes.Mode;
import com.rohanclan.afae.modes.Rule;

/**
 * 
 * @author robrohan
 */
public class ModeCache 
{
	protected static ModeCache soleInstace = new ModeCache();
	protected boolean hasBeenLoaded = false;
	
	/** A cache for fully loaded modes */
	protected Map<String, Mode> modes;
	/** a list of mode hulls */
	protected Mode[] modeList;
	
	/**
	 * 
	 */
	protected ModeCache() 
	{
		modes = new TreeMap<String, Mode>();
	}
	
	/**
	 * 
	 * @return
	 */
	public static ModeCache getInstance() 
	{
		return soleInstace;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static Mode getMode(String name) 
	{
		return getInstance().getModeNamed(name);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	public static Mode getModeFor(String filename) 
	{
		return getInstance().getModeForFilename(filename);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
	private Mode getModeForFilename(String filename) 
	{
		if(filename == null) 
			return getModeNamed("text.xml");
		// check to see if it's already loaded
		String modeName = filenameToModeName(filename);
		if(modeName == null) 
			return getModeNamed("text.xml");
		
		return getModeNamed(modeName);
	}

	/**
	 * Try to parse a filename and get the mode out of it so we can load
	 * the proper mode for the file type
	 * @param filename
	 * @return
	 */
	private String filenameToModeName(String filename) 
	{
		Mode[] localModes = getModeList();
	
		if(localModes == null) 
			return null;
		
		for(int i = 0; i < localModes.length; i++) 
		{
			Mode mode = localModes[i];
			if(mode.matches(filename)) return mode.getFilename();
		}
		
		return null;
	}

	/**
	 * Gets a sorted array containing all of the modes defined by the catalog.
	 * @return Mode[]
	 */
	public Mode[] getModeList()
	{
		if(modeList == null) 
		{
			loadCatalog();
		}
		return modeList;
	}

	/**
	 * loads the catalog file
	 */
	protected void loadCatalog() 
	{
		//get a reader
		CatalogReader reader = new CatalogReader();
		//read in the catalog
		modeList = reader.read("modes/catalog");
		
		for(int i = 0; i < modeList.length; i++) 
		{
			Mode mode = modeList[i];
			modes.put(mode.getFilename(), mode);
		}
	}
	
	/**
	 * Gets a mode by name
	 * @param name
	 * @return
	 */
	protected Mode getModeNamed(String name) {
		loadIfNecessary(name);
		return (Mode) modes.get(name);
	}

	/**
	 * checks to see if the mode is already loaded and if so gets that
	 * otherwise loads the mode
	 * @param name
	 */
	private void loadIfNecessary(String name) 
	{
		//get the mode out of the cache
		Mode hull = (Mode)modes.get(name);
		
		//if its not there try to reload the catalog
		if(hull == null) 
		{
			loadCatalog();
			/* this will happen when there was a problem loading the
			 * catalog */
			if(modes.size() == 0) 
				return;
			
			hull = (Mode)modes.get(name);
		}
		
		//if the mode is not loaded load it
		if(hull.notLoaded()) 
			hull.load();
	}

	/**
	 * Answer the Rule set this delegate/rule resolves to. This
	 * may require loading more modes.
	 */
	public static Rule resolveDelegate(Mode mode, String delegateName)
	{
		//delegates with a :: in it requires the loading of another mode
		//as in html + javascript
		int index = delegateName.indexOf("::");
		
		if(index == -1) 
		{
			// Local delegate/rule set
			return mode.getRule(delegateName);
		}
		//the name is like javascript::MAIN so grab the first part and load it
		Mode loadedMode = getMode(delegateName.substring(0, index) + ".xml");
		
		return loadedMode.getRule(delegateName.substring(index + 2));
	}
}