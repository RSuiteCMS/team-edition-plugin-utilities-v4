package com.rsicms.pluginUtilities.apiResult;

import java.util.HashMap;

public class ResultMap extends HashMap<String, Object> {
	private static final long serialVersionUID = 8698920237410773049L;
	public ResultMap putMap(String name) {
		ResultMap map = new ResultMap();
		put(name, map);
		return map;
	}
	public ResultList putList(String name) {
		ResultList list = new ResultList();
		put(name, list);
		return list;
	}
}
