package com.snmp.mib.model;

/**
 * 
 * <Description> 返回结果<br> 
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.model <br>
 */
public class AjaxResponseBody {
    /**
     * 返回执行状态
     */
    private String msg; 
    /**
     * 返回结果
     */
    private String result;  
      
    public String getMsg() {  
        return msg;  
    }  
      
    public void setMsg(String msg) {  
        this.msg = msg;  
    }

    public String getResult() {
    	return result;
    }
    
    public void setResult(String result) {
    	this.result = result;
    }  
      
   
  
}  