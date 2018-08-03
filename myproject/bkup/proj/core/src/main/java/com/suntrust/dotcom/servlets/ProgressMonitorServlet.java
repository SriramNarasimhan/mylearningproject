package com.suntrust.dotcom.servlets;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;

import com.suntrust.dotcom.utils.ProgressDetails;

@SuppressWarnings("serial")
public class ProgressMonitorServlet extends SlingAllMethodsServlet {
	protected void doPGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		// get the taskId
		String taskId = request.getParameter("taskIdentity");

		// get the progres of this task
		ProgressDetails taskProgress = ProgressDetails.taskProgressHash.get(taskId);

		// write the progress in the response
		response.getWriter().write(taskProgress.toString());
	}
}