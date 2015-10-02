package com.rsicms.pluginUtilities.apiResult;

import java.util.ArrayList;

public class ResultList extends ArrayList<Object> {
	private static final long serialVersionUID = 3721795984815184717L;
	public ResultMap addMap() {
		ResultMap map = new ResultMap();
		add(map);
		return map;
	}
	public ResultList addList() {
		ResultList list = new ResultList();
		add(list);
		return list;
	}
}
