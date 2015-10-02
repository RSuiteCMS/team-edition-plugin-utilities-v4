package com.rsicms.teamEdition.models;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WorkflowStatus {
	
	private static Log log = LogFactory.getLog(WorkflowStatus.class);

	private String name = null;
	private String contentStatus = null;
    private String role = "";
    private String defaultUserId = "";
	private String dueDateString = null;
	private String instructions = "";
	private Boolean emailNotification = false;
	private Boolean allowReroute = true;
	
	public WorkflowStatus(Node statusElement) {
		if (statusElement.getAttributes().getNamedItem("name") != null) {
			this.name = statusElement.getAttributes().getNamedItem("name").getTextContent();
		}
		if (statusElement.getAttributes().getNamedItem("content-status") != null) {
			this.contentStatus = statusElement.getAttributes().getNamedItem("content-status").getTextContent();
		}
        if (statusElement.getAttributes().getNamedItem("role") != null) {
            this.role = statusElement.getAttributes().getNamedItem("role").getTextContent();
        }
        if (this.role == null || this.role.isEmpty()) {
            this.role = null;
        }
        if (statusElement.getAttributes().getNamedItem("default-user") != null) {
            this.defaultUserId = statusElement.getAttributes().getNamedItem("default-user").getTextContent();
        }
		if (this.defaultUserId == null || this.defaultUserId.isEmpty()) {
			this.defaultUserId = null;
		}
		if (statusElement.getAttributes().getNamedItem("due-date-offset") != null) {
			this.dueDateString = statusElement.getAttributes().getNamedItem("due-date-offset").getTextContent();
		}
		if (statusElement.getAttributes().getNamedItem("email-notification") != null && 
				statusElement.getAttributes().getNamedItem("email-notification").getTextContent().equalsIgnoreCase("true")) {
			this.emailNotification = true;
		}
		if (statusElement.getAttributes().getNamedItem("allow-to-reroute") != null && 
				statusElement.getAttributes().getNamedItem("allow-to-reroute").getTextContent().equalsIgnoreCase("false")) {
			this.allowReroute = false;
		}
		NodeList instrNode = statusElement.getChildNodes();
		for (int i = 0; i < instrNode.getLength(); i++) {
			if (instrNode.item(i).getNodeType() == Node.ELEMENT_NODE && instrNode.item(i).getLocalName().equals("instructions")) {
				this.instructions = instrNode.item(i).getTextContent();
			}
		}
	}

	public WorkflowStatus(String name, String contentStatus, String role, String defaultUser, String dateOffset, Boolean notify, Boolean reroute, String instructions) {
		log.info("Creating workflow status with values: " + name + " " + contentStatus + " " + defaultUser + " " + dateOffset + " " + notify.toString() + " " + reroute.toString() + " " + instructions);
		this.name = name;
		this.contentStatus = contentStatus;
		if (defaultUser == null || defaultUser.isEmpty()) {
			defaultUser = null;
		}
        this.role = role;
        this.defaultUserId = defaultUser;
		this.dueDateString = dateOffset;
		this.emailNotification = notify;
		this.allowReroute = reroute;
		this.instructions = instructions;
	}

	public String getName() {
		return this.name;
	}
	
	public String getContentStatus() {
		return this.contentStatus;
	}
	
    public String getRole() {
        return this.role;
    }
    
    public String getDefaultUser() {
        return this.defaultUserId;
    }
    
	public String getDueDateString() {
		return this.dueDateString;
	}
	
	public String getInstructions() {
		return this.instructions;
	}
	
	public Boolean getEmailNotification() {
		return this.emailNotification;
	}
	
	public Boolean getAllowReroute() {
		return this.allowReroute;
	}
	
}
