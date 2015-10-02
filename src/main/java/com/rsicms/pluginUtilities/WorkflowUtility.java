package com.rsicms.pluginUtilities;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.extensions.Plugin;
import com.reallysi.rsuite.api.workflow.ProcessDefinitionInfo;


public class WorkflowUtility extends PluginUtility {
	Log log = LogFactory.getLog(WorkflowUtility.class);
	
	public WorkflowUtility(ExecutionContext context, Plugin plugin) {
		super(context, plugin);
	}
	public String getWorkflowName(String path) throws RSuiteException {
		InputStream is = getResourceStream(path);
		Document doc;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(is);
		} catch (Throwable t) {
			throw new RSuiteException(RSuiteException.ERROR_DOM_PARSE_ERR, "Could not parse workflow to derive name", t);
		}
		Element el = doc.getDocumentElement();
		return el.getAttribute("name");
	}
	public void importWorkflow(String defPath, String schemaPath) throws RSuiteException {
		User system = context.getAuthorizationService().getSystemUser();
		String workflowName = getWorkflowName(defPath);
		if (workflowName == null) {
			throw new RSuiteException(RSuiteException.ERROR_DOM_PARSE_ERR, "Workflow does not have a name");
		}
		ProcessDefinitionInfo def = context.getProcessDefinitionService().getProcessDefinitionByName(workflowName, -1);
		String workflowXml = getResource(defPath);
		if (null == def || !def.getXml().equals(workflowXml)) {
	    	String workflowSchemaXml = getResource(schemaPath);
	    	context.getProcessDefinitionService().createProcessDefinition(system, workflowXml, workflowSchemaXml);
		}
	}
	
	public void importAllWorkflows(String root) throws RSuiteException {
		Map<String, Integer> items = new HashMap<String, Integer>();
		log.info(String.format("Importing all workflows from %s%n", root));
		for (String item : listResources(root)) {
			String key;
			Integer cv;
			if (item.endsWith(".schema.xml")) {
				key = item.substring(0, item.length() - 11);
				cv = items.containsKey(key) ? items.get(key) : 0;
				items.put(key, cv | 1);
			} else if (item.endsWith(".xml")) {
				key = item.substring(0, item.length() - 4);
				cv = items.containsKey(key) ? items.get(key) : 0;
				items.put(key, cv | 2);				
			}
		}
		for (String key : items.keySet()) {
			if (items.get(key) == 3) {
				try {
					log.info(String.format("Importing \"%s.xml\"%n", key));
					importWorkflow(key + ".xml", key + ".schema.xml");
				} catch (RSuiteException rse) {
					rse.printStackTrace();
				}
			}
		}
	}
}
