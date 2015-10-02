package com.rsicms.teamEdition;

public class TEConstants {

	public static final String RSUITE_USER_ROLE = "RSuiteUser";
    public static final String RSUITE_WORKFLOW_ADMIN_ROLE = "RSuiteWorkflowAdministrator";
	public static final String TE_USER_ROLE = "RSuiteTeamEditionUser";
	public static final String TE_USER_ROLE_DESC = "RSuite Team Edition User";
	public static final String TE_ADMIN_ROLE = "RSuiteTeamEditionAdmin";
	public static final String TE_ADMIN_ROLE_DESC = "Client-scope Administrator for RSuite Team Edition";
	
	public static final String USER_LICENSE_COUNT = "user.license.count";
	public static final String MESSAGE_USER_MUST_BE_ADMIN = "User must be a Team Edition administrator (" + TE_ADMIN_ROLE + ") to perform this action";
	public static final String MESSAGE_USER_COUNT_EXCEEDED = "User count exceeded";
	public static final String MESSAGE_CANNOT_DELETE_ONLY_ADMIN = "Cannot delete the only remaining TE admin user";
	public static final String MESSAGE_USER_MUST_BE_SELF_OR_ADMIN = "Users can only perform this action for themselves or if they are a Team Edition administrator (" + TE_ADMIN_ROLE + ")";

	public static final String[] TE_ADMIN_ROLES = { TE_ADMIN_ROLE, "RSuiteAdministrator"};	
	public static final String USER_MUST_BE_ADMIN = "User must be a Team Edition administrator (" + TE_ADMIN_ROLE + ") to perform this action";

    public static final String TE_FEEDBACK_EMAIL_ADDRESS = "te_feedback@rsuitecms.com";

}
