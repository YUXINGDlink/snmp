package com.snmp.mib.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.snmp.mib.model.MibObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 
 * <Description>解析json <br>
 * 
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.service <br>
 */
public class ParseJson {

    /**
     * 
     * Description: 将json字符串转换为java对象<br>
     * 
     * @author yx<br>
     * @param jsonParam
     *            json
     * @return <br>
     */
    public List<MibObject> json2List(String jsonParam) {
        List<MibObject> moList = new ArrayList();
        // 接收{}对象，此处接收数组对象会有异常

        // 将json字符串转换为json对象
        JSONObject obj = new JSONObject().fromObject(jsonParam);
        JSONArray jsonArray = JSONArray.fromObject(obj.get("data").toString());// 把String转换为json

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject job = jsonArray.getJSONObject(i);
//            if ("1".equals(job.get("update"))) {
                MibObject mo = (MibObject) JSONObject.toBean(job, MibObject.class);// 将建json对象转换为Person对象
                moList.add(mo);
//            }
        }
        return moList;// 返回一个Person对象
    }

    /**
     * 
     * Description: list转换成 json <br>
     * 
     * @author yx<br>
     * @param list 
     * @return <br>
     */
    public static JSONArray list2Json(List<MibObject> list) {
        JSONArray jsonArr = JSONArray.fromObject(list);
        return jsonArr;
    }

    /**
     * 
     * Description: 用于tableview功能中，将json字符串转换为java对象<br>
     * 
     * @author yx<br>
     * @param jsonParam
     *            json
     * @return <br>
     */
    public List<MibObject> json2ListForTable(String jsonParam) {
        List<MibObject> moList = new ArrayList();
        // 接收{}对象，此处接收数组对象会有异常

        // 将json字符串转换为json对象
        JSONObject obj = new JSONObject().fromObject(jsonParam);
        JSONArray dataJsonArray = JSONArray.fromObject(obj.get("data").toString());// 把String转换为json
//        JSONArray accessJsonArray = JSONArray.fromObject("[" + obj.get("access").toString() + "]");
//        JSONArray syntaxJsonArray = JSONArray.fromObject("[" + obj.get("syntax").toString() + "]");
        boolean isFinished;
        for (int i = 0; i < dataJsonArray.size(); i++) {
            isFinished = false;
            JSONObject job = dataJsonArray.getJSONObject(i);
            MibObject mo = (MibObject) JSONObject.toBean(job, MibObject.class);
            String oid=mo.getOid();
            if(".0".equals(oid.substring(oid.length()-2))) {
                mo.setOid(oid.substring(0,oid.length()-2));
            }
            moList.add(mo);
            
        }
        return moList;// 
    }
    /**
     * 
     * Description: 多个oid同时get时，转换成object<br> 
     *  
     * @param jsonParam 前台传入的数据
     * @return <br>
     */
    public List<MibObject> json2ListForMib(String jsonParam) {
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
}
