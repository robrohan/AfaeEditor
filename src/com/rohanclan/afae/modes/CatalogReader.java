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
package com.rohanclan.afae.modes;

import java.io.File;
import java.io.IOException;
//import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.rohanclan.afae.AfaePlugin;
import com.rohanclan.afae.editor.AfaeEditorTools;

/**
 * This class reads the "catalog" file which holds the index of the modes
 * 
 * @author robrohan
 */
public class CatalogReader {
	private static final String ATTR_NAME = "NAME";

	private static final String ATTR_FILE = "FILE";

	private static final String ATTR_FILE_NAMES = "FILE_NAME_GLOB";

	private static final String ATTR_FIRST_LINE = "FIRST_LINE_GLOB";

	/**
	 * C'tor
	 */
	public CatalogReader() {
		super();
	}

	/**
	 * Read the filename file and get all the modes out of it
	 * 
	 * @param filename
	 * @return the modes in the file or a new blank Mode[0]
	 */
	public Mode[] read(String filename) {
		//URL mode = null;
		try {
			return readFile(AfaeEditorTools.getFile(filename));
		} catch (Exception e) {
			AfaePlugin.logError("Error reading catalog file ", e, CatalogReader.class);
			e.printStackTrace();
			return new Mode[0];
		}
	}

	/**
	 * Reads all the modes in the file file
	 * 
	 * @param file
	 * @return
	 * @throws DocumentException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected Mode[] readFile(File file) throws DocumentException, IOException {
		// parse the file into a new DOM and grab the root node
		SAXReader reader = new SAXReader();
		Document doc = null;
		doc = reader.read(file);
		Element root = doc.getRootElement();
		
		List<Element> modeE = root.elements("MODE");

		// assume we'll have about 50 modes (see the catalog file)
		List<Mode> modes = new ArrayList<Mode>(50);

		for (Iterator<Element> iter = modeE.iterator(); iter.hasNext();) {
			Element modeElement = (Element) iter.next();
			modes.add(newMode(modeElement));
		}

		return (Mode[]) modes.toArray(new Mode[modes.size()]);
	}

	/**
	 * Helper function for readFile just grabs the known elements from the
	 * catalog file when making a mode
	 * 
	 * @param modeElement
	 * @return
	 */
	private Mode newMode(Element modeElement) {
		return Mode.newMode(modeElement.attributeValue(ATTR_NAME), modeElement
				.attributeValue(ATTR_FILE), modeElement
				.attributeValue(ATTR_FILE_NAMES), modeElement
				.attributeValue(ATTR_FIRST_LINE));
	}

	/**
	 * This method is used to create the "extensions" attribute for the editor
	 * XML.
	 */
	/*
	 * public static void main(String[] args) { Set list =
	 * getListOfExtensions(); StringBuffer sb = new
	 * StringBuffer("extensions=\"");
	 * 
	 * for(Iterator iter = list.iterator(); iter.hasNext();) { String ext =
	 * (String) iter.next(); sb.append(ext); sb.append(","); }
	 * 
	 * sb.setLength(sb.length() - 1); sb.append("\"");
	 * 
	 * System.out.println(sb.toString()); }
	 * 
	 * public static Set getListOfExtensions() { CatalogReader c = new
	 * CatalogReader(); Mode[] modes = null;
	 * 
	 * try { modes = c.readFile(new
	 * File("/Applications/Users/robrohan/workspace/CW_Fallback/modes/catalog")); }
	 * catch (DocumentException e) { //ignore } catch (IOException e) { //ignore }
	 * 
	 * Set list = new TreeSet(); for(int i = 0; i < modes.length; i++) { Mode
	 * mode = modes[i]; mode.appendExtensionsOnto(list); } return list; }
	 */
}
