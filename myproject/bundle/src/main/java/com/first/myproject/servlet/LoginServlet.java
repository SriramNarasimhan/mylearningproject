package com.first.myproject.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.jcr.Credentials;
import javax.jcr.RepositoryException;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SlingServlet(paths = "/bin/security_check", methods = "POST")
public class LoginServlet extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;
	private Logger log = LoggerFactory.getLogger(LoginServlet.class);

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		String message = null;
		String DEALER_GROUP_NAME = "dealer";
		Authorizable member = null;

		try {
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			String email = request.getParameter("email");

			ResourceResolver resourceResolver = request.getResourceResolver();

			UserManager userManager = resourceResolver
					.adaptTo(UserManager.class);
			final Group group = (Group) userManager
					.getAuthorizable(DEALER_GROUP_NAME);
			Iterator<Authorizable> members = group.getMembers();

			while (members.hasNext()) {

				member = members.next();
				String currentUser = member.getID().toString();
				String userLastName = null, userEmailId = null;
				Boolean userPwd = false;

				/* to get the current user */
				Authorizable authUser = userManager
						.getAuthorizable(currentUser);
				/* to get the property of the authorizable. Use relative path */
				if (authUser.hasProperty("profile/familyName")) {
					userLastName = member.getProperty("profile/familyName")[0]
							.getString();
				}
				if (authUser.hasProperty("profile/email")) {
					userEmailId = member.getProperty("profile/email")[0]
							.getString();
				}
				if (StringUtils.equals(email, userEmailId)
						&& StringUtils.isNotBlank(email)) {
					message = "sendpassword";
					out.print(message);
				}

				userPwd = checkPassword(authUser, password);

				if (StringUtils.equals(username, userLastName)
						&& StringUtils.isNotBlank(userLastName)
						&& userPwd.equals(true)) {

					request.getSession().setAttribute("username", username);
					message = "loginsuccess";
					out.print(message);
					break;
				}
			}
		} catch (Exception e) {
			log.debug("exception in Login servlet ==" + e.getMessage());
		}
	}

	private Boolean checkPassword(Authorizable member, String password)
			throws RepositoryException {

		Credentials credentials = ((User) member).getCredentials();
		if (credentials instanceof SimpleCredentials) {
			char[] userCredsPwd = ((SimpleCredentials) credentials)
					.getPassword();
			if (password.equals(String.valueOf(userCredsPwd))) {
				return true;
			}
		} else {
			try {
				Class<?> userCredsClass = credentials.getClass();
				Method matcher = userCredsClass.getMethod("matches",
						SimpleCredentials.class);
				SimpleCredentials userCreds = new SimpleCredentials(member
						.getPrincipal().getName(), password.toCharArray());
				boolean match = (Boolean) matcher
						.invoke(credentials, userCreds);
				if (match) {
					return true;
				} else {
					return false;
				}
			} catch (Throwable t) {
				// failure here, fall back to password check failure below
				log.info("Passwords check fail");
			}
		}
		throw new RepositoryException("Passwords do not match");

	}

}