package com.snmp.mib.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.snmp.mib.model.AjaxResponseBody;
import com.snmp.mib.model.MibObject;
import com.snmp.mib.model.SearchCriteria;
import com.snmp.mib.service.SnmpDataService;

import net.sf.json.JSONObject;

/**
 * 
 * <Description> <br> snmp操作
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.controller <br>
 */
@RestController
public class SnmpController {

	/**
	 * 
	 * Description: <br> snmpGet操作
	 *  
	 * @author yx<br>
	 * @param search SearchCriteria查询条件认证
	 * @param errors 错误
	 * @return <br>
	 */

    @PostMapping("/api/snmpGet")
	public ResponseEntity<?> snmpGet(@Valid @RequestBody SearchCriteria search, Errors errors) {

        AjaxResponseBody result = new AjaxResponseBody();
        // If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
        
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.get(search);
        
        result.setResult(snmpStr);
        result.setMsg("success");
        return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br> snmpGetNext操作
	 *  
	 * @author yx<br>
	 * @param search SearchCriteria查询条件认证
     * @param errors 错误
	 * @return <br>
	 */
    @PostMapping("/api/snmpGetnext")
	public ResponseEntity<?> snmpGetNext(@Valid @RequestBody SearchCriteria search, Errors errors) {
        AjaxResponseBody result = new AjaxResponseBody();
        // If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.getNext(search);
        result.setResult(snmpStr);
        result.setMsg("success");
        return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br>  snmpSet操作
	 *  
	 * @author yx<br>
	 * @param search SearchCriteria查询条件认证
     * @param errors 错误
	 * @return <br>
	 */
    @PostMapping("/api/snmpSet")
    public ResponseEntity<?> snmpSet(@Valid @RequestBody SearchCriteria search, Errors errors) {
        
        AjaxResponseBody result = new AjaxResponseBody();
        	// If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = "";
        try {
            snmpStr = snmpData.set(search);
        } 
        catch (IOException e) {
            	// TODO Auto-generated catch block
            e.printStackTrace();
        }
        result.setResult(snmpStr);
        result.setMsg("success");
        return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br>  snmpWalk操作
	 *  
	 * @author yx<br>
	 * @param search SearchCriteria查询条件认证
     * @param errors 错误
	 * @return <br>
	 */
    @PostMapping("/api/snmpWalk")
    public ResponseEntity<?> snmpWalk(@Valid @RequestBody SearchCriteria search, Errors errors) {
    
    	AjaxResponseBody result = new AjaxResponseBody();
    	// If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);

        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.walk(search);
        
        result.setResult(snmpStr);
        result.setMsg("success");
    	return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br> snmpTableview操作
	 *  
	 * @author yx<br>
     * @param search SearchCriteria查询条件认证
     * @param errors 错误
	 * @return <br>
	 */
    @PostMapping("/api/snmpTable")
    public ResponseEntity<?> snmpTable(@Valid @RequestBody SearchCriteria search, Errors errors) {
    
    	AjaxResponseBody result = new AjaxResponseBody();
    
    	// If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.tableView(search);
        
        result.setResult(snmpStr);
        result.setMsg("success");
    	return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br> snmp批量set
	 *  
	 * @author yx<br>
	 * @param jsonParam 前台传入要处理的数据
	 * @param errors 错误
	 * @return <br>
	 */
	// @PostMapping("/api/snmpBatchSet")
    @ResponseBody
    @RequestMapping(value = "/api/snmpBatchSet", method = RequestMethod.POST, 
        produces = "application/json;charset=UTF-8")  
    public ResponseEntity<?> snmpBatchSet(@RequestBody JSONObject jsonParam, Errors errors) {
    
    	AjaxResponseBody result = new AjaxResponseBody();
    	// return result.toJSONString();
    // If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.batchSet(jsonParam.toString());
        result.setResult(snmpStr);
        result.setMsg("success");
    	return ResponseEntity.ok(result);
    }

	/**
	 * 
	 * Description: <br> snmpGetBulk操作
	 *  
	 * @author yx<br>
	 * @param search SearchCriteria查询条件认证
     * @param errors 错误
	 * @return <br>
	 */
    @PostMapping("/api/snmpGetBulk")
    public ResponseEntity<?> snmpGetBulk(@Valid @RequestBody SearchCriteria search, Errors errors) {
    
    	AjaxResponseBody result = new AjaxResponseBody();
    	// If error, just return a 400 bad request, along with the error message
        if (errors.hasErrors()) {
            result.setMsg(
                    errors.getAllErrors().stream().map(x -> x.getDefaultMessage()).collect(Collectors.joining(",")));
            return ResponseEntity.badRequest().body(result);
        
        }
        SnmpDataService snmpData = new SnmpDataService();
        String snmpStr = snmpData.getBulk(search);
        result.setResult(snmpStr);
        result.setMsg("success");
        return ResponseEntity.ok(result);
    }
}
