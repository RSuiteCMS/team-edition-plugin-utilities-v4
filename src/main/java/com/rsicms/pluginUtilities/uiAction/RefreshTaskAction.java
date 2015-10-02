package com.rsicms.pluginUtilities.uiAction;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.taskmgmt.exe.TaskInstance;

import com.reallysi.rsuite.api.remoteapi.result.UserInterfaceAction;
import com.reallysi.rsuite.api.workflow.TaskInfo;

public class RefreshTaskAction extends UserInterfaceAction {
	public RefreshTaskAction() {
		setName("teamEdition:refreshTasks");
		getProperties().put("taskIds", new ArrayList<String>());
	}
	@SuppressWarnings("unchecked")
	public List<String> getTasks() {
		return (List<String>) getProperties().get("taskIds");
	}
	public void addTask(String taskId) {
		getTasks().add(taskId);
	}
	public void addTask(TaskInfo task) {
		addTask(String.valueOf(task.getTaskInstanceId()));
	}
	public void addTask(TaskInstance task) {
		addTask(String.valueOf(task.getId()));
	}
}
