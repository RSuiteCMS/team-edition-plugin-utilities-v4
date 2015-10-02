package com.rsicms.pluginUtilities;

import com.reallysi.rsuite.api.RSuiteException;
import com.reallysi.rsuite.api.extensions.Plugin;
import com.reallysi.rsuite.api.extensions.PluginAware;
import com.reallysi.rsuite.api.remoteapi.CallArgumentList;
import com.reallysi.rsuite.api.remoteapi.RemoteApiDefinition;
import com.reallysi.rsuite.api.remoteapi.RemoteApiExecutionContext;
import com.reallysi.rsuite.api.remoteapi.RemoteApiHandler;
import com.reallysi.rsuite.api.remoteapi.RemoteApiResult;

public abstract class SimpleRemoteApiHandler implements RemoteApiHandler, PluginAware {
	protected RemoteApiDefinition definition = null;
	protected RemoteApiExecutionContext context = null;
	protected CallArgumentList arguments = null;
	protected Plugin plugin = null;
	
	@Override
	public RemoteApiResult execute(RemoteApiExecutionContext context, CallArgumentList arguments) throws RSuiteException {
		this.context = context;
		this.arguments = arguments;
		try {
			return execute();
		} catch (Throwable t) {
			throw new RSuiteException(RSuiteException.ERROR_INTERNAL_ERROR, "Error executing Webservice", t);
		}
	}

	@Override
	public void initialize(RemoteApiDefinition definition) {
		this.definition = definition;
	}
	@Override
	public void setPlugin(Plugin plugin) {
		this.plugin = plugin;
	}

	abstract public RemoteApiResult execute() throws Throwable;
}
