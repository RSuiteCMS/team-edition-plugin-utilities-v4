package com.rsicms.pluginUtilities.datatype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.api.forms.DataTypeProviderOptionValuesContext;
import com.reallysi.rsuite.api.forms.DefaultDataTypeOptionValuesProviderHandler;
import com.reallysi.rsuite.service.AuthorizationService;
import com.reallysi.rsuite.api.security.RoleDescriptor;
import com.reallysi.rsuite.api.security.RoleManager;
import com.reallysi.rsuite.api.DataTypeOptionValue;
import com.reallysi.rsuite.api.RSuiteException;

public class RoleListProvider extends DefaultDataTypeOptionValuesProviderHandler {

    // private Log log = LogFactory.getLog(RoleListProvider.class);

    /**
     * RSuite has received a request for a data type and is asking the provider
     * handler provide the option values.
     */
    public void provideOptionValues(DataTypeProviderOptionValuesContext context, List<DataTypeOptionValue> optionValues) throws RSuiteException {

        Boolean optionalValue = Boolean.getBoolean((String) this.dataType.getProperties().get("optional"));

        populateOptions(context, optionValues, optionalValue);
    }

    static public void populateOptions(ExecutionContext context, List<DataTypeOptionValue> optionValues, Boolean optionalValue)
            throws RSuiteException {
        AuthorizationService authSvc = context.getAuthorizationService();
        RoleManager roleMgr = authSvc.getRoleManager();

        List<DataTypeOptionValue> roleValues = new ArrayList<DataTypeOptionValue>();

        if (optionalValue) {
            roleValues.add(new DataTypeOptionValue("", ""));
        }

        List<RoleDescriptor> roleList = roleMgr.getRoles();
        for (RoleDescriptor role : roleList) {
            String roleName = role.getName();
            roleValues.add(new DataTypeOptionValue(roleName, roleName));
        }
        
        // Sort by label value.
        Collections.sort(roleValues, new Comparator<DataTypeOptionValue>() {
            public int compare(DataTypeOptionValue n1, DataTypeOptionValue n2) {
                return n1.getLabel().compareToIgnoreCase(n2.getLabel());
            }
        });

        optionValues.addAll(roleValues);
    }
}
