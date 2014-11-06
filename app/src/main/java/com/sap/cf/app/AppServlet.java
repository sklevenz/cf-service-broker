package com.sap.cf.app;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
		
		System.out.println(content);
		
		if (content != null) {
			GsonBuilder gsonBuilder = new GsonBuilder();
			Gson gson = gsonBuilder.setPrettyPrinting().create();

			Object o = gson.fromJson(content, Object.class);
			String prettyJson = gson.toJson(o);
			
			response.getWriter().write("<html><body><b>VCAP_SERVICES</b><p><textarea cols=\"50\" rows=\"50\">" + prettyJson + "</textarea></body></html>");
		} else {
			response.getWriter().write(VCAP_SERVICES + " not set");
		}
		response.setStatus(200);
	}

}
