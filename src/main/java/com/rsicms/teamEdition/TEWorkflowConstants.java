package com.rsicms.teamEdition;



public class TEWorkflowConstants {

	public static final String WORKFLOW_PROCESS_NAME = "TEStatusWorkflow";
    public static final String QT_WORKFLOW_PROCESS_NAME = "TEQuickTask";

    public static final String START_WORKFLOW_USERID_PARAM = "rsuiteUserId";
	public static final String WORKFLOW_ID_VAR_NAME_PARAM = "workflowId";
	public static final String PRIMARY_MO_VAR_NAME_PARAM = "moDisplayName";
	public static final String NEXT_STEP_VAR_NAME_PARAM = "nextStep";
	public static final String PRIOR_STEP_VAR_NAME_PARAM = "priorStep";
	public static final String WORKFLOW_STEPS_VAR_NAME_PARAM = "workflowSteps";
	public static final String WORKFLOW_STATUS_VAR_NAME_PARAM = "workflowContentStatus";
	public static final String WORKFLOW_CONTINUATION_VAR_NAME_PARAM = "continueWorkflow";
	public static final String NEXT_STEP_TASK_NAME_VAR_NAME_PARAM = "taskName";
	public static final String NEXT_STEP_USER_ID_VAR_NAME_PARAM = "userId";
    public static final String NEXT_STEP_ROLE_VAR_NAME_PARAM = "sendToRole";
	public static final String NEXT_STEP_DUE_DATE_VAR_NAME_PARAM = "dueDate";
	public static final String NEXT_STEP_RETURN_TO_SENDER_VAR_NAME_PARAM = "return";
    public static final String PROJECT_KEY_VAR_NAME_PARAM = "projectKey";

	public static final String END_WORKFLOW_STEP_VALUE = "(End workflow)";

	public static final String WORKFLOW_NAME_PREFIX = "workflowName";
	public static final String HEAD_ROW_PREFIX = "headrow";
	public static final String ORDER_PREFIX = "order";
    public static final String STEP_PREFIX = "step";
    public static final String ROLE_PREFIX = "role";
	public static final String DATE_PREFIX = "dueDate";
	public static final String DATE_UNIT_PREFIX = "dueDateUnit";
	public static final String INSTRUCTIONS_PREFIX = "instructions";
	public static final String CONTENT_STATUS_PREFIX = "contentStatus";
	public static final String NOTIFY_PREFIX = "notify";
	public static final String REROUTE_PREFIX = "reroute";
	public static final String USER_PREFIX = "user";
	public static final String WORKFLOW_CONFIG_ALIAS_TYPE = "workflow-status-config";

	public static final String CONFIG_FROM_ADDRESS = "rsuite.simple.workflow.from.address";
	public static final String CONFIG_FROM_NAME = "rsuite.simple.workflow.from.name";
	
	public static final String DEFAULT_TASK_DURATION = "1 month";

    public static final String TE_INSPECT_WORKFLOW_URL = "inspectWorkflow/task/";
    public static final String TE_REPORTS_STYLESHEET_URL = "plugin/team-edition-results-reporter/reports.css";

    public static final String TASK_DESCRIPTION_VAR_NAME_PARAM = "taskDescription";
    public static final String NEXT_STEP_TASK_DESCRIPTION_FULL_VAR_NAME_PARAM = "taskDescriptionFull";
    public static final String WORKFLOW_CONFIGURATION_VAR_NAME_PARAM = "workflowConfigurationName";
    public static final String STATUS_LMD = "workflow-content-status";
    public static final String PROJECT_KEY_LMD = "project-key";
    public static final String WORKFLOW_DETAILS_LMD = "_status-workflow-details";
    public static final String WORKFLOW_TASK_LIST_PROPERTY_PREFIX = "inspect.workflow.tasks.";


}
