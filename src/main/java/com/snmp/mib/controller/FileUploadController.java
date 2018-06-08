package com.snmp.mib.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.snmp.mib.model.MibObject;
import com.snmp.mib.service.MibParser;
import net.percederberg.mibble.MibLoaderException;

/**
 * 
 * <Description> 文件上传的Controller mib文件解析的时候需先上传至服务器，解析完成后删除文件
 *  
 * @author yx <br>
 * @version 1.0 <br>
 * @CreateDate 2018年6月8日 <br>
 * @since V1.0 <br>
 * @see com.snmp.mib.controller <br>
 */
@Controller
public class FileUploadController {

	/**
	 * 
	 * Description: <br> 文件上传
	 *  
	 * @author yx<br>
	 * @return <br>
	 */
    @RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload() {
	return "/fileupload";
    }

	/**
	 * 
	 * Description: <br> 
	 *  
	 * @author yx<br>
	 * @param file 文件类型
	 * @return <br>
	 */
    @RequestMapping(value = "/api/upload", method = RequestMethod.POST)
	@ResponseBody
    public String upload(@RequestParam("file") MultipartFile file){
    	String filePath = "";
    	MibParser mibParser = new MibParser();
    	if (!file.isEmpty()) {
	    try {
                File fileSourcePath = new File(Class.class.getClass().getResource("/").getPath());
                String fileName = file.getOriginalFilename();
                if (fileName.indexOf(":") > -1) {
                    filePath = fileName;
                } 
                else {
                    filePath = fileSourcePath + "\\" + fileName;
                }
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                out.write(file.getBytes());
                out.flush();
                out.close();
	    } 
	    catch (FileNotFoundException e){
	    	e.printStackTrace();
	    	return "上传失败," + e.getMessage();
	    } 
	    catch (IOException e) {
	    	e.printStackTrace();
	    	return "上传失败," + e.getMessage();
	    }
	
	    List<MibObject> moList = new ArrayList();
	    try {
			// 将mib文件解析成list
	    	moList = mibParser.doMibParser(filePath);
	    } 
	    catch (IOException e) {
				// TODO Auto-generated catch block
	    	e.printStackTrace();
	    } 
	    catch (MibLoaderException e) {
				// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }
			// 生成mib库所需的xml
			// String result = mibParser.toStringFromDoc(moList);
	    String result = mibParser.mibList2Json(moList);
			// 解析完成后删除文件
	    if (file.getOriginalFilename().indexOf(":") == -1) {
	    	deleteFile(filePath);
	    }
	    return result;
    	}
    	else {
            return "上传失败，因为文件是空的.";
        }
    }

	/**
	 * 
	 * Description: <br> 文件上传解析完成后，删除文件
	 *  
	 * @author yx<br>
	 * @param fullFilePath <br>文件路径
	 */

    public static void deleteFile(String fullFilePath) {
        File deleteFile = new File(fullFilePath);
        deleteFile.delete();
    }

}