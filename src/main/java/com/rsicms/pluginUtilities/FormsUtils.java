package com.rsicms.pluginUtilities;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.DataTypeOptionValue;
import com.reallysi.rsuite.api.FormControlType;
import com.reallysi.rsuite.api.ManagedObject;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.forms.FormInstanceCreationContext;
import com.reallysi.rsuite.api.forms.FormParameterInstance;
import com.reallysi.rsuite.api.xml.XPathEvaluator;
import com.reallysi.rsuite.service.XmlApiManager;


/**
 * Collection of shortcut methods to create form parameters.
 */
public class FormsUtils
{
	private static Log log = LogFactory.getLog(FormsUtils.class);
	
	private static String CLASSNAME = "FormsUtils";

	public static String sortAscending = "asc";
	public static String sortDescending = "desc";
	public static String sortNoSort = "nosort";
	public static String sortNatural = "natural";

	public static Boolean required = true;
	public static Boolean notRequired = false;
	public static Boolean readOnly = true;
	public static Boolean notReadOnly = false;
	public static Boolean allowMultiple = true;
	public static Boolean dontAllowMultiple = false;
	public static String[] nullValues = null;
	public static String nullValue = null;
	public static List<DataTypeOptionValue> nullDataTypeOptions = null;
	public static String nullDataType = null;
	public static String nullValidationRegex = null;
	public static String nullValidationMessage = null;
	
	
	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param values
	 */
	public static void addFormLabelParameter(List<FormParameterInstance> params, String name, String label, String[] values) {
		createFormParameter(params, name, label, null, null, FormControlType.LABEL, null, null, sortNoSort, null,  false);
	}
	/**
	 * @param params
	 * @param name
	 * @param label
	 */
	public static void addFormLabelParameter(List<FormParameterInstance> params, String name, String label) {
		createFormParameter(params, name, label, null, null, FormControlType.LABEL, null, null, sortNoSort, null,  false);
	}

	/**
	 * @param controlType
	 * @param params
	 * @param name
	 * @param label
	 * @param dataTypeName
	 * @param beforeOptions
	 * @param values
	 * @param sortOrder
	 * @param required
	 * @param readOnly
	 * 
	 * For use with select, multiselect, checkbox, radio controls 
	 */
	public static void addFormSelectTypeParameter(FormControlType controlType, List<FormParameterInstance> params, String name, String label, String dataTypeName, List<DataTypeOptionValue> beforeOptions, String[] values, String sortOrder, Boolean allowMultiple, Boolean required, Boolean readOnly) {
		createFormParameter(params, name, label, null, values, controlType, dataTypeName, beforeOptions, sortOrder, allowMultiple, required, null, "This field is required", readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param dataTypeName
	 * @param beforeOptions
	 * @param value
	 * @param required
	 * @param readOnly
	 */
	public static void addFormTextParameter(List<FormParameterInstance> params, String name, String label, String dataTypeName, List<DataTypeOptionValue> beforeOptions, String value, Boolean required, String validationRegex, String validationMessage, Boolean readOnly) {
		createFormParameter(params, name, label, value, null, FormControlType.INPUT, dataTypeName, beforeOptions, sortNoSort, false, required, validationRegex, validationMessage, readOnly);
	}

    /**
     * @param params
     * @param name
     * @param label
     * @param dataTypeName
     * @param beforeOptions
     * @param values
     * @param required
     * @param readOnly
     */
    public static void addFormTextParameterRepeatable(List<FormParameterInstance> params, String name, String label, String dataTypeName,
            List<DataTypeOptionValue> beforeOptions, String[] values, Boolean required, String validationRegex, String validationMessage, Boolean readOnly) {
        createFormParameter(params, name, label, null, values, FormControlType.INPUT, dataTypeName, beforeOptions, sortNoSort, true, required,
                validationRegex, validationMessage, readOnly);
    }

    /**
	 * @param params
	 * @param name
	 * @param label
	 * @param dataTypeName
	 * @param beforeOptions
	 * @param values
	 * @param allowMultiple
	 * @param required
	 * @param readOnly
	 */
	public static void addFormTaxonomyParameter(List<FormParameterInstance> params, String name, String label, String dataTypeName, List<DataTypeOptionValue> beforeOptions, String[] values, Boolean allowMultiple, Boolean required, Boolean readOnly) {
		createFormParameter(params, name, label, null, values, FormControlType.fromName("taxonomy"), dataTypeName, beforeOptions, sortNoSort, allowMultiple, required, null, "This field is required.", readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param value
	 * @param required
	 * @param readOnly
	 */
	public static void addFormDateParameter(List<FormParameterInstance> params, String name, String label, String value, Boolean required, Boolean readOnly) {
		createFormParameter(params, name, label, value, null, FormControlType.DATEPICKER, null, null, sortNoSort, false, required, null, "This field is required.", readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param value
	 * @param required
	 * @param readOnly
	 */
	public static void addFormDateRangeParameter(List<FormParameterInstance> params, String name, String label, String[] values, Boolean required, Boolean readOnly) {
		createFormParameter(params, name, label, null, values, FormControlType.DATERANGE, null, null, sortNoSort, false, required, null, "This field is required.", readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param value
	 * @param required
	 * @param readOnly
	 */
	public static void addFormTextAreaParameter(List<FormParameterInstance> params, String name, String label, String value, Boolean required, String validationRegex, String validationMessage, Boolean readOnly) {
		createFormParameter(params, name, label, value, null, FormControlType.TEXTAREA, null, null, sortNoSort, false, required, validationRegex, validationMessage, readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param label
	 * @param values
	 * @param required
	 * @param readOnly
	 */
	public static void addFormTextAreaParameter(List<FormParameterInstance> params, String name, String label, String[] values, Boolean required, String validationRegex, String validationMessage, Boolean readOnly) {
		createFormParameter(params, name, label, null, values, FormControlType.TEXTAREA, null, null, sortNoSort, false, required, validationRegex, validationMessage, readOnly);
	}

	/**
	 * @param params
	 * @param name
	 * @param beforeOptions
	 */
	public static void addFormSubmitButtonsParameter(List<FormParameterInstance> params, String name, List<DataTypeOptionValue> beforeOptions) {
		createFormParameter(params, name, null, null, null, FormControlType.SUBMITBUTTON, null, beforeOptions, sortNoSort, false, false);
	}

    /**
     * @param params
     * @param name
     * @param label
     * @param values
     * @param required
     * @param readOnly
     */
    public static void addFormCkEditorParameter(List<FormParameterInstance> params, String name, String label, String value) {
        createFormParameter(params, name, label, value, nullValues, FormControlType.fromName("ckeditor"), nullDataType, nullDataTypeOptions, sortNoSort,
                dontAllowMultiple, required, nullValidationRegex, nullValidationMessage, notReadOnly);
    }

    /**
	 * @param params
	 * @param name
	 * @param value
	 */
	public static void addFormHiddenParameter(List<FormParameterInstance> params, String name, String value) {
		createFormParameter(params, name, null, value, null, FormControlType.HIDDEN, null, null, sortNoSort, false, false);
	}

	private static void createFormParameter(List<FormParameterInstance> params,
			String name, String label, 
			String value, String[] values, 
			FormControlType controlType, 
			String dataTypeName, List<DataTypeOptionValue> beforeOptions, String sortOrder,
			Boolean allowMultiple, Boolean readOnly) {
		createFormParameter(params, name, label, value, values, controlType, dataTypeName, beforeOptions, sortOrder, 
				allowMultiple, null, null, null, readOnly);
	}

	private static void createFormParameter(List<FormParameterInstance> params,
			String name, String label, 
			String value, String[] values, 
			FormControlType controlType, 
			String dataTypeName, List<DataTypeOptionValue> beforeOptions, String sortOrder,
			Boolean allowMultiple, Boolean required, String validationRegex, String validationMessage, Boolean readOnly) {
		FormParameterInstance param = new FormParameterInstance();
		log.debug(CLASSNAME + " Creating form parameter " + name + "/" + label);
		param.setName(name);
		if (null != label && !label.isEmpty())
			param.setLabel(label);
		if (null != values) {
			for (String val : values) {
				param.addValue(val);
			}
		}
		if (null != value && !value.isEmpty())
			param.addValue(value);
		if (null != allowMultiple && allowMultiple == true)
			param.setAllowMultiple(true);
		if (null != required && required != false) {
			param.setRequired(required);
            if (null != validationMessage) {
                param.setValidationErrorMessage(validationMessage);
            }
			if (null != validationRegex && !validationRegex.isEmpty()) {
				param.setValidationRegex(validationRegex);
			}
		}
		if (null != dataTypeName && !dataTypeName.isEmpty())
			param.setDataTypeName(dataTypeName);
		if (null != beforeOptions && beforeOptions.size() > 0)
			param.setBeforeOptions(beforeOptions);
		if (readOnly == true)
			param.setReadOnly(true);
		param.setFormControlType(controlType);
		if (sortOrder != null) {
			param.setSortOptions(sortOrder);
		}
		params.add(param);
	}

	public static String getXmlValue(FormInstanceCreationContext context, ManagedObject mo, String xpath) {
		XmlApiManager xmlApiManager = context.getXmlApiManager();
		XPathEvaluator evaluator = xmlApiManager.getXPathEvaluator();

		String value = "";
		try {
			value = evaluator.executeXPathToString(
							xpath,
							mo.getElement().getOwnerDocument());
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
		}
		return value;
	}
	
	public static String[] getXmlValues(FormInstanceCreationContext context, ManagedObject mo, String xpath) {
		XmlApiManager xmlApiManager = context.getXmlApiManager();
		XPathEvaluator evaluator = xmlApiManager.getXPathEvaluator();

		try {
			String[] values = evaluator.executeXPathToStringArray(
							xpath,
							mo.getElement().getOwnerDocument());
			return values;
		} catch (RSuiteException e) {
			log.error(CLASSNAME + " Error getting XPath value " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * @param cssName a unique value that will be included in the css classes
	 * @param field the type of the field, like "instructions"
	 * @return
	 */
	public static String makeCssNames(String cssName, String field) {
		return field + cssName + " " + field + "Field";
	}

}

