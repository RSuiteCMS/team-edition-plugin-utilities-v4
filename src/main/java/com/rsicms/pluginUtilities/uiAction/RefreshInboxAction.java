package com.rsicms.pluginUtilities.uiAction;
import com.reallysi.rsuite.api.remoteapi.result.UserInterfaceAction;


public class RefreshInboxAction extends UserInterfaceAction {
	public RefreshInboxAction(String inboxName) {
		this();
		this.setInboxName(inboxName);
	}
	public RefreshInboxAction() {
		this.setName("teamEdition:refreshInbox");
	}
	public void setInboxName(String inboxName) {
		this.getProperties().put("inboxName", inboxName);
	}
	public String getInboxName() {
		return (String) this.getProperties().get("inboxName");
	}
}
