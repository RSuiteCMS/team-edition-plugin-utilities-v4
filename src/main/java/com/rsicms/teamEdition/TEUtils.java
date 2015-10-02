package com.rsicms.teamEdition;

import com.reallysi.rsuite.api.extensions.ExecutionContext;

public class TEUtils {

    public static String getRsuiteServerUrl(ExecutionContext context) { 
        return "http://" + context.getRSuiteServerConfiguration().getHostName() + ":" + context.getRSuiteServerConfiguration().getPort();
    }
    
    public static String getRsuiteUrl(ExecutionContext context) { 
        return getRsuiteServerUrl(context) + "/rsuite-cms/";
    }
    
    public static String getRestUrlV1(ExecutionContext context) {
        return getRsuiteServerUrl(context) + "/rsuite/rest/v1/";
    }

    public static String getRestUrlV2(ExecutionContext context) {
        return getRsuiteServerUrl(context) + "/rsuite/rest/v2/";
    }
}
