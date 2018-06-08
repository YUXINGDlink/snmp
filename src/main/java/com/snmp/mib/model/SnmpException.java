package com.snmp.mib.model;

/**
 * 
 * <Description>异常 <br> 
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.model <br>
 */
public class SnmpException extends Exception {
	/**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    
    public SnmpException() {
    	super("SNMP Exception");
    }
    
    public SnmpException(String msg) {
    	super(msg);
    }
    
    public SnmpException(Exception exception) {
    	super(exception);
    }
}
