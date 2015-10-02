package com.rsicms.pluginUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.SortOrder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.reallysi.rsuite.api.DataTypeOptionValue;
import com.reallysi.rsuite.api.FormControlType;
import com.reallysi.rsuite.api.ManagedObject;
import com.reallysi.rsuite.api.MetaDataType;
import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.forms.FormColumnInstance;
import com.reallysi.rsuite.api.forms.FormInstanceCreationContext;
import com.reallysi.rsuite.api.forms.FormParameterInstance;
import com.reallysi.rsuite.api.xml.XPathEvaluator;
import com.reallysi.rsuite.service.XmlApiManager;

public class FormBuilder {
	
	private static Log log = LogFactory.getLog(FormsUtils.class);
	private static final String CLASSNAME = FormBuilder.class.getSimpleName();
	private static boolean empty(String str) { return StringUtils.isEmpty(str); }
	private static boolean empty(List<?> list) { return list == null || list.size() == 0; }

	public static FormParameterInstance label(
			final String label, 
			final String styleClass, 
			final String[] values) 
	{
		return param(FormControlType.LABEL, null, label, null, values, null, null, null, null, null, null, null, null, styleClass);
	}
	/**
	 * @param params
	 * @param name
	 * @param label
	 */
	public static FormParameterInstance label(
			String styleClass, 
			String label) 
	{
		return label(label, styleClass, null); 
	}

	public static FormParameterInstance select(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String[] values, 
			SortOrder sortOrder, 
			Boolean allowMultiple, 
			Boolean required, 
			Boolean readOnly) 
	{
		return param(FormControlType.SELECT, name, label, null, values, beforeOptions, dataTypeName, sortOrder, allowMultiple, required, null, "This field is required", readOnly, null);
	}
	public static FormParameterInstance multiselect(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String[] values, 
			SortOrder sortOrder, 
			Boolean allowMultiple, 
			Boolean required, 
			Boolean readOnly) 
	{
		return param(FormControlType.MULTISELECT, name, label, null, values, beforeOptions, dataTypeName, sortOrder, allowMultiple, required, null, "This field is required", readOnly, null);
	}
	public static FormParameterInstance checkbox(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String[] values, 
			SortOrder sortOrder, 
			Boolean allowMultiple, 
			Boolean required, 
			Boolean readOnly) 
	{
		return param(FormControlType.CHECKBOX, name, label, null, values, beforeOptions, dataTypeName, sortOrder, allowMultiple, required, null, "This field is required", readOnly, null);
	}
	public static FormParameterInstance radio(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String[] values,
			SortOrder sortOrder,
			Boolean allowMultiple, 
			Boolean required, 
			Boolean readOnly) 
	{
		// FormControlType.RADIOBUTTON radio causes a bug with qa166 
		return param(FormControlType.fromName("radio"), name, label, null, values, beforeOptions, dataTypeName, sortOrder, allowMultiple, required, null, "This field is required", readOnly, null);
	}
	public static FormParameterInstance radio(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String value,
			SortOrder sortOrder, 
			Boolean required) 
	{
		// FormControlType.RADIOBUTTON radio causes a bug with qa166 
		return param(FormControlType.fromName("radio"), name, label, value, null, beforeOptions, dataTypeName, sortOrder, null, required, null, "This field is required", null, null);
	}

	public static FormParameterInstance input(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String value, 
			Boolean required, 
			String validationRegex, 
			String validationMessage, 
			Boolean readOnly) 
	{
		return param(FormControlType.INPUT, name, label, value, null, beforeOptions, dataTypeName, null, null, required, validationRegex, validationMessage, readOnly, null);
	}

	public static FormParameterInstance taxonomy(
			String name, 
			String label, 
			String dataTypeName, 
			List<DataTypeOptionValue> beforeOptions, 
			String[] values, 
			Boolean allowMultiple, 
			Boolean required, 
			Boolean readOnly) 
	{
		return param(FormControlType.fromName("taxonomy"), name, label, null, values, beforeOptions, dataTypeName, null, allowMultiple, required, null, null, readOnly, null);
	}

	public static FormParameterInstance date(
			String name, 
			String label, 
			String value, 
			Boolean required, 
			Boolean readOnly) 
	{
		return param(FormControlType.DATEPICKER, name, label, value, null, null, null, null, null, required, null, null, readOnly, null);
	}

	public static FormParameterInstance textarea(
			String name, 
			String label, 
			String value, 
			Boolean required, 
			String validationRegex, 
			String validationMessage, 
			Boolean readOnly
		) 
	{
		return param(FormControlType.TEXTAREA, name, label, value, null, null, null, null, false, required, validationRegex, validationMessage, readOnly, null);
	}

	public static FormParameterInstance textarea(
			String name, 
			String label, 
			String[] values, 
			Boolean required, 
			String validationRegex, 
			String validationMessage, 
			Boolean readOnly) 
	{
		return param(FormControlType.TEXTAREA, name, label, null, values, null, null, null, true, required, validationRegex, validationMessage, readOnly, null);
	}

	public static FormParameterInstance submitButtons(
			String name, 
			List<DataTypeOptionValue> beforeOptions) 
	{
		return param(FormControlType.SUBMITBUTTON, name, null, null, null, beforeOptions, null, null, false, false);
	}

	public static FormParameterInstance hidden(
			String name, 
			String value) 
	{
		return param(FormControlType.HIDDEN, name, null, value, null, null, null, null, null, null, null, null, null, null);
	}

	private static FormParameterInstance param(
			FormControlType controlType, 
			String name, 
			String label, 
			String value, 
			String[] values, 
			List<DataTypeOptionValue> beforeOptions, 
			String dataTypeName, 
			SortOrder sortOrder,
			Boolean allowMultiple, 
			Boolean readOnly) 
	{
		return param(controlType, name, label, value, values, beforeOptions, dataTypeName, sortOrder, allowMultiple, false, null, null, readOnly, null);
	}
	
	private static FormParameterInstance param(
			final FormControlType controlType, 
			final String name, 
			final String label, 
			final String value, 
			final String[] values, 
			final List<DataTypeOptionValue> beforeOptions, 
			final String dataTypeName, 
			final SortOrder sortOrder,
			final Boolean allowMultiple, 
			final Boolean required, 
			final String validationRegex, 
			final String validationMessage, 
			final Boolean readOnly, 
			final String styleClass)
	{
		log.debug(CLASSNAME + " Creating form parameter " + name + "/" + label);
		return new FormParameterInstance(){
			{
				setFormControlType(controlType);
				setName(name);
				if (!empty(label)) setLabel(label);
				if (null != values) {
					for (String val : values) {
						addValue(val);
					}
				}
				if (!empty(value)) addValue(value);
				if (allowMultiple == true) setAllowMultiple(true);
				if (required == true) {
					setRequired(true);
					setValidationErrorMessage(empty(validationMessage) ? "This field is required" : validationMessage);
					if (null != validationRegex) {
						setValidationRegex(validationRegex);
					}
					if (!empty(beforeOptions)) setBeforeOptions(beforeOptions);
					if (readOnly == true) setReadOnly(true);
					
					
				}
				if (!empty(dataTypeName)) setDataTypeName(dataTypeName);
				if (sortOrder != null) setSortOptions(sortOrder.toString());
				if (!empty(styleClass)) setStyleClass(styleClass);
			}
		};
	}
	public static FormColumnInstance column(String name, FormParameterInstance... params) {
		FormColumnInstance col = new FormColumnInstance();
		col.setName(name);
		for (FormParameterInstance param : params) {
			col.addParam(param);
		}
		return col;
	}
	
	public static List<FormColumnInstance> columns(FormColumnInstance... columns) {
		List<FormColumnInstance> cols = new ArrayList<FormColumnInstance>();
		for (FormColumnInstance col : columns) {
			cols.add(col);
		}
		return cols;
	}
}
