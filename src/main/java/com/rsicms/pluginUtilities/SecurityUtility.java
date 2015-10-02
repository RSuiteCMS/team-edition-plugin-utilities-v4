package com.rsicms.pluginUtilities;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.security.ExecPermission;
import com.reallysi.rsuite.api.security.ExecPermissionManager;
import com.reallysi.rsuite.api.security.Role;
import com.reallysi.rsuite.api.security.RoleDescriptor;
import com.reallysi.rsuite.api.security.RoleManager;

public class SecurityUtility {
	static private Comparator<Role> roleName;
	static {
        roleName = new Comparator<Role>(){
            @Override
            public int compare(Role r1, Role r2){
                return r1.getName().compareTo(r2.getName());
            }
        };

     }
	/** Create delimited list of the user's roles
	 * @param user
	 * @param delimeter
	 * @return
	 */
	public static String getUserRolesList(User user, String delimeter) {
		Role[] roles = user.getRoles();
		Arrays.sort(roles, roleName);
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (Role role : roles) {
			i++;
			sb.append(role.getName());
			if (i < roles.length) {
				sb.append(delimeter);
			}
		}
		return sb.toString();
	}	
	public static boolean userIsAdmin(User user) {
		return user.hasRole("RSuiteAdministrator");
	}
	public static void assertRoleContainsOne(User user, String failure, String[] roles) throws RSuiteException {
		for (String role : roles) {
			if (user.hasRole(role)) {
				return;
			}
		}
		throw new RSuiteException(failure);	
	}
	public static ExecPermission createExecPermissionIfNotExists(ExecutionContext context, String permission, String description) throws RSuiteException {
		ExecPermissionManager epMgr = context.getAuthorizationService().getExecPermissionManager();
		User system = context.getAuthorizationService().getSystemUser();
		ExecPermission perm = epMgr.getExecPermission(permission);
		if (perm != null) {
			return perm;
		}
		epMgr.createExecPermission(system, permission, description);
		perm = epMgr.getExecPermission(permission);
		return perm;
	}	
	
	public static RoleDescriptor createRoleIfNotExists(ExecutionContext context, String role, String description) throws RSuiteException {
		RoleManager roleMgr = context.getAuthorizationService().getRoleManager();
		RoleDescriptor roleDesc = roleMgr.getRole(role);
		if (roleDesc != null) {
			return roleDesc;
		}
		roleMgr.createRole(null, role, null, description);
		return roleMgr.getRole(role);
	}
	public static String getUserRolesList(User user) {
		return StringUtils.join(user.getRoles(), " ");
	}
}
