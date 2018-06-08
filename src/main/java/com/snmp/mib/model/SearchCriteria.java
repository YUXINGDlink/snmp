package com.snmp.mib.model;

/**
 * 
 * <Description> 前台传入的查询参数<br> 
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.model <br>
 */
public class SearchCriteria {
    /** v3设置的用户名 */
    private String username;
    /** 解析的mib路径 */
    private String filePath;
    /**读  */
    private String readCommunity;
    /**写  */
    private String setCommunity;
    /** 操作类型 */
    private String opration;
    /**版本号  */
    private int version;
    /** oid */
    private String oid;
    /** 查看的主机ip */
    private String host;
    /** set时输入的值 */
    private String setValue;
    /**  端口*/
    private int port;
    /**类型  */
    private String systax;
    /** 超时 */
    private int timeout;
    /** 重连次数 */
    private int retransmits;
    /**  */
    private String profileName;
    /** v3安全认证名 */
    private String securityName;
    /**v3  */
    private String contextName;
    /** v3 */
    private String contextEngineId;
    /**v3  */
    private int authProtocol;
    /** getbulk设置的参数 */
    private int nonRepeaters;
    /** getbulk设置的参数 */
    private int maxRepetitions;
    /**v3  */
    private String authPass;
    /** v3 */
    private String priPass;
    
    public String getUsername() {
    	return username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public String getFilePath() {
    	return filePath;
    }
    
    public void setFilePath(String filePath) {
    	this.filePath = filePath;
    }
    
    public String getReadCommunity() {
    	return readCommunity;
    }
    
    public void setReadCommunity(String readCommunity) {
    	this.readCommunity = readCommunity;
    }
    
    public String getOpration() {
    	return opration;
    }
    
    public void setOpration(String opration) {
    	this.opration = opration;
    }
    
    public int getVersion() {
    	return version;
    }
    
    public void setVersion(int version) {
    	this.version = version;
    }
    
    public String getOid() {
    	return oid;
    }
    
    public void setOid(String oid) {
    	this.oid = oid;
    }
    
    public String getHost() {
    	return host;
    }
    
    public void setHost(String host) {
    	this.host = host;
    }
    
    public String getSetValue() {
    	return setValue;
    }
    
    public void setSetValue(String setValue) {
    	this.setValue = setValue;
    }
    
    public int getPort() {
    	return port;
    }
    
    public void setPort(int port) {
    	this.port = port;
    }
    
    public String getSetCommunity() {
    	return setCommunity;
    }
    
    public void setSetCommunity(String setCommunity) {
    	this.setCommunity = setCommunity;
    }
    
    public String getSystax() {
    	return systax;
    }
    
    public void setSystax(String systax) {
    	this.systax = systax;
    }
    
    public int getTimeout() {
    	return timeout;
    }
    
    public void setTimeout(int timeout) {
    	this.timeout = timeout;
    }
    
    public int getRetransmits() {
    	return retransmits;
    }
    
    public void setRetransmits(int retransmits) {
    	this.retransmits = retransmits;
    }
    
    public String getProfileName() {
    	return profileName;
    }
    
    public void setProfileName(String profileName) {
    	this.profileName = profileName;
    }
    
    public String getSecurityName() {
    	return securityName;
    }
    
    public void setSecurityName(String securityName) {
    	this.securityName = securityName;
    }
    
    public String getContextName() {
    	return contextName;
    }
    
    public void setContextName(String contextName) {
    	this.contextName = contextName;
    }
    
    public String getContextEngineId() {
    	return contextEngineId;
    }
    
    public void setContextEngineId(String contextEngineId) {
    	this.contextEngineId = contextEngineId;
    }
    
    public int getAuthProtocol() {
    	return authProtocol;
    }
    
    public void setAuthProtocol(int authProtocol) {
    	this.authProtocol = authProtocol;
    }
    
    public int getNonRepeaters() {
    	return nonRepeaters;
    }
    
    public void setNonRepeaters(int nonRepeaters) {
    	this.nonRepeaters = nonRepeaters;
    }
    
    public int getMaxRepetitions() {
    	return maxRepetitions;
    }
    
    public void setMaxRepetitions(int maxRepetitions) {
    	this.maxRepetitions = maxRepetitions;
    }
    
    public String getAuthPass() {
    	return authPass;
    }
    
    public void setAuthPass(String authPass) {
    	this.authPass = authPass;
    }
    
    public String getPriPass() {
    	return priPass;
    }
    
    public void setPriPass(String priPass) {
    	this.priPass = priPass;
    }

}