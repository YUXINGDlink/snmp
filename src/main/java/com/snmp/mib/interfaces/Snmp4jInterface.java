package com.snmp.mib.interfaces;

import java.io.IOException;
import java.util.List;

import com.snmp.mib.model.SearchCriteria;
/**
 * 
 * <Description> snmp4j接口定义<br> 
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.interfaces <br>
 */
public interface Snmp4jInterface {
	/**
	 * 
	 * Description: 根据oid取设备信息<br> 
	 *  
	 * @author yx<br>
	 * @param search 
	 * @return string<br>
	 */
    String get(SearchCriteria search);
    /**
     * 
     * Description: 根据oid取下一节点信息<br> 
     *  
     * @author yx<br>
     * @param search 
     * @return string<br>
     */
    String getNext(SearchCriteria search);
    
 
    /**
     * 
     * Description:set功能 <br> 
     *  
     * @author yx<br>
     * @param search 
     * @return string
     * @throws IOException  <br>
     */
    String set(SearchCriteria search) throws IOException;
    
    
    /**
     * 
     * Description: table功能<br> 
     *  
     * @author yx<br>
     * @param search 
     * @return string<br>
     */
    String tableView(SearchCriteria search);
    /**
     * 
     * Description:  walk功能<br> 
     *  
     * @author yx<br>
     * @param search 
     * @return string<br>
     */
    String walk(SearchCriteria search);
    /**
     * 
     * Description: getBulk功能<br> 
     *  
     * @author yx<br>
     * @param search 
     * @return string<br>
     */
    String getBulk(SearchCriteria search);
   
    /**
     * 
     * Description: 根据多个oid取设备信息<br> 
     *  
     * @author yx<br>
     * @param search 
     * @param oidList oid列表
     * @return <br>
     */
    String getList(SearchCriteria search, List<String> oidList);
    
     
}
