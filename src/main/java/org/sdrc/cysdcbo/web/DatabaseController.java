/**
 * 
 */
package org.sdrc.cysdcbo.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.service.MasterRawDataService;
import org.sdrc.cysdcbo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Harsh Pratyush(harsh@sdrc.in)
 *
 */
@Controller
@RequestMapping("/database")
public class DatabaseController {
	
	@Autowired
	private MasterRawDataService masterRawDataService;
	
	@Autowired
	private ResourceBundleMessageSource messages;
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("insertXpaths")
	@ResponseBody
	boolean insertXpaths() throws Exception
	{
		return masterRawDataService.generateXpath();
	}
	
	
	@RequestMapping("updateAreaTable")
	@ResponseBody
	boolean updateAreaTable()throws Exception
	{
		return masterRawDataService.updateAreaTable();
	}
	
	
	@RequestMapping("getRawDataFromOdk")
	@ResponseBody
	boolean getRawDataFromOdk()throws Exception
	{
		return masterRawDataService.getRawDataFromOdk();
	}
	
	
	@RequestMapping("uptoQuestionLevel")
	@ResponseBody
	boolean uptoQuestionLevel()throws Exception
	{
		return masterRawDataService.uptoQuestionLevel();
	}
	
	
	@RequestMapping("updateXformMapping")
	@ResponseBody
	boolean updateXformMapping()throws Exception
	{
		return masterRawDataService.updateXformMapping();
	}
	
	@RequestMapping("formatGeoCordinates")
	@ResponseBody
	boolean formatGeoCordinates()throws Exception
	{
		return masterRawDataService.formatGeoCordinates();
	}
	
	@RequestMapping("getRawExcel")
	@ResponseBody
	String getRawExcel()throws Exception
	{
		return masterRawDataService.generateExcel();
	}
	
	
	
	@RequestMapping("persistRawData")
	@ResponseBody
	boolean persistRawData()throws Exception
	{
		return masterRawDataService.persistRawData();
	}
	
	@RequestMapping(value="updatePassword",method=RequestMethod.POST)
	@ResponseBody
	boolean updatePassword(@RequestBody CollectUserModel collectUserModel,@RequestHeader("secret") String secret)throws Exception
	{
		if(secret!=null && secret.equalsIgnoreCase(messages.getMessage("secret.code", null,null)))
		return userService.updatePassword(collectUserModel);
		else
		return false;
	}
	
	
	
	@RequestMapping("generateReport")
	@ResponseBody
	void generateReport(HttpServletResponse response)throws Exception
	{
		InputStream inputStream;
		String fileName = "";
		try {
			fileName= masterRawDataService.getSubmissionReport().replaceAll("%3A", ":").replaceAll("%2F", "/")
						 .replaceAll("%5C", "/").replaceAll("%2C",",")
						 .replaceAll("\\+", " ").replaceAll("%22", "")
						 .replaceAll("%3F", "?").replaceAll("%3D", "=");
			inputStream = new FileInputStream(fileName);
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"",
					new java.io.File(fileName).getName());
			response.setHeader(headerKey, headerValue);
			response.setContentType("application/octet-stream"); //for all file type
			ServletOutputStream outputStream = response.getOutputStream();
			FileCopyUtils.copy(inputStream, outputStream);
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			new File(fileName).delete();
		}
	}
	
}
