package com.rsicms.teamEdition;

import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.security.Role;
import com.reallysi.rsuite.api.system.ActiveUser;
import com.reallysi.rsuite.api.system.MailMessageBean;
import com.reallysi.rsuite.service.AuthorizationService;
import com.reallysi.rsuite.service.MailService;
import com.rsicms.pluginUtilities.SecurityUtility;


public class TESecurityUtility extends SecurityUtility {
	private static Log log = LogFactory.getLog(TESecurityUtility.class);
	public static boolean userHasRole(ActiveUser user, String roleName) {
		if (user == null || roleName == null) { return false; }
		for (Role role : user.getRoles()) {
			if (roleName.equals(role.getName())) return true;
		}
		return false;		
	}
	public static boolean userHasRole(User user, String roleName) {
		if (user == null || roleName == null) { return false; }
		return user.hasRole(roleName);
	}
	public static boolean userIsTEUser(User user) {
		return userHasRole(user, TEConstants.TE_USER_ROLE);
	}
	public static boolean userIsTEUser(ActiveUser user) {
		return userHasRole(user, TEConstants.TE_USER_ROLE);
	}
	public static boolean userIsTEAdmin(User user) {
		return userHasRole(user, TEConstants.TE_ADMIN_ROLE);
	}
	public static boolean userIsTEAdmin(ActiveUser user) {
		return userHasRole(user, TEConstants.TE_ADMIN_ROLE);
	}
	public static boolean userIsRSuiteAdmin(User user) {
		return userHasRole(user, "RSuiteAdministrator");
	}
	public static boolean userIsRSuiteAdmin(ActiveUser user) {
		return userHasRole(user, "RSuiteAdministrator");
	}
    public static boolean userIsTEWorkflowAdmin(User user) {
        return userHasRole(user, TEConstants.RSUITE_WORKFLOW_ADMIN_ROLE);
    }
    public static boolean userIsTEWorkflowAdmin(ActiveUser user) {
    	return userHasRole(user, TEConstants.RSUITE_WORKFLOW_ADMIN_ROLE);
    }
    

	public static Integer getTEUserMaxCount(ExecutionContext context) {
		String countS = context.getConfigurationProperties().getProperty(TEConstants.USER_LICENSE_COUNT, "5");
		Integer count = Integer.valueOf(countS);
		return count;
	}
	public static Integer getTEAdminUserCount(ExecutionContext context) throws RSuiteException {
		Integer teAdminCount = 0;
        for (User user : context.getAuthorizationService().getLocalUserManager().getUsers() ) {
			if (userIsTEAdmin(user)) {
				teAdminCount++;	
			}
		}
		return teAdminCount;
	}
	public static Integer getTEUserCount(ExecutionContext context) throws RSuiteException {
		Integer teUserCount = 0;
        for (User user : context.getAuthorizationService().getLocalUserManager().getUsers() ) {
			if (userIsTEUser(user)) {
				teUserCount++;	
			}
		}
		return teUserCount;
	}
	/** Can/can't add another user based on available licenses
	 * @param context
	 * @return Boolean
	 * @throws RSuiteException
	 */
	public static Boolean teUserLicenseAvailable(ExecutionContext context) throws RSuiteException {
		Boolean canAdd = true;
		if (getTEUserCount(context) >= getTEUserMaxCount(context)) {
			canAdd = false;
		}
		return canAdd;
	}
	/** Create a new local user using a generated password, then email the password to the user.  
	 * @param context
	 * @param user Current session user. Must be a TE admin or RSuite admin. 
	 * @param userId User id for new user
	 * @param fullName First and last name of new user
	 * @param email Email address for new user
	 * @param roles Comma-separated list of roles for new user. Should include RSuiteTeamEditionUser.
	 * @throws RSuiteException
	 */
	public static void addTEUser(ExecutionContext context, User user, String userId, String fullName, String email, String roles) throws RSuiteException {
		if (userIsTEAdmin(user) || userIsAdmin(user)) {
			if (teUserLicenseAvailable(context)) {
				String password = generatePassword();
				AuthorizationService authSvc = context.getAuthorizationService();
				authSvc.getLocalUserManager().defineUser(userId, password, fullName, email, roles);
				User newUser = authSvc.getLocalUserManager().getUser(userId);
				String mailBody = "Dear " + newUser.getFullName() + ",\n\n" +
						"An RSuite Team Edition account has been created for you!\n\n" +
						"Username: " + newUser.getUserId() + "\n" +
						"Password: " + password + "\n\n" +
						"Click here to log in: " + getRsuiteTaskUrl(context) + "\n\n" +
						"Regards,\nRSuite Team Edition";
				try {
					sendPasswordEmail(context, newUser, mailBody);
				} catch (RSuiteException e) {
					log.error("Exception sending password to new user:" + e);
					throw new RSuiteException("Exception sending password to new user:" + e);
				}
			} else {
				throw new RSuiteException(TEConstants.MESSAGE_USER_COUNT_EXCEEDED);
			}
		} else {
			throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_ADMIN);
		}
	}
	public static void resetAndMailPassword(ExecutionContext context, User currentUser, String targetUserId) throws RSuiteException {
		if (!userIsTEAdmin(currentUser) && !userIsAdmin(currentUser) && !currentUser.getUserId().equals(targetUserId)) {
			throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_SELF_OR_ADMIN);
		}
		AuthorizationService authSvc = context.getAuthorizationService();
		User targetUser = authSvc.getLocalUserManager().getUser(targetUserId);
		String password = generatePassword();
		authSvc.getLocalUserManager().setUserPassword(targetUserId, password);
		String mailBody = "Dear " + targetUser.getFullName() + ",\n\n" +
				"Your RSuite Team Edition password has been reset. \n\n" +
				"Username: " + targetUser.getUserId() + "\n" +
				"Password: " + password + "\n\n" +
				"Click here to log in: " + getRsuiteTaskUrl(context) + "\n\n" +
				"Regards,\nRSuite Team Edition";
		try {
			sendPasswordEmail(context, targetUser, mailBody);
		} catch (RSuiteException e) {
			log.error("Exception sending new password to user:" + e);
			throw new RSuiteException("Exception sending new password to user:" + e);
		}
	}
	public static String getRsuiteTaskUrl(ExecutionContext context) { 
		return "http://" + context.getRSuiteServerConfiguration().getHostName() + ":" + context.getRSuiteServerConfiguration().getPort() + "/rsuite-cms/tasks/";
	}
	public static String generatePassword() { 
		return RandomStringUtils.randomAlphanumeric(RandomUtils.nextInt(13) + 8);
	}
	public static Boolean teAdminsWillExist(ExecutionContext context, User userToDelete) throws RSuiteException {
		Boolean enoughAdmins = true;
		if (userIsTEAdmin(userToDelete) && getTEAdminUserCount(context) == 1) {
			enoughAdmins = false;
		}
		return enoughAdmins;
	}
	public static void deleteTEUser(ExecutionContext context, User user, String userToDeleteId) throws RSuiteException {
		AuthorizationService authSvc = context.getAuthorizationService();
		User userToDelete = authSvc.findUser(userToDeleteId);
		if (userIsTEAdmin(user) || userIsAdmin(user)) {
			//TODO better to check in UI whether can add more before prevent this way
			if (!userIsTEAdmin(userToDelete) || getTEAdminUserCount(context) > 1) {
				authSvc.getLocalUserManager().removeUser(userToDeleteId);
			} else {
				throw new RSuiteException(TEConstants.MESSAGE_CANNOT_DELETE_ONLY_ADMIN);
			}
		} else {
			throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_ADMIN);
		}
	}
	public static void makeTEUserAnAdmin(ExecutionContext context, User user, String userToMakeAdminId) throws RSuiteException {
		AuthorizationService authSvc = context.getAuthorizationService();
		User userToMakeAdmin = authSvc.findUser(userToMakeAdminId);
		if (userIsTEAdmin(user) || userIsAdmin(user)) {
			if (!userIsTEAdmin(userToMakeAdmin)) {
				String roleList = getUserRolesList(userToMakeAdmin, ",") + "," + TEConstants.TE_ADMIN_ROLE;
				authSvc.getLocalUserManager().updateUser(userToMakeAdminId, userToMakeAdmin.getFullName(), userToMakeAdmin.getEmail(), roleList);
			}
		} else {
			throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_ADMIN);
		}
	}
	public static void updateListOfTEAdmins(ExecutionContext context, User sessionUser, List<String> adminUserIds) throws RSuiteException {
		AuthorizationService authSvc = context.getAuthorizationService();
		if (userIsTEAdmin(sessionUser) || userIsAdmin(sessionUser)) {
			List<User> users = authSvc.getLocalUserManager().getUsers();
			for (User user : users) {
				if (userIsTEUser(user)) {
					if (adminUserIds.contains(user.getUserID())) {
						if (!userIsTEAdmin(user)) {
							String roleList = getUserRolesList(user, ",") + "," + TEConstants.TE_ADMIN_ROLE;
							authSvc.getLocalUserManager().updateUser(user.getUserId(), user.getFullName(), user.getEmail(), roleList);
						}
					} else {
						if (userIsTEAdmin(user)) {
							String roleList = getUserRolesList(user, ",");
							roleList = roleList.replace(TEConstants.TE_ADMIN_ROLE, "");
							authSvc.getLocalUserManager().updateUser(user.getUserId(), user.getFullName(), user.getEmail(), roleList);
						}
					}
				}
			}
		} else {
			throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_ADMIN);
		}
	}
    public static void updateListOfTEWorkflowAdmins(ExecutionContext context, User sessionUser, List<String> workflowAdminUserIds) throws RSuiteException {
        // Could merge with updateListOfTEAdmins, but possible the logic for workflow admins will diverge 
        AuthorizationService authSvc = context.getAuthorizationService();
        if (userIsTEWorkflowAdmin(sessionUser) || userIsAdmin(sessionUser)) {
            List<User> users = authSvc.getLocalUserManager().getUsers();
            for (User user : users) {
                if (userIsTEUser(user)) {
                    if (workflowAdminUserIds.contains(user.getUserID())) {
                        if (!userIsTEWorkflowAdmin(user)) {
                            String roleList = getUserRolesList(user, ",") + "," + TEConstants.RSUITE_WORKFLOW_ADMIN_ROLE;
                            authSvc.getLocalUserManager().updateUser(user.getUserId(), user.getFullName(), user.getEmail(), roleList);
                        }
                    } else {
                        if (userIsTEWorkflowAdmin(user)) {
                            String roleList = getUserRolesList(user, ",");
                            roleList = roleList.replace(TEConstants.RSUITE_WORKFLOW_ADMIN_ROLE, "");
                            authSvc.getLocalUserManager().updateUser(user.getUserId(), user.getFullName(), user.getEmail(), roleList);
                        }
                    }
                }
            }
        } else {
            throw new RSuiteException(TEConstants.MESSAGE_USER_MUST_BE_ADMIN);
        }
    }
	private static void sendPasswordEmail(ExecutionContext context, User user, String mailBody) throws RSuiteException {
		String mailFrom = "teamedition_noreply@rsicms.com";
		String mailSubject = "RSuite Team Edition Password";
		String mailTo = user.getEmail();

        MailMessageBean msg = new MailMessageBean();
        msg.setFrom(mailFrom);
        msg.setSubject(mailSubject);
        msg.setContent(mailBody);
        msg.setTo(mailTo);
        MailService mailSvc = context.getMailService();
		mailSvc.send(msg);
	}

}
