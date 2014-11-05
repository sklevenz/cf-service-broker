package com.sap.cf.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AppServlet
 */
public class AppServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String VCAP_SERVICES = "VCAP_SERVICES";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String content = System.getenv(VCAP_SERVICES);

		if (content != null) {
			response.getWriter().write(content);
		} else {
			response.getWriter().write(VCAP_SERVICES + " not set");
		}
		response.setStatus(200);
	}

}
