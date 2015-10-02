package com.rsicms.pluginUtilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.extensions.Plugin;

public class PluginUtility {
	protected ExecutionContext context;
	protected Plugin plugin;
	protected Map<String, String> staticContents = new HashMap<String, String>();
	Log log = LogFactory.getLog(PluginUtility.class);
	
	public InputStream getResourceStream(String path) {
		return plugin.getClassLoader().getResourceAsStream(path);
	}
	
	public String getResource(String path) throws RSuiteException {
		InputStream is = getResourceStream(path);
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(is, writer, "UTF-8");
		} catch (IOException ioe) {
			throw new RSuiteException(RSuiteException.ERROR_OBJECT_NOT_FOUND,
					"Couldn't read " + path, ioe);
		}
		return writer.toString();
	}
	
	public Document loadXmlResource(String resourcePath) throws ParserConfigurationException, SAXException, IOException {
		InputStream pluginStream = getResourceStream(resourcePath);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(pluginStream);		
	}
	
	private void readStaticWebservices() throws IOException, ParserConfigurationException, SAXException {
		staticContents.clear();
		Document doc = loadXmlResource("rsuite-plugin.xml");
		NodeList extensionPoints = doc.getDocumentElement().getElementsByTagName("extensionProvider");
		for (int i = 0; i < extensionPoints.getLength(); i += 1) {
			Element webservices = (Element) extensionPoints.item(i);
			if ("rsuite.WebService".equals(webservices.getAttribute("id"))) {
				NodeList sws = webservices.getElementsByTagName("staticWebService");
				for (int j = 0; j < sws.getLength(); j += 1) {
					Element staticService = (Element) sws.item(j);
					String serverUri = staticService.getAttribute("root").substring(1);
					String jarUri = staticService.getAttribute("path").substring(1);
					staticContents.put(serverUri, jarUri);
				}
			}
		}
	}

	public PluginUtility(ExecutionContext context, Plugin plugin) {
		this.context = context;
		this.plugin = plugin;
		try {
			readStaticWebservices();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public String getProperty(String key) {
		if (plugin == null) {
			return null;
		}
		String confId = plugin.getId() + "-config";
		Plugin config;
		String confProp;
		if (null != context
				&& null != (config = context.getPluginManager().get(confId))
				&& null != (confProp = (String) config.getPropertyValue(key))) {
			return confProp;
		}
		return (String) plugin.getPropertyValue(key);
	}
	public List<String> listResources(String jarPath) throws RSuiteException {
		List<String> files = new ArrayList<String>();
		File jar = plugin.getLocation();
		URL jarUrl;
		InputStream jarStream;
		try {
			jarUrl = jar.toURI().toURL();
			jarStream = jarUrl.openStream();
			ZipInputStream zip = new ZipInputStream(jarStream);
			ZipEntry ze;
			while(null != (ze = zip.getNextEntry())) {
				String name = ze.getName();
				if (name.endsWith("/")) {
					continue;
				}
				if (jarPath == null || name.startsWith(jarPath + "/")) {
					files.add(name);
				}
			}
			jarStream.close();
		} catch (IOException e) {
			throw new RSuiteException(RSuiteException.ERROR_FILE_NOT_FOUND, "IO Error when reading plugin " + plugin.getLocation().getAbsolutePath(), e);
		}
		return files;
	}
}
