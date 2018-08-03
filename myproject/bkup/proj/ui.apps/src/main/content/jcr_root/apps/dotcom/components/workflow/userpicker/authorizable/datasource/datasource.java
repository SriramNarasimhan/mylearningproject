package apps.dotcom.components.workflow.userpicker.authorizable.datasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFactory;
import javax.jcr.Node;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;

import org.apache.sling.commons.osgi.PropertiesUtil;

import com.adobe.cq.sightly.WCMUsePojo;
import com.suntrust.dotcom.config.SuntrustDotcomService;

import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.QueryBuilder;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.scripting.SlingBindings;
import org.apache.sling.api.scripting.SlingScriptHelper;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;

import com.adobe.granite.security.user.UserProperties;
import com.adobe.granite.security.user.UserPropertiesService;
import com.adobe.granite.ui.components.Config;
import com.adobe.granite.ui.components.ExpressionHelper;
import com.adobe.granite.ui.components.ExpressionResolver;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class datasource extends SlingSafeMethodsServlet{

	private static final String PATH_GIVEN_NAME = UserPropertiesService.PROFILE_PATH
			+ "/@" + UserProperties.GIVEN_NAME;
	private static final String PATH_FAMILY_NAME = UserPropertiesService.PROFILE_PATH
			+ "/@" + UserProperties.FAMILY_NAME;
	private static final String PATH_DISPLAY_NAME = UserPropertiesService.PROFILE_PATH
			+ "/@" + UserProperties.DISPLAY_NAME;
	private static final String PATH_EMAIL = UserPropertiesService.PROFILE_PATH
			+ "/@" + UserProperties.EMAIL;
	private static String USER_GROUP = "";
	private static String USER_AD_GROUP = "";

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws ServletException,
			IOException {
		try {

           String property;
           SlingBindings bindings = (SlingBindings) request.getAttribute(SlingBindings.class.getName());
           SlingScriptHelper scriptHelper = bindings.getSling();           
           SuntrustDotcomService dotcomServiceconfig = scriptHelper.getService(SuntrustDotcomService.class);


			SlingScriptHelper sling = getScriptHelper(request);
			final ResourceResolver resolver = request.getResourceResolver();
			Session session = resolver.adaptTo(Session.class);
			UserManager um = resolver.adaptTo(UserManager.class);
			ExpressionHelper ex = new ExpressionHelper(
					sling.getService(ExpressionResolver.class), request);
			User currentUser = resolver.adaptTo(User.class);

			Config dsCfg = new Config(request.getResource().getChild(
					Config.DATASOURCE));

			String queryString = ex.getString(dsCfg.get("query", String.class));
			long offset = ex.get(dsCfg.get("offset", "0"), long.class);
			long limit = ex.get(dsCfg.get("limit", "20"), long.class);

			// user picker customized to list down the users from custom groups
			 USER_AD_GROUP=ex.getString(dsCfg.get("userGroup", String.class));
			 if (dotcomServiceconfig != null) {
				 USER_GROUP = dotcomServiceconfig.getPropertyValue(USER_AD_GROUP);
	         }			
			if (USER_GROUP == "") {
				// userGroup parameter is missing or null so assigning everyone
				// as group
				System.out.println("userGroup parameter is missing or empty");
				USER_GROUP = "everyone";
			}
			Authorizable authGroup = um.getAuthorizable(USER_GROUP);

			if (authGroup != null && authGroup.isGroup()) {
				Group group = (Group) authGroup;
				Iterator<Authorizable> groupMembersList = group.getMembers();
				if (groupMembersList.hasNext() == false) {
					System.out.println("group " + USER_GROUP
							+ " has no members.");
					// no members in group so assigning everyone group
					USER_GROUP = "everyone";
				}
			} else {
				System.out.println("group " + USER_GROUP + " does not exists");
				// group does not exists so assigning everyone group
				USER_GROUP = "everyone";
			}

			QuerySelector selector = QuerySelector.fromName(ex.getString(dsCfg
					.get("selector", String.class)));
			Boolean serviceUserFilter = parseBooleanFilter(ex.getString(dsCfg
					.get("serviceUserFilter", "exclude")));
			Boolean impersonableUserFilter = parseBooleanFilter(ex
					.getString(dsCfg
							.get("impersonableUserFilter", String.class)));

			Query query = createQuery(session, currentUser, offset, limit,
					queryString, selector, serviceUserFilter,
					impersonableUserFilter);

			Iterator<Authorizable> authorizables = um.findAuthorizables(query);

			@SuppressWarnings("unchecked")
			final DataSource datasource = new SimpleDataSource(
					new TransformIterator(authorizables, new Transformer() {
						@Override
						public Object transform(Object o) {
							try {
								return resolver.getResource(((Authorizable) o)
										.getPath());
							} catch (RepositoryException e) {
								throw new RuntimeException(e);
							}
						}
					}));

			request.setAttribute(DataSource.class.getName(), datasource);
		} catch (RepositoryException e) {
			throw new ServletException(e);
		}
	}

	private static SlingScriptHelper getScriptHelper(ServletRequest request) {
		SlingBindings bindings = (SlingBindings) request
				.getAttribute(SlingBindings.class.getName());
		return bindings.getSling();
	}

	private static Boolean parseBooleanFilter(String value) {
		if ("includeonly".equals(value)) {
			return true;
		} else if ("exclude".equals(value)) {
			return false;
		}
		return null;
	}

	private static Query createQuery(final Session session,
			final User currentUser, final long offset, final long limit,
			final String queryString, final QuerySelector selector,
			final Boolean serviceUserFilter,
			final Boolean impersonableUserFilter) {

		return new Query() {
			@Override
			public <T> void build(QueryBuilder<T> builder) {
				try {
					builder.setLimit(offset, limit);
					builder.setSelector(selector.clazz);
					// added setScope to load the users from group
					builder.setScope(USER_GROUP, true);

					T condition = createTermCondition(queryString, builder);

					if (serviceUserFilter != null) {
						if (serviceUserFilter) {
							condition = and(
									builder,
									condition,
									createServiceUserCondition(builder,
											session.getValueFactory()));
						} else {
							condition = and(
									builder,
									condition,
									builder.not(createServiceUserCondition(
											builder, session.getValueFactory())));
						}
					}

					if (impersonableUserFilter != null) {
						if (impersonableUserFilter) {
							condition = and(
									builder,
									condition,
									createImpersonateCondition(currentUser,
											builder));
						} else {
							condition = and(builder, condition,
									builder.not(createImpersonateCondition(
											currentUser, builder)));
						}
					}

					if (condition != null) {
						builder.setCondition(condition);
					}
				} catch (RepositoryException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	private static <T> T createTermCondition(String query,
			QueryBuilder<T> builder) {
		T condition = null;

		if (StringUtils.isNotBlank(query)) {
			String pattern = "%" + query.toLowerCase() + "%";

			T termCondition = builder.or(builder.like(
					"fn:lower-case(@rep:principalName)", pattern), builder.or(
					builder.like("fn:lower-case(" + PATH_GIVEN_NAME + ")",
							pattern), builder.or(builder.like("fn:lower-case("
							+ PATH_FAMILY_NAME + ")", pattern), builder.or(
							builder.like("fn:lower-case(" + PATH_DISPLAY_NAME
									+ ")", pattern), builder.like(
									"fn:lower-case(" + PATH_EMAIL + ")",
									pattern)))));
			condition = and(builder, condition, termCondition);
		}

		return condition;
	}

	private static <T> T createServiceUserCondition(QueryBuilder<T> builder,
			ValueFactory valueFactory) {
		return builder.eq("@jcr:primaryType",
				valueFactory.createValue("rep:SystemUser"));
	}

	private static <T> T createImpersonateCondition(User currentUser,
			QueryBuilder<T> builder) throws RepositoryException {
		if (currentUser.isAdmin()) {
			// give all users EXCEPT oneself and "anonymous"
			return builder.and(
					builder.not(builder.nameMatches(currentUser.getID())),
					builder.not(builder.nameMatches("anonymous")));
		} else {
			// TODO The current code is ported from previous implementation, but
			// it seems the logic is wrong.
			// It should be the other way around.
			// i.e. We want to find the list of users that the current user can
			// impersonate.
			return builder.impersonates(currentUser.getPrincipal().getName());
		}
	}

	private static <T> T and(QueryBuilder<T> builder, T condition1, T condition2) {
		return (condition1 != null) ? builder.and(condition1, condition2)
				: condition2;
	}

	private static enum QuerySelector {
		All(Authorizable.class), User(User.class), Group(Group.class);

		private Class<? extends Authorizable> clazz;

		private QuerySelector(Class<? extends Authorizable> clazz) {
			this.clazz = clazz;
		}

		public static QuerySelector fromName(String name) {
			if (StringUtils.isNotBlank(name)) {
				for (QuerySelector e : QuerySelector.values()) {
					if (name.equals(e.name().toLowerCase())) {
						return e;
					}
				}
			}
			return All;
		}


}
}
