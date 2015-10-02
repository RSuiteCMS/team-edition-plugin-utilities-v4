package com.rsicms.teamEdition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.reallysi.rsuite.api.Alias;
import com.reallysi.rsuite.api.DataTypeOptionValue;
import com.reallysi.rsuite.api.ManagedObject;
import com.reallysi.rsuite.api.MetaDataItem;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.control.ObjectMetaDataSetOptions;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.remoteapi.CallArgumentList;
import com.reallysi.rsuite.api.remoteapi.RemoteApiExecutionContext;
import com.reallysi.rsuite.api.workflow.ProcessDefinitionInfo;
import com.rsicms.pluginUtilities.LmdUtils;
import com.rsicms.teamEdition.models.TEWorkflowInstanceSettings;
import com.rsicms.teamEdition.models.WorkflowConfiguration;
import com.rsicms.teamEdition.models.WorkflowStatus;

public class TEWorkflowUtility {
    private static Log log = LogFactory.getLog(TEWorkflowUtility.class);

    /*
     * Return alpha sorted list of configured status workflow names as a
     * datatype list
     */
    public static List<DataTypeOptionValue> getTEStatusWorkflowNamesDataType(ExecutionContext context) throws RSuiteException {
        List<DataTypeOptionValue> statusWorkflowOptions = new ArrayList<DataTypeOptionValue>();
        List<String> statusWorkflows = getTEStatusWorkflowNamesList(context);
        for (String statusWorkflow : statusWorkflows) {
            statusWorkflowOptions.add(new DataTypeOptionValue(statusWorkflow, statusWorkflow));
        }
        return statusWorkflowOptions;
    }

    /*
     * Return alpha sorted list of configured status workflow names
     */
    public static List<String> getTEStatusWorkflowNamesList(ExecutionContext context) throws RSuiteException {
        List<String> workflowNameList = new ArrayList<String>();
        List<ManagedObject> configMos = getTEStatusWorkflowMos(context);
        for (ManagedObject configMo : configMos) {
            Alias[] aliases = configMo.getAliases(TEWorkflowConstants.WORKFLOW_CONFIG_ALIAS_TYPE);
            workflowNameList.add(aliases[0].getText());
        }
        Collections.sort(workflowNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        return workflowNameList;
    }

    /*
     * Return alpha sorted list of all workflow names (including status workflow
     * config names) as a datatype list
     */
    public static List<DataTypeOptionValue> getCombinedWorkflowNamesDataType(ExecutionContext context) throws RSuiteException {
        List<DataTypeOptionValue> workflowNameOptions = new ArrayList<DataTypeOptionValue>();
        List<String> workflows = getCombinedWorkflowNamesList(context);
        for (String workflow : workflows) {
            if (workflow.equals(TEWorkflowConstants.QT_WORKFLOW_PROCESS_NAME)) {
                workflowNameOptions.add(new DataTypeOptionValue(workflow, "Quick Task"));
            } else {
                workflowNameOptions.add(new DataTypeOptionValue(workflow, workflow));
            }
        }
        return workflowNameOptions;
    }

    /*
     * Return alpha sorted list of all workflow names, including status workflow
     * config names
     */
    public static List<String> getCombinedWorkflowNamesList(ExecutionContext context) throws RSuiteException {
        List<String> workflowNameList = new ArrayList<String>();
        List<ProcessDefinitionInfo> processDefinitions = context.getProcessDefinitionService().listLatestProcessDefinition(
                context.getAuthorizationService().getSystemUser());
        for (ProcessDefinitionInfo pd : processDefinitions) {
            if (pd.getName().equals(TEWorkflowConstants.WORKFLOW_PROCESS_NAME)) {
                workflowNameList.addAll(getTEStatusWorkflowNamesList(context));
            } else {
                workflowNameList.add(pd.getName());
            }
        }
        Collections.sort(workflowNameList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareToIgnoreCase(o2);
            }
        });
        return workflowNameList;
    }

    /*
     * Return alpha sorted list of all tasks for all workflows, including
     * statuses for TE status workflow configurations
     */
    public static List<DataTypeOptionValue> getCombinedTaskDataType(ExecutionContext context) throws RSuiteException {
        List<DataTypeOptionValue> taskNameOptions = new ArrayList<DataTypeOptionValue>();
        List<String> tasks = getCombinedTaskList(context);
        for (String task : tasks) {
            taskNameOptions.add(new DataTypeOptionValue(task, task));
        }
        return taskNameOptions;
    }

    /*
     * Return alpha sorted list of all tasks for all workflows, including
     * statuses for TE status workflow configurations
     */
    public static List<String> getCombinedTaskList(ExecutionContext context) throws RSuiteException {
        List<String> taskNames = new ArrayList<String>();
        List<ProcessDefinitionInfo> processDefinitions = context.getProcessDefinitionService().listLatestProcessDefinition(
                context.getAuthorizationService().getSystemUser());
        for (ProcessDefinitionInfo pd : processDefinitions) {
            if (pd.getName().equals(TEWorkflowConstants.WORKFLOW_PROCESS_NAME)) {
                List<ManagedObject> configMos = TEWorkflowUtility.getTEStatusWorkflowMos(context);
                for (ManagedObject configMo : configMos) {
                    Node configMoNode = configMo.getElement();
                    WorkflowConfiguration cfg = new WorkflowConfiguration(configMoNode);
                    for (WorkflowStatus status : cfg.getStatusesList()) {
                        if (!taskNames.contains(status.getName())) {
                            taskNames.add(status.getName());
                        }
                    }
                }
            } else {
                String propTasks = getStatusPropertyValue(context, pd.getName()); 
                if (!propTasks.isEmpty()) {
                    String[] moreTasks = propTasks.split(",");
                    for (String task : moreTasks) {
                        if (!taskNames.contains(task.trim())) {
                            taskNames.add(task.trim());
                        }
                    }
                }
            }
        }
        return taskNames;
    }

    /*
     * Return ordered list of tasks for specified workflow (or statuses for a
     * status workflow); the workflowName for a status workflow should be the
     * configuration name, not "TEStatusWorkflow"
     */
    public static List<String> getCompleteTaskList(ExecutionContext context, String workflowName) throws RSuiteException {
        List<String> possibleTasksList = new ArrayList<String>();
        String propTasks = getStatusPropertyValue(context, workflowName); 
        if (!propTasks.isEmpty()) {
            String[] moreTasks = propTasks.split(",");
            for (String task : moreTasks) {
                if (!possibleTasksList.contains(task.trim())) {
                    possibleTasksList.add(task.trim());
                }
            }
        } else if (workflowName.equals(TEWorkflowConstants.QT_WORKFLOW_PROCESS_NAME)) {
            List<ProcessDefinitionInfo> pds = context.getProcessDefinitionService().listLatestProcessDefinition(
                    context.getAuthorizationService().getSystemUser());
            Integer pdv = null;
            for (ProcessDefinitionInfo pdi : pds) {
                if (pdi.getName().equals(workflowName)) {
                    pdv = pdi.getVersion();
                }
            }
            ProcessDefinitionInfo qtpd = context.getProcessDefinitionService().getProcessDefinitionByName(workflowName, pdv);
            String qtDefString = qtpd.getXml();
            try {
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = builderFactory.newDocumentBuilder();
                Document xmlDocument = builder.parse(new ByteArrayInputStream(qtDefString.getBytes()));

                XPath xpath = XPathFactory.newInstance().newXPath();
                String expression = "/*:process-definition/*:task-node/*:task";
                XPathExpression expr = xpath.compile(expression);

                NodeList tasks = (NodeList) expr.evaluate(xmlDocument, XPathConstants.NODESET);
                for (int i = 0; i < tasks.getLength(); i++) {
                    String taskName = tasks.item(i).getAttributes().getNamedItem("name").getTextContent();
                    if (!possibleTasksList.contains(taskName)) {
                        possibleTasksList.add(taskName);
                    }
                }
            } catch (XPathExpressionException e) {
                log.error("Could not get task names using XPath: " + e);
            } catch (ParserConfigurationException e) {
                log.error("Could not get task names using XPath: " + e);
            } catch (SAXException e) {
                log.error("Could not get task names using XPath: " + e);
            } catch (IOException e) {
                log.error("Could not get task names using XPath: " + e);
            }
        } else {
            List<String> statusList = getCompleteStatusList(context, workflowName);
            if (statusList != null) {
                possibleTasksList.addAll(statusList);
            }
        }
        return possibleTasksList;
    }

    public static List<ManagedObject> getTEStatusWorkflowMos(ExecutionContext context) throws RSuiteException {
        User user = context.getAuthorizationService().getSystemUser();
        List<ManagedObject> configMos = context.getManagedObjectService().getObjectsByAlias(user, TEWorkflowConstants.WORKFLOW_CONFIG_ALIAS_TYPE);
        return configMos;
    }

    /*
     * Return ordered list of statuses for a status workflow
     */
    private static List<String> getCompleteStatusList(ExecutionContext context, String configName) throws RSuiteException {
        List<String> statusList = new ArrayList<String>();
        WorkflowConfiguration cfg = WorkflowConfiguration.getExistingWorkflowConfiguration(context, configName);
        if (cfg == null) {
            return null;
        } else {
            for (WorkflowStatus stat : cfg.getStatusesList()) {
                statusList.add(stat.getName());
            }
        }
        return statusList;
    }

    private static String getStatusPropertyValue(ExecutionContext context, String workflowName) {
        String propWfName = workflowName.replaceAll("\\s", "").toLowerCase();
        log.info("Looking for property " + TEWorkflowConstants.WORKFLOW_TASK_LIST_PROPERTY_PREFIX + propWfName);
        String propTasks = context.getConfigurationProperties().getProperty(TEWorkflowConstants.WORKFLOW_TASK_LIST_PROPERTY_PREFIX + propWfName, "");
        if (!propTasks.isEmpty() ) log.info("Found value for " + TEWorkflowConstants.WORKFLOW_TASK_LIST_PROPERTY_PREFIX + propWfName + ": " + propTasks);
        return propTasks;
    }

    public static String launchWorkflowProcess(ExecutionContext context, CallArgumentList args) throws RSuiteException {
        String resultMessage = "";
        String moid = args.getFirstValue("rsuiteId");
        String projectKey = args.getFirstValue("projectKeyText");
        String workflowConfigName = args.getFirstValue("workflowConfig");
        String taskDescription = args.getFirstValue("descriptionText");
        String userId = args.getFirstValue("userId");
        log.info("Starting launch workflow service with values moid = " + moid + ", workflowConfigName = " + workflowConfigName + 
                ", projectKey = " + projectKey + ", userId = " + userId + ", taskDescription = " + taskDescription);

        //TODO add error handling for bad user id
        User user = context.getAuthorizationService().findUser(userId);
        User systemUser = context.getAuthorizationService().getSystemUser();
        ManagedObject mo = context.getManagedObjectService().getManagedObject(systemUser, moid);

        try {
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDateTime = formatter.format(date);
            String wfId = workflowConfigName + "-" + startDateTime + "-" + userId;

            TEWorkflowInstanceSettings settings = new TEWorkflowInstanceSettings();
            settings.setConfigName(workflowConfigName);
            settings.setWorkflowId(wfId);
            settings.setStartDateTime(date + "");
            settings.setOriginator(userId);
            settings.setProjectKey(projectKey);
            for (String argName : args.getNames()) {
                //the argument names have had the more generic css style added to them when building the form, so must truncate
                if (argName.startsWith(TEWorkflowConstants.USER_PREFIX) && argName.contains(" ")) {
                    String stepUserName = args.getFirstValue(argName);
                    String label = argName.split(" ")[0];
                    settings.setUserAssignment(label, stepUserName);
                    Boolean isARole = false;
                    String[] roles = context.getAuthorizationService().getAllRoles();
                    for (String role : roles) {
                        if (role.equals(stepUserName)) {
                            isARole = true;
                            continue;
                        }
                    }
                    if (isARole == false) {
                        try {
                            User stepUser = context.getAuthorizationService().findUser(stepUserName);
                        } catch (Exception e) {
                            log.error("Failed to find user " + stepUserName);
                            resultMessage = "Unknown user " + stepUserName + " included for " + workflowConfigName + ". Could not begin workflow.";
                        }
                    }
                } else if (argName.startsWith(TEWorkflowConstants.DATE_PREFIX)) {
                    String value = argName.split(" ")[0];
                    settings.setDueDate(value, args.getFirstValue(argName));
                } else if (argName.startsWith(TEWorkflowConstants.INSTRUCTIONS_PREFIX)) {
                    String value = argName.split(" ")[0];
                    settings.setInstruction(value, args.getFirstValue(argName.replaceAll(";", ",")));
                }
            }
            settings.setCurrentStatus("launch");

            LmdUtils.createLmdFieldIfDoesntExist(context, TEWorkflowConstants.WORKFLOW_DETAILS_LMD, false, true, false);
            LmdUtils.addElementToLmdIfNotAlready(context, TEWorkflowConstants.WORKFLOW_DETAILS_LMD, mo.getNamespaceURI(), mo.getLocalName());
            LmdUtils.createLmdFieldIfDoesntExist(context, TEWorkflowConstants.STATUS_LMD, false, true, false);
            LmdUtils.addElementToLmdIfNotAlready(context, TEWorkflowConstants.STATUS_LMD, mo.getNamespaceURI(), mo.getLocalName());
            LmdUtils.createLmdFieldIfDoesntExist(context, TEWorkflowConstants.PROJECT_KEY_LMD, false, true, false);
            LmdUtils.addElementToLmdIfNotAlready(context, TEWorkflowConstants.PROJECT_KEY_LMD, mo.getNamespaceURI(), mo.getLocalName());

            // This LMD field is unversioned and so no need to check out
            // document before add it.
            MetaDataItem mdi = new MetaDataItem(TEWorkflowConstants.WORKFLOW_DETAILS_LMD, settings.constructWorkflowInstanceConfig());
            ObjectMetaDataSetOptions opts = new ObjectMetaDataSetOptions();
            opts.setAddNewItem(true);
            context.getManagedObjectService().addMetaDataEntry(user, moid, mdi, opts);

            // Add project key as lmd if not already present
            List<MetaDataItem> mdis = mo.getMetaDataItems();
            Boolean keyPresent = false;
            for (MetaDataItem keyMdi : mdis) {
                if (keyMdi.getName().equals(TEWorkflowConstants.PROJECT_KEY_LMD) && keyMdi.getValue().equals(projectKey)) {
                    keyPresent = true;
                }
            }
            if (keyPresent == false) {
                MetaDataItem keyMdi = new MetaDataItem(TEWorkflowConstants.PROJECT_KEY_LMD, projectKey);
                ObjectMetaDataSetOptions options = new ObjectMetaDataSetOptions();
                options.setAddNewItem(true);
                context.getManagedObjectService().addMetaDataEntry(user, moid, keyMdi, options);
            }

            Map<String, Object> startVars = new HashMap<String, Object>();
            startVars.put("rsuite contents", moid);
            startVars.put(TEWorkflowConstants.PRIMARY_MO_VAR_NAME_PARAM, context.getManagedObjectService().getManagedObject(user, moid).getDisplayName());
            startVars.put(TEWorkflowConstants.WORKFLOW_ID_VAR_NAME_PARAM, wfId);
            startVars.put(TEWorkflowConstants.WORKFLOW_CONFIGURATION_VAR_NAME_PARAM, workflowConfigName);
            startVars.put(TEWorkflowConstants.TASK_DESCRIPTION_VAR_NAME_PARAM, taskDescription);
            startVars.put(TEWorkflowConstants.PROJECT_KEY_VAR_NAME_PARAM, projectKey);
            startVars.put(TEWorkflowConstants.START_WORKFLOW_USERID_PARAM, userId);
            startVars.put("workflowConfigLmdValue", settings.constructWorkflowInstanceConfig());

            context.getProcessInstanceService().createAndStart(user, TEWorkflowConstants.WORKFLOW_PROCESS_NAME, startVars);

        } catch (Exception e) {
            log.error("Received " + e.getClass().getSimpleName() + " exception from launching workflow: " + e.getMessage(), e);
        }
        return resultMessage;
    }

}
