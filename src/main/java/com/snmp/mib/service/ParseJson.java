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
            if ("1".equals(job.get("update"))) {
                MibObject mo = (MibObject) JSONObject.toBean(job, MibObject.class);// 将建json对象转换为Person对象
                moList.add(mo);
            }
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
        JSONArray accessJsonArray = JSONArray.fromObject("[" + obj.get("access").toString() + "]");
        JSONArray syntaxJsonArray = JSONArray.fromObject("[" + obj.get("syntax").toString() + "]");
        boolean isFinished;
        for (int i = 0; i < dataJsonArray.size(); i++) {
            isFinished = false;
            JSONObject job = dataJsonArray.getJSONObject(i);

            if ("1".equals(job.get("update"))) {
                Iterator iteratorData = job.keys();

                while (iteratorData.hasNext()) {
                    if (isFinished == true) {
                        break;
                    }
                    String key = String.valueOf(iteratorData.next());// 得到键
                    String value = job.getString(key);// 得到值
                    for (int j = 0; j < accessJsonArray.size(); j++) {
                        if (isFinished == true) {
                            break;
                        }
                        JSONObject accessJob = accessJsonArray.getJSONObject(j);
                        Iterator iteratorAcc = accessJob.keys();
                        while (iteratorAcc.hasNext()) {
                            if (isFinished == true) {
                                break;
                            }
                            String acckey = String.valueOf(iteratorAcc.next());// 得到键
                            String accvalue = accessJob.getString(acckey);// 得到值
                            if (key.equals(acckey) && "read-write".equals(accvalue)) {
                                for (int k = 0; k < syntaxJsonArray.size(); k++) {
                                    if (isFinished == true) {
                                        break;
                                    }
                                    JSONObject syntaxJob = syntaxJsonArray.getJSONObject(k);
                                    Iterator iteratorSyn = syntaxJob.keys();

                                    while (iteratorSyn.hasNext()) {
                                        String synkey = String.valueOf(iteratorSyn.next());// 得到键
                                        String synvalue = syntaxJob.getString(synkey);// 得到值
                                        if (key.equals(synkey)) {
                                            MibObject mo = new MibObject();
                                            mo.setName(key);
                                            mo.setOid(job.getString("oid" + key));
                                            mo.setSyntax(synvalue);
                                            mo.setValue(job.getString(key));
                                            // mo = (MibObject) JSONObject.toBean(job, MibObject.class);//
                                            // 将建json对象转换为Person对象
                                            moList.add(mo);
                                            isFinished = true;
                                            break;
                                        }
                                    }

                                }

                                break;
                            }
                        }

                    }
                }

            }

        }
        return moList;// 
    }
}
