package com.rsicms.pluginUtilities.datatype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.DataTypeOptionValue;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.forms.DataTypeProviderOptionValuesContext;
import com.reallysi.rsuite.api.forms.DefaultDataTypeOptionValuesProviderHandler;
import com.reallysi.rsuite.service.AuthorizationService;

/**
 * Provides lists of users defined in RSuite who have ALL of the provided roles.
 * 
 */
public class UserListProvider extends
		DefaultDataTypeOptionValuesProviderHandler {
	private static Log log = LogFactory.getLog(UserListProvider.class);

	/**
	 * RSuite has received a request for a data type and is asking the provider
	 * handler provide the option values.
	 */
	public void provideOptionValues(
			DataTypeProviderOptionValuesContext context,
			List<DataTypeOptionValue> optionValues) throws RSuiteException {
		log.info("provideOptionValues(): Start...");
		String roleNames = (String) this.dataType.getProperties().get(
				"roleNames");

		Boolean optionalValue = Boolean.getBoolean((String) this.dataType
				.getProperties().get("optional"));

		populateOptions(context, optionValues, roleNames, optionalValue);
	}

    static public void populateOptions(ExecutionContext context,
            List<DataTypeOptionValue> optionValues, String roleNames,
            Boolean optionalValue) throws RSuiteException {
        log.info("provideOptionValues.populateOptions(): roleNames for user list filtering are: "
                + roleNames);

        List<String> rolesToCheck = new ArrayList<String>();
        if (!StringUtils.isEmpty(roleNames)) {
            for (String roleName : StringUtils.split(roleNames, ",")) {
                rolesToCheck.add(roleName.trim());
            }
        }

        List<DataTypeOptionValue> userValues = new ArrayList<DataTypeOptionValue>();
        if (optionalValue == true) {
            userValues.add(new DataTypeOptionValue("", ""));
        }
        AuthorizationService authSvc = context.getAuthorizationService();
        for (User user : authSvc.getUserManager().getUsers()) {
            if (rolesToCheck.size() > 0) {
                for (String roleName : rolesToCheck) {
                    if (user.hasRole(roleName)) {
                        addUserOption(userValues, user);
                        break;
                    }
                }
            } else {
                addUserOption(userValues, user);
            }
        }
        // Sort by label value.
        Collections.sort(userValues, new Comparator<DataTypeOptionValue>() {
            public int compare(DataTypeOptionValue n1, DataTypeOptionValue n2) {
                return n1.getLabel().compareToIgnoreCase(n2.getLabel());
            }
        });

        optionValues.addAll(userValues);

        log.info("provideOptionValues.populateOptions(): "
                + userValues.size()
                + " users matched criteria and returned in datatype");
        log.info("provideOptionValues.populateOptions(): Done.");
    }

    private static void addUserOption(List<DataTypeOptionValue> userValues, User user) {
        DataTypeOptionValue value = new DataTypeOptionValue(
                user.getUserId(), user.getFullName());
        userValues.add(value);
    }

}
