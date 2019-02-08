package org.sdrc.cysdcbo.web;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.service.UserService;
import org.sdrc.cysdcbo.util.Constants;
import org.sdrc.cysdcbo.util.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


/**
 * @author Harsh
 * @since version 1.0.0.0
 *
 */

@Controller
public class LoginController implements AuthenticationProvider{
	
	@Autowired
	private ResourceBundleMessageSource messages;
	
	@Autowired
	
	private final StateManager stateManager;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private MessageDigestPasswordEncoder  passwordEncoder;
	
	@Autowired
	public LoginController(StateManager stateManager){
		this.stateManager = stateManager;
	}
	
	@RequestMapping(value="/updateTable")
	public boolean updateTable()
	{
		return userService.insertUserTable();
	}
	
	
	@RequestMapping(value={"/","/home"})
	public String homePage()
	{
		if(stateManager.getValue(Constants.USER_PRINCIPAL)==null)
		return "home";
		
		else
			return "redirect:/dashboard";
	}
	
	@RequestMapping(value="/login",method=RequestMethod.POST)
	public String authorize(HttpServletRequest request, 
							RedirectAttributes redirectAttributes,
							@RequestParam("username") String username,
							@RequestParam("password") String password,
							Model model) throws IOException{
		List<String> errMessgs = new ArrayList<String>();
		try {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
			token.setDetails(new WebAuthenticationDetails(request));
			Authentication authentication = this.authenticate(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			e.printStackTrace();
			SecurityContextHolder.getContext().setAuthentication(null);
			errMessgs.add(messages.getMessage("invalid.username.password", null, null));
			redirectAttributes.addFlashAttribute("formError", errMessgs);
			redirectAttributes.addFlashAttribute("className",messages.getMessage("bootstrap.alert.danger",null, null));
			return "redirect:/";
		}
		return "redirect:/dashboard";
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
	CollectUserModel	collectUserModel=userService.findByUserName(authentication.getName());
//	System.out.println(passwordEncoder.encodePassword(authentication.getCredentials().toString(), authentication.getPrincipal().toString()));	
	if (collectUserModel == null || !(passwordEncoder.isPasswordValid(collectUserModel.getPassword(), authentication.getCredentials().toString(), authentication.getPrincipal().toString())))
			throw new BadCredentialsException("Invalid User!");
		
		long loginMetaId = 0;
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
				.currentRequestAttributes();
		HttpServletRequest request = attr.getRequest();
		String sessionID=request.getHeader(Constants.COOKIES);
		String ipAddress = getIpAddr(request);
		String userAgent = request.getHeader("User-Agent");
		loginMetaId=userService.saveUserLoginMeta(ipAddress,
				collectUserModel.getUserId(), userAgent,sessionID);
		collectUserModel.setUserLoginMetaId(loginMetaId);
		stateManager.setValue(Constants.USER_PRINCIPAL,collectUserModel);
		return new UsernamePasswordAuthenticationToken(authentication.getName(), (String)authentication.getCredentials(), null);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return false;
	}
	
	private String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse resp, RedirectAttributes redirectAttributes)
			throws IOException, ServletException {
		
		HttpSession session=request.getSession(false);
		if(session !=null){
			long loginId=((CollectUserModel) stateManager
					.getValue(Constants.USER_PRINCIPAL)).getUserLoginMetaId();
			stateManager.setValue(Constants.USER_PRINCIPAL, null);
			request.getSession().setAttribute(Constants.USER_PRINCIPAL, null);
			request.getSession().invalidate();
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
					.currentRequestAttributes();
			attr.getRequest().getSession(true)
					.removeAttribute(Constants.USER_PRINCIPAL);
			attr.getRequest().getSession(true).invalidate();
			 userService.updateLoggedOutStatus(loginId, new
			 Timestamp(new Date().getTime()));
	
			request.logout();
	
			List<String> errMessgs = new ArrayList<>();
			
			errMessgs.add("Successfully logged out!!");
			redirectAttributes.addFlashAttribute("formError", errMessgs);
			redirectAttributes.addFlashAttribute("className",messages.getMessage("bootstrap.alert.success",null, null));
			return "redirect:/";
		}
		else{
			request.getSession().invalidate();
			return "redirect:/";
		}
	}
	
	
	
	
	
}
