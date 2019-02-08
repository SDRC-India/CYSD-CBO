package org.sdrc.cysdcbo.core;

/**
 * @author Harsh(harsh@sdrc.in)
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sdrc.cysdcbo.model.CollectUserModel;
import org.sdrc.cysdcbo.model.FeaturePermissionMappingModel;
import org.sdrc.cysdcbo.util.Constants;
import org.sdrc.cysdcbo.util.StateManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class AuthorizeInterceptor extends HandlerInterceptorAdapter {

	private final StateManager stateManager;
	private final ResourceBundleMessageSource messages;

	@Autowired
	public AuthorizeInterceptor(StateManager stateManager,
			ResourceBundleMessageSource messages) {
		this.stateManager = stateManager;
		this.messages = messages;
	}

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) {
		
		if (!(handler instanceof HandlerMethod))
			return true;

		Authorize authorize = ((HandlerMethod) handler)
				.getMethodAnnotation(Authorize.class);

		if (authorize == null)
			return true;

		CollectUserModel user = (CollectUserModel) stateManager.getValue(Constants.USER_PRINCIPAL);
		if (user == null)
			throw new AccessDeniedException(messages.getMessage(
					Constants.ACCESS_DENIED, null, null));
		
		List<String> feature = new ArrayList<String>();
		feature =	Arrays.asList(authorize.feature().split(","));
		String permission = authorize.permission();
		
		if (user != null && user.getUserRoleFeaturePermissionMappings() != null) {
			for (int i = 0; i < user.getUserRoleFeaturePermissionMappings().size(); i++) {
				FeaturePermissionMappingModel fpMapping = user
						.getUserRoleFeaturePermissionMappings().get(i).getRoleFeaturePermissionSchemeModel().getFeaturePermissionMapping();
				if (feature.contains(fpMapping.getFeature().getFeatureName())
						&& permission.equals(fpMapping.getPermission()
								.getPermissionName())) {
					return true;
				}
			}
		}
		/*if (user != null) {	
				if (feature.equals("programXForms")
						&& permission.equals("view")){
					return true;
				}
		}*/
		throw new AccessDeniedException(messages.getMessage(
				Constants.ACCESS_DENIED, null, null));
	}

}
