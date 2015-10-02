package com.rsicms.teamEdition.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.reallysi.rsuite.api.ManagedObject;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.ExecutionContext;

public class WorkflowConfiguration {
	
	private static Log log = LogFactory.getLog(WorkflowConfiguration.class);

	private String workflowName = "";
	private List<WorkflowStatus> workflowStatuses = new ArrayList<WorkflowStatus>();
	
	public WorkflowConfiguration(Node configMoNode) {
		this.workflowName = configMoNode.getAttributes().getNamedItem("name").getTextContent();
		NodeList statusNodes = configMoNode.getChildNodes();
		for (int i = 0; i < statusNodes.getLength(); i++) {
			if (statusNodes.item(i).getNodeType() == Node.ELEMENT_NODE && statusNodes.item(i).getLocalName().equals("status")) {
				WorkflowStatus status = new WorkflowStatus(statusNodes.item(i));
				this.workflowStatuses.add(status);
			}
		}
	}

    public static WorkflowConfiguration getExistingWorkflowConfiguration(ExecutionContext context, String configName) throws RSuiteException {
        ManagedObject configMo = context.getManagedObjectService().getObjectByAlias(context.getAuthorizationService().getSystemUser(), configName);
        if (configMo == null) {
            return null;
        }
        return new WorkflowConfiguration(configMo.getElement());
    }
    
	public static Document constructWorkflowStatusDocument(ExecutionContext context, String workflowName, List<WorkflowStatus> statuses) throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = context.getXmlApiManager().getDocumentBuilderFactory();
		docFactory.setNamespaceAware(true);
		DocumentBuilder builder = docFactory.newDocumentBuilder();
		
		Document newWorkflowDoc = builder.newDocument();
		newWorkflowDoc.setXmlStandalone(true);
		Element newWorkflowElement = newWorkflowDoc.createElement("status-workflow");
		newWorkflowDoc.appendChild(newWorkflowElement);

		newWorkflowElement.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi",
				"http://www.w3.org/2001/XMLSchema-instance");
		newWorkflowElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation",
				"status-workflow.xsd");
		newWorkflowElement.setAttribute("name", workflowName);
		
		for (WorkflowStatus status : statuses) {
			log.info("Adding workflow status element to document with values: " + status.getName() + " " + status.getContentStatus() + " " + status.getDefaultUser() + " " + status.getDueDateString() + " " + status.getEmailNotification().toString() + " " + status.getAllowReroute().toString() + " " + status.getInstructions());
			Element newStatusElement = newWorkflowDoc.createElement("status");
			newStatusElement.setAttribute("name", status.getName());
			newStatusElement.setAttribute("content-status", status.getContentStatus());
            newStatusElement.setAttribute("role", status.getRole());
            newStatusElement.setAttribute("default-user", status.getDefaultUser());
			newStatusElement.setAttribute("due-date-offset", status.getDueDateString());
			newStatusElement.setAttribute("email-notification", status.getEmailNotification().toString());
			newStatusElement.setAttribute("allow-to-reroute", status.getAllowReroute().toString());
			Element newInstructionsElement = newWorkflowDoc.createElement("instructions");
			newInstructionsElement.setTextContent(status.getInstructions());
			newStatusElement.appendChild(newInstructionsElement);
			newWorkflowElement.appendChild(newStatusElement);
		}
		return newWorkflowDoc;
	}

	public String getWorkflowName() {
		return this.workflowName;
	}

	public List<WorkflowStatus> getStatusesList() {
		return this.workflowStatuses;
	}

	public WorkflowStatus getStatusByName(String name) {
		WorkflowStatus returnStatus = null;
		for (WorkflowStatus status : this.workflowStatuses) {
			if (status.getName().equals(name)) {
				returnStatus = status;
				break;
			}
		}
		return returnStatus;
	}
	
}
