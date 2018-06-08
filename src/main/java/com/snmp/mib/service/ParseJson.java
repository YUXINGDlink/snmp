package com.snmp.mib.service;

import java.util.ArrayList;
import java.util.List;

import com.snmp.mib.model.MibObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ParseJson {

	// 将json字符串转换为java对象
	public List<MibObject> JSON2Object(String jsonParam) {
		List<MibObject> moList = new ArrayList();
		// 接收{}对象，此处接收数组对象会有异常

		// 将json字符串转换为json对象
		JSONObject obj = new JSONObject().fromObject(jsonParam);
		JSONArray jsonArray = JSONArray.fromObject(obj.get("data").toString());// 把String转换为json

		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject job = jsonArray.getJSONObject(i);
			MibObject mo = (MibObject) JSONObject.toBean(job, MibObject.class);// 将建json对象转换为Person对象
			moList.add(mo);
		}
		return moList;// 返回一个Person对象
	}

	/**
	 * list转换成 json
	 * 
	 * @param list
	 * @return
	 */
	public static JSONArray List2Json(List<MibObject> list) {
		JSONArray json = new JSONArray();
		JSONArray jsonArr = JSONArray.fromObject(list);
		return jsonArr;
	}
}
