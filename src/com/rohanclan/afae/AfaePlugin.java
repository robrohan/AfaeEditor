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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.rohanclan.afae.editor.AfaeEditorTools;
//import com.rohanclan.afae.outline.IAfaeOutlinePage;
import com.rohanclan.afae.rules.ColorManager;

/**
 * The main plugin class to be used in the desktop.
 */
public class AfaePlugin extends AbstractUIPlugin implements IAfaeExtensionIds, IImageConstants
{
	public static final String STATUS_CATEGORY_MODE = "com.rohanclan.afae.modeStatus";
	private static final byte LOG_ERROR 	= 0;
	private static final byte LOG_WARN 	= 1;
	private static final byte LOG_DEBUG 	= 2;
	private static final byte LOG_INFO 	= 3;
	
	//The shared instance.
	private static AfaePlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private AfaeEditorTools editorTools;
	private static ColorManager colorManager;
	
	private static List<ILogListener> loglisteners;
	
	/**
	 * The constructor.
	 */
	public AfaePlugin()
	{
		super();
		plugin = this;
		
		try 
		{
			resourceBundle = ResourceBundle.getBundle("plugin");
		} 
		catch(MissingResourceException x) 
		{
			x.printStackTrace(System.err);
			resourceBundle = null;
		}
	}
	
	/**
	 * This method is called upon plug-in activation. Seems like most startup 
	 * stuff should now go here.
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		AfaePlugin.logInfo("Afae starting...", null, AfaePlugin.class);
	}

	
	/**
	 * Gets the color manager
	 * @return
	 */
	public ColorManager getColorManager() 
	{	
		if (colorManager == null)
		{
			colorManager = new ColorManager(getPreferenceStore());
		}
		return colorManager;
	}
	
	/**
	 * Gets a list of log listeners from the plugin.xml files
	 * @return
	 */
	private static List<ILogListener> getLogListeners()
	{
		if(loglisteners == null)
		{
			loglisteners = computeLogListeners();
		}
		return loglisteners;
	}
	
	/**
	 * 
	 * @return
	 */
	private static List<ILogListener> computeLogListeners()
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXT_LOG_LISTENER_ID);
		
		IExtension[] extension = extensionPoint.getExtensions();
		
		ArrayList<ILogListener> results = new ArrayList<ILogListener>();
		
		//loop over every extension for this ID
		for(int i=0; i < extension.length; i++)
		{
			//get all the sub elements for this ID
			IConfigurationElement[] elements = extension[i].getConfigurationElements();
			for(int j=0; j<elements.length; j++)
			{
				try
				{
					//try to get the attribute "class" and create an instance of it
					Object listener = elements[j].createExecutableExtension("class");
					if(listener instanceof ILogListener)
					{
						results.add((ILogListener)listener);
					}
				}
				catch(CoreException ce)
				{
					//AfaePlugin.logError("computeLogListeners",ce,AfaePlugin.class);
					ce.printStackTrace(System.err);
				}
			}
		}
		
		return results;
	}
	
	/**
	 * 
	 * @param type
	 * @return
	 */
	/* public static IAfaeOutlinePage getOutliner(String type)
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(EXT_OUTLINE_PROVIDER);
		
		//if(extensionPoint == null)
		//	return null;
		
		IExtension[] extension = extensionPoint.getExtensions();
		
		//loop over every extension for this ID
		for(int i=0; i < extension.length; i++)
		{
			//get all the sub elements for this ID
			IConfigurationElement[] elements = extension[i].getConfigurationElements();
			
			for(int j=0; j<elements.length; j++)
			{
				AfaePlugin.logDebug("looking at ext point: " + j,null,AfaePlugin.class);
				
				try
				{
					//get the extensions this outline supports
					String exttype = elements[j].getAttribute("exttype");
					
					AfaePlugin.logDebug("got: " + exttype,null,AfaePlugin.class);
					
					if(exttype.indexOf(type) > -1)
					{	
						AfaePlugin.logDebug("got: " + exttype,null,AfaePlugin.class);
						
						//try to get the attribute "class" and create an instance of it
						Object provider = elements[j].createExecutableExtension("class");					
						if(provider instanceof IAfaeOutlinePage)
						{
							return (IAfaeOutlinePage)provider;
						}
					}
				}
				catch(CoreException ce)
				{
					AfaePlugin.logError("getOutliner",ce,AfaePlugin.class);
				}
			}
		}
		
		return null;
	}*/
	
	/**
	 * has to do with the partitions... used to get mode file rules to eclipse 
	 * partition rules mostly.
	 * @return
	 */
	public AfaeEditorTools getEditorTools() 
	{
		if (editorTools == null)
			editorTools = new AfaeEditorTools();
		
		return editorTools;
	}
	
	/**
	 * Returns the shared instance.
	 */
	public static AfaePlugin getDefault() 
	{
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() 
	{
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Gets a string from the resource bundle
	 * 
	 * @param key
	 * @return the string from the plugin's resource bundle, or 'key' if not found.
	 */
	public static String getResourceString(String key) 
	{
		ResourceBundle bundle= AfaePlugin.getDefault().getResourceBundle();
		try 
		{
			return bundle.getString(key);
		}
		catch(MissingResourceException e) 
		{
			AfaePlugin.logWarn("Asked for unknown key: "+key,null,AfaePlugin.class);
			return key;
		}
	}

	/**
	 * Loads an image from the icons/ directory
	 * @param imageName
	 * @return
	 */
	protected static ImageDescriptor getImageDescriptor(String imageName) 
	{
		String iconPath = "icons/";
		try 
		{
			URL installURL = getDefault().getBundle().getEntry("/");
			URL url = new URL(installURL, iconPath + imageName);
			
			return ImageDescriptor.createFromURL(url);
		}
		catch(MalformedURLException e) 
		{
			AfaePlugin.logWarn("Asked for unknown image: "+imageName,null,AfaePlugin.class);
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/**
	 * Gets the full resource bundle 
	 * @return the plugin's resource bundle
	 */
	public ResourceBundle getResourceBundle() 
	{
		return resourceBundle;
	}
	
	/**
	 * Loads up any images into the registry so it can be used 
	 * in the plugin.
	 */
	protected void initializeImageRegistry(ImageRegistry reg) 
	{
		super.initializeImageRegistry(reg);
		
		reg.put(IMG_BOX,getImageDescriptor(DIR_OBJ_16+IMG_BOX).createImage());
		reg.put(IMG_AFAE16,getImageDescriptor(DIR_OBJ_16+IMG_AFAE16).createImage());
		//reg.put("box", getImageDescriptor("boxIcon.gif").createImage());
	}

	/**
	 * Loads an images from the image registry
	 * @param handle
	 * @return
	 */
	public static Image getImage(String handle) 
	{
		return getDefault().getImageRegistry().get(handle);
	}

	/////////////////////////////// LOGGING STUFF ///////////////////////////////////
	
	/**
	 * Log error to any log extension points
	 *
	 * @param message the message to log
	 * @param exception the exception
	 * @param klass the class where the exception happened (Thing.class)
	 */
	@SuppressWarnings("unchecked")
	public static void logError(String message, Exception exception, Class klass) 
	{
		logCommon(message,exception,klass,LOG_ERROR);
	}
	
	/**
	 * Log warnings to any log extension points
	 *
	 * @param message the message to log
	 * @param exception the exception
	 * @param klass the class where the exception happened (Thing.class)
	 */
	@SuppressWarnings("unchecked")
	public static void logWarn(String message, Exception exception, Class klass) 
	{
		logCommon(message,exception,klass,LOG_WARN);
	}
	
	/**
	 * Log debug statements to any log extension points
	 *
	 * @param message the message to log
	 * @param exception the exception
	 * @param klass the class where the exception happened (Thing.class)
	 */
	@SuppressWarnings("unchecked")
	public static void logDebug(String message, Exception exception, Class klass) 
	{
		logCommon(message,exception,klass,LOG_DEBUG);
	}
	
	/**
	 * Log debug statements to any log extension points
	 *
	 * @param message the message to log
	 * @param exception the exception
	 * @param klass the class where the exception happened (Thing.class)
	 */
	@SuppressWarnings("unchecked")
	public static void logInfo(String message, Exception exception, Class klass) 
	{
		logCommon(message,exception,klass,LOG_INFO);
	}
	
	/**
	 * Helper class for the logging calls
	 * 
	 * @param message
	 * @param e
	 * @param klass
	 * @param type
	 */
	@SuppressWarnings("unchecked")
	private static void logCommon(final String message, final Exception e, final Class klass, final byte type)
	{
		for(final Iterator<ILogListener> all = getLogListeners().iterator(); all.hasNext();)
		{
			final ILogListener each = (ILogListener)all.next();
			
			ISafeRunnable runnable = new ISafeRunnable()
			{
				public void handleException(Throwable ex){	
					all.remove();
				}
				public void run() throws Exception
				{
					switch(type)
					{
						case LOG_ERROR:
							each.logError(message,e,klass);
							break;
						case LOG_WARN:
							each.logWarn(message,e,klass);
							break;
						case LOG_DEBUG:
							each.logDebug(message,e,klass);
							break;
						case LOG_INFO:
							each.logInfo(message,e,klass);
							break;
					}
				}
			};
			//Platform.run(runnable);
			SafeRunner.run(runnable);
		}
	}
}
