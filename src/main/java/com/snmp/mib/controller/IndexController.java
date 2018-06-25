package com.snmp.mib.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 
 * <Description> <br>  
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.controller <br>
 */
@Controller
public class IndexController {
    IndexController(){
        
    }
    /**
     * 
     * Description: <br>  
     *   
     * @author yx<br>
     * @return <br>
     */
    @GetMapping("/")
    public String index() {
    	return "index";
    }
}
