package com.snmp.mib.model;

/**
 * 
 * <Description> MibObject<br>
 * 
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.model <br>
 */
public class MibObject {
    /**
     * id
     */
    private String id;
    /**
     * node名称
     */
    private String name;
    /**
     * 值
     */
    private String value;
    /**
     * 父节点
     */
    private String parent;
    /**
     * 类型
     */
    private String syntax;
    /**
     * 状态
     */
    private String status;
    /**
     * 权限
     */
    private String access;
    /**
     * 描述
     */
    private String desc;
    /**
     * oid
     */
    private String oid;
    /**
     * 是否为表中的列
     */
    private boolean isTableColumn;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getSyntax() {
        return syntax;
    }

    public void setSyntax(String syntax) {
        this.syntax = syntax;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public boolean getIsTableColumn() {
        return isTableColumn;
    }

    public void setIsTableColumn(boolean isTableColumn) {
        this.isTableColumn = isTableColumn;
    }

}
