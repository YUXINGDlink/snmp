package com.snmp.mib.model;

/**
 * 
 * <Description>节点对象 <br> 
 *  
 * @author  YX <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.model <br>
 */
public class Nodes {
    /**
     *id 
     */
    private int id; 
    /**
     * 节点id
     */
    private String nodeId;  
    /**
     * 父节点id
     */
    private String parentId;  
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 值
     */
    private String value;
    /**
     * 类型
     */
    private String syntax;
    /**
     * 描述
     */
    private String desc;
  
    public int getId() {  
        return id;  
    }  
  
    public void setId(int id) {  
        this.id = id;  
    }  
  
    public String getNodeId() {  
        return nodeId;  
    }  
  
    public void setNodeId(String nodeId) {  
        this.nodeId = nodeId;  
    }  
  
    public String getParentId() {  
        return parentId;  
    }  
  
    public void setParentId(String parentId) {  
        this.parentId = parentId;  
    }  
  
    public String getNodeName() {  
        return nodeName;  
    }  
  
    public void setNodeName(String nodeName) {  
        this.nodeName = nodeName;  
    }

    public String getValue() {
    	return value;
    }
    
    public void setValue(String value) {
    	this.value = value;
    }
    
    public String getSyntax() {
    	return syntax;
    }
    
    public void setSyntax(String syntax) {
    	this.syntax = syntax;
    }
    
    public String getDesc() {
    	return desc;
    }
    
    public void setDesc(String desc) {
    	this.desc = desc;
    }  
  
}  
