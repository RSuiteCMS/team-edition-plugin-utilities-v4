package com.rsicms.pluginUtilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.xml.XPathEvaluator;
import com.reallysi.rsuite.service.XmlApiManager;

public class XmlUtility {
	private static final Class<?> CLASS = XmlUtility.class;
	private static Log log = LogFactory.getLog(CLASS);
	private static final String CLASSNAME = CLASS.getSimpleName();

	private ExecutionContext context;
	private XmlApiManager xmlMgr;
	private XPathEvaluator evaluator;
	
	public XmlUtility(ExecutionContext context) {
		this.context = context;
		this.xmlMgr = this.context.getXmlApiManager();
		this.evaluator = xmlMgr.getXPathEvaluator();
	}
	

	public Document parse(String xml) throws SAXException, IOException {
		if (xml == null) return null;
		return parse(xml.getBytes());
	}
	public Document parse(byte[] bytes) throws SAXException, IOException {
		return parse(new ByteArrayInputStream(bytes));
	}
	public Document parse(InputStream is) throws SAXException, IOException {
		return xmlMgr.constructNonValidatingDocumentBuilder().parse(is);
	}	
	
	public String getString(Node el, String xpath) {
		String value = "";
		try {
			value = evaluator.executeXPathToString(xpath, el);
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
		}
		return value;
	}
	
	public String[] getStrings(Node el, String xpath) {
		try {
			String[] values = evaluator.executeXPathToStringArray(xpath, el);
			return values;
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
			return new String[0];
		}
	}
	public Boolean getBool(Node el, String xpath) {
		return Boolean.valueOf(getString(el, xpath));
	}
	public Boolean[] getBools(Node el, String xpath) {
		List<Boolean> bools = new ArrayList<Boolean>();
		for (String val : getStrings(el, xpath)) {
			bools.add(Boolean.valueOf(val));
		}
		return bools.toArray(new Boolean[0]);
	}
	public Node getNode(Node el, String xpath) {
		Node ret = null;
		try {
			ret = evaluator.executeXPathToNode(xpath, el);
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
		}
		return ret;		
	}
	public Node[] getNodes(Node el, String xpath) {
		Node[] ret = new Node[0];
		try {
			ret = evaluator.executeXPathToNodeArray(xpath, el);
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
		}
		return ret;		
	}
	//A node list may contain a mix of elements and other nodes; since getElements does this filtering, call it can get the first hit.
	public Element getElement(Node el, String xpath) {
		Element[] els = getElements(el, xpath);
		if (els.length == 0) { return null; }
		return els[0];
	}
	public Element[] getElements(Node el, String xpath) {
		List<Element> els = new ArrayList<Element>();
		for (Node n : getNodes(el, xpath)) {
			if (n instanceof Element) {
				els.add((Element)n);
			}
		}
		return els.toArray(new Element[0]);
	}
	public Integer getInt(Node el, String xpath) {
		try {
			return Integer.valueOf(getString(el, xpath));
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	public Integer[] getIntegers(Node el, String xpath) {
		List<Integer> ret = new ArrayList<Integer>();
		for (String val : getStrings(el, xpath)) {
			try {
				ret.add(Integer.valueOf(val));
			} catch (NumberFormatException nfe) {
				ret.add(null);
			}
		}
		return ret.toArray(new Integer[0]);
	}
	public Double getDouble(Node el, String xpath) {
		try {
			return Double.valueOf(getString(el, xpath));
		} catch (NumberFormatException nfe) {
			return Double.NaN;
		}
	}
	public Double[] getDoubles(Node el, String xpath) {
		List<Double> ret = new ArrayList<Double>();
		for (String val : getStrings(el, xpath)) {
			try {
				ret.add(Double.valueOf(val));
			} catch (NumberFormatException nfe) {
				ret.add(Double.NaN);
			}
		}
		return ret.toArray(new Double[0]);
	}
}
