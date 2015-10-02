package com.rsicms.pluginUtilities;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.Plugin;
import com.reallysi.rsuite.api.extensions.PluginAware;
import com.reallysi.rsuite.api.forms.FormDefinition;
import com.reallysi.rsuite.api.forms.FormHandler;
import com.reallysi.rsuite.api.forms.FormInstance;
import com.reallysi.rsuite.api.forms.FormInstanceCreationContext;

abstract public class SimpleFormHandler implements FormHandler, PluginAware {

	protected FormInstance form;
	protected FormInstanceCreationContext context;
	protected FormDefinition formDef;
	protected Plugin plugin;
	
	@Override
	public void adjustFormInstance(FormInstanceCreationContext context, FormInstance form) throws RSuiteException {
		this.context = context;
		this.form = form;
		try {
			adjustFormInstance();
		} catch (Throwable t) {
			throw new RSuiteException(RSuiteException.ERROR_INTERNAL_ERROR, t.getMessage(), t);
		}
	}

	@Override
	public void initialize(FormDefinition formDef) {
		this.formDef = formDef;
	}

	@Override
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	
	abstract public void adjustFormInstance() throws Throwable;
}
