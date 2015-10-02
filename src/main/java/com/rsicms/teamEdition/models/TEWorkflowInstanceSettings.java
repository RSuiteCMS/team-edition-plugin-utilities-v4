package com.rsicms.teamEdition.models;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.rsicms.teamEdition.TEWorkflowConstants;

public class TEWorkflowInstanceSettings {
	
	private Map<String,String> userAssignments = new HashMap<String,String>();
	private Map<String,String> dueDates = new HashMap<String,String>();
	private Map<String,String> instructions = new HashMap<String,String>();
	private String configName = null;
    private String wfId = null;
    private String projectKey = null;
	private String startDateTime = null;
	private String originator = null;
    private String currentStatus = null;
    
    final private String fieldWorkflowConfigName = "workflow-config-name";
    final private String fieldWorkflowId = "workflow-id";
    final private String fieldDateTime = "date-time";
    final private String fieldProjectKey = "project-key";
    final private String fieldOriginator = "originator";
    final private String fieldCurrentStatus = "current-status";
    final private String sep = ":";
	
    private static Log log = LogFactory.getLog(TEWorkflowInstanceSettings.class);
    
    public TEWorkflowInstanceSettings() {
    }
    
    public TEWorkflowInstanceSettings(String settings) {
		String[] wfConfigInfoParts = settings.split(";");
		for (String configInfo : wfConfigInfoParts) {
		    configInfo = configInfo.trim();
			String[] ci = configInfo.split(sep);
			if (ci[0].equals(fieldWorkflowConfigName)) {
				this.configName = ci[1];
            } else if (ci[0].equals(fieldWorkflowId)) {
                this.wfId = configInfo.replace(fieldWorkflowId + sep, "");
            } else if (ci[0].equals(fieldProjectKey)) {
                this.projectKey = configInfo.replace(fieldProjectKey + sep, "");
            } else if (ci[0].equals(fieldDateTime)) {
                this.startDateTime = configInfo.replace(fieldDateTime + sep, "");
			} else if (ci[0].equals(fieldOriginator)) {
				this.originator = configInfo.replace(fieldOriginator + sep, "");
            } else if (ci[0].equals(fieldCurrentStatus)) {
                this.currentStatus = configInfo.replace(fieldCurrentStatus + sep, "");
			} else if (ci[0].startsWith(TEWorkflowConstants.USER_PREFIX)) {
			    this.userAssignments.put(ci[0], ci[1]);
			} else if (ci[0].startsWith(TEWorkflowConstants.DATE_PREFIX)) {
				this.dueDates.put(ci[0], ci[1]);
			} else if (ci[0].startsWith(TEWorkflowConstants.INSTRUCTIONS_PREFIX)) {
				String instructions = "";
				if (ci.length > 1)
					instructions = ci[1];
				this.instructions.put(ci[0], instructions);
			}
		}
	}

	
	public  Map<String,String> getUserAssignments() {
		return this.userAssignments;
	}

    public void setUserAssignment(String stepName, String userAssignment) {
        this.userAssignments.put(stepName, userAssignment);
    }

	public  Map<String,String> getDueDates() {
		return this.dueDates;
	}

    public void setDueDate(String stepName, String dueDate) {
        this.dueDates.put(stepName, dueDate);
    }

    public  Map<String,String> getInstructions() {
		return this.instructions;
	}

    public void setInstruction(String stepName, String instruction) {
        this.instructions.put(stepName, instruction);
    }

	public String getConfigName() {
		return this.configName;
	}

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getProjectKey() {
        return this.projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getWorkflowId() {
        return this.wfId;
    }

    public void setWorkflowId(String wfId) {
        this.wfId = wfId;
    }

	public String getOriginator() {
		return this.originator;
	}

    public void setOriginator(String originator) {
        this.originator = originator;
    }

	public String getStartDateTime() {
		return this.startDateTime;
	}

    public void setStartDateTime(String dateTime) {
        this.startDateTime = dateTime;
    }

    public String getCurrentStatus() {
        return this.currentStatus;
    }
    
    public void setCurrentStatus(String status) {
        this.currentStatus = status;
    }

    public String constructWorkflowInstanceConfig() {
        String endSep = "; ";
        StringBuffer sb = new StringBuffer("");
        sb.append(fieldWorkflowConfigName + sep + this.configName + endSep);
        sb.append(fieldWorkflowId + sep + this.wfId + endSep);
        sb.append(fieldDateTime + sep + this.startDateTime + endSep);
        sb.append(fieldOriginator + sep + this.originator + endSep);
        sb.append(fieldProjectKey + sep + this.projectKey + endSep);
        Map<String, String> assignments = this.userAssignments;
        for (String key : assignments.keySet()) {
            sb.append(key + sep + assignments.get(key) + endSep);
        }
        Map<String, String> dueDates = this.dueDates;
        for (String key : dueDates.keySet()) {
            sb.append(key + sep + dueDates.get(key) + endSep);
        }
        Map<String, String> instructions = this.instructions;
        for (String key : instructions.keySet()) {
            sb.append(key + sep + instructions.get(key) + endSep);
        }
        sb.append(fieldCurrentStatus + sep + this.currentStatus + endSep);
        return sb.toString();
    }
}
