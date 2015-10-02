package com.rsicms.pluginUtilities;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.DataType;
import com.reallysi.rsuite.api.ElementMatchingCriteria;
import com.reallysi.rsuite.api.ElementMatchingOptions;
import com.reallysi.rsuite.api.LayeredMetadataDefinition;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.User;
import com.reallysi.rsuite.api.extensions.ExecutionContext;
import com.reallysi.rsuite.service.MetaDataService;

/**
 * Collection of shortcut methods to create form parameters.
 */
public class LmdUtils {
    private static Log log = LogFactory.getLog(LmdUtils.class);

    /**
     * @param context
     * @param lmdName
     * @param isVersionable
     * @param allowsMultiple
     * @param allowsContextual
     * @throws RSuiteException
     */
    public static void createLmdFieldIfDoesntExist(ExecutionContext context, String lmdName, Boolean isVersionable, Boolean allowsMultiple,
            Boolean allowsContextual) throws RSuiteException {

        MetaDataService metaSvc = context.getMetaDataService();
        User user = context.getAuthorizationService().getSystemUser();

        LayeredMetadataDefinition def = metaSvc.getLayeredMetaDataDefinition(user, lmdName);
        if (def == null) {
            DataType dt = null;
            List<ElementMatchingCriteria> elemSet = null;
            metaSvc.createLayeredMetaDataDefinition(user, lmdName, "string", isVersionable, allowsMultiple, allowsContextual, elemSet, dt);
        }

    }

    /**
     * @param context
     * @param lmdName
     * @param namespace
     * @param elementLocalName
     * @throws RSuiteException
     */
    public static void addElementToLmdIfNotAlready(ExecutionContext context, String lmdName, String namespace, String elementLocalName) throws RSuiteException {

        MetaDataService metaSvc = context.getMetaDataService();
        User user = context.getAuthorizationService().getSystemUser();
        LayeredMetadataDefinition def = metaSvc.getLayeredMetaDataDefinition(user, lmdName);
        if (def == null) {
            throw new RSuiteException("No metadata definition for " + lmdName);
        }

        ElementMatchingCriteria[] elemSet = def.getElementCriteria();

        if (elemSet == null || def.isAssociatedWithElementCriteria(namespace, elementLocalName, new ElementMatchingOptions()) == false) {
            List<ElementMatchingCriteria> updatedList = new ArrayList<ElementMatchingCriteria>();
            if (elemSet != null) {
                for (ElementMatchingCriteria emc : elemSet) {
                    updatedList.add(metaSvc.createElementMatchingCriteria(emc.getNamespaceUri(), emc.getLocalName()));
                }
            }
            updatedList.add(metaSvc.createElementMatchingCriteria(namespace, elementLocalName));
            metaSvc.setLayeredMetaDataDefinitionElementCriteria(user, lmdName, updatedList);
        }
    }
}
