package com.snmp.mib.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.adventnet.snmp.mibs.MibException;
import com.adventnet.snmp.mibs.MibNode;
import com.adventnet.snmp.mibs.MibOperations;
import com.snmp.mib.model.MibObject;
import com.snmp.mib.model.Nodes;

import net.percederberg.mibble.Mib;
import net.percederberg.mibble.MibLoader;
import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibValue;
import net.percederberg.mibble.MibValueSymbol;
import net.percederberg.mibble.snmp.SnmpObjectType;
import net.sf.json.JSONArray;
/**
 * 
 * <Description> 解析mib文件<br> 
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.service <br>
 */
public class MibParser {

	/**
	 * 
	 * Description: mib库解析后，返回MibObject的list<br> 
	 *  
	 * @author yx<br>
	 * @param filePath 文件路径
	 * @return List
	 * @throws IOException 
	 * @throws MibLoaderException <br>
	 */
    @SuppressWarnings({ "unchecked" })
    public List doMibParser(String filePath) throws IOException, MibLoaderException {
        MibOperations mibOps = new MibOperations();
        try {
            mibOps.loadMibModules(filePath);
        } catch (MibException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    	// String filePath = "f:/aabak/Bridge.mib";
    	File file = new File(filePath);
    	MibLoader ml = new MibLoader();
    	Mib mib = ml.load(file);
    	String mibName = mib.getName();
    
    	System.err.println("mibName===" + mibName);
    	System.err.println("-------------------------------------");
    	String syntax = "";
    	String access = "";
    	String status = "";
    	String desc = "";
    	List<MibObject> list = new ArrayList<MibObject>();
    	Collection c = mib.getAllSymbols();
    	Iterator it = c.iterator();
    	int i = 0;
    	while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof MibValueSymbol) {
            	MibObject mo = new MibObject();
            	MibValueSymbol mvs = (MibValueSymbol) obj;
            	SnmpObjectType sot = null;
            	if (mvs.getType() instanceof SnmpObjectType) {
                    sot = (SnmpObjectType) mvs.getType();
            	}
            	if (sot != null) {
//                    
                    MibNode node = mibOps.getMibNode(mvs.getName());//sysDescr.0
                    if(node.getSyntax()==null) {
                        syntax = sot.getSyntax().getName();
                    }else {
                        syntax= node.getSyntax().toString();
                    }
                    access = sot.getAccess().toString();
                    status = sot.getStatus().toString();
                    desc = sot.getDescription().toString();
            	}
            	// 是否为表的列
                boolean isTableColumn = mvs.isTableColumn();
                String name = mvs.getName();
                
                MibValue value = mvs.getValue();
                MibValueSymbol parent = mvs.getParent();
                String parentValue = "";
                System.err.println("name==" + name);
                System.err.println("value==" + value);
                System.err.println("isTableColumn==" + isTableColumn);
                if (parent != null) {
                    parentValue = parent.getValue().toString();
                    if (parent.getParent() == null) {
                    	System.err.println("supperParentName======" + mibName);
                    	System.err.println("supperParentValue=====" + parentValue);
                    
                    }
                    System.err.println("parentName=" + parent.getName());
                    System.err.println("parentValue=" + parent.getValue());
                
                } 
                System.err.println("syntax=" + syntax);
                System.err.println("access=" + access);
                System.err.println("status=" + status);
                System.err.println("-------------------------------------");
                
                mo.setName(name);
                //mo.setValue(value.toString());
                mo.setOid(value.toString());
                mo.setParent(parentValue.toString().replaceAll("[.]", ""));
                mo.setSyntax(syntax);
                mo.setAccess(access);
                mo.setStatus(status);
                mo.setDesc(desc);
                mo.setId(value.toString().replaceAll("[.]", ""));
            	mo.setIsTableColumn(isTableColumn);
            	list.add(mo);
            	i++;
            }
    	}
    
    	return list;
    }

/**
 * 
 * Description:返回生成mib tree所需的xml <br> 
 *  
 * @author XXX<br>
 * @param list 
 * @return  <br>
 */
    public static String toStringFromDoc(List<MibObject> list) {
    	Nodes treeNodes = new Nodes();
    	StringBuffer xmlnodes = new StringBuffer();
    	if (list != null && list.size() > 0) {
            xmlnodes.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            xmlnodes.append("<nodes>");
            for (int i = 0; i < list.size(); i++) {
            	MibObject mo = (MibObject) list.get(i);
            	treeNodes.setId(i);
            	treeNodes.setParentId(mo.getParent().replaceAll("[.]", ""));
                treeNodes.setNodeName(mo.getName());
                treeNodes.setNodeId(mo.getValue().replaceAll("[.]", ""));
                treeNodes.setValue(mo.getValue());
                treeNodes.setSyntax(mo.getSyntax());
            // treeNodes.setDesc(mo.getDesc());
                xmlnodes.append("<node nodeId='" + treeNodes.getNodeId() + "' parentId='" + treeNodes.getParentId()
                    + "' desc='" + treeNodes.getDesc() + "' syntax='" + treeNodes.getSyntax() + "' value='"
                    + treeNodes.getValue() + "'>" + treeNodes.getNodeName() + "</node>");
            }
            xmlnodes.append("</nodes>");
    	}
    
    	return xmlnodes.toString();
    }
/**
 * 
 * Description:list转json字符串 <br> 
 *  
 * @author XXX<br>
 * @param list 
 * @return <br>
 */
    public static String mibList2Json(List<MibObject> list) {
    	JSONArray json = JSONArray.fromObject(list);
    	return json.toString();
    }
}
