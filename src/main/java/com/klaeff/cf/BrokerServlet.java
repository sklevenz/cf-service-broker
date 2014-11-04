package com.klaeff.cf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.klaeff.cf.service.ServiceUri;
import com.klaeff.cf.service.ServiceUriSerializer;
import com.klaeff.cf.service.Services;
import com.klaeff.cf.service.ServicesFactory;

public class BrokerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Services services =ServicesFactory.create();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleGet(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handlePut(req, resp);
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		handleDelete(req, resp);
	}

	public static void handleGet(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		try {
			UrlParserResult r = parse(req.getPathInfo());

			switch (r.getResourceType()) {
			case Catalog:
				String catalog = createCatalog(req.getScheme(),
						req.getServerName(), req.getServerPort());

				resp.getWriter().print(catalog);
				resp.setStatus(200);

				break;
			default:
				throw new MethodNotAllowedException("GET");
			}

		} catch (NotFoundException e) {
			resp.getWriter().print("404 - not found - " + req.getPathInfo());
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			resp.getWriter().print("405 - method not allowed - GET");
			resp.setStatus(405);
		}
	}

	public static String createCatalog(String scheme, String host, int port)
			throws IOException {
		String catalog = toJson(scheme, host, port);

		return catalog;
	}

	public static void handlePut(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		try {
			UrlParserResult r = parse(req.getPathInfo());

			switch (r.getResourceType()) {
			case Instance:
				resp.setStatus(200);
				break;
			case Binding:
				resp.setStatus(200);
				break;
			default:
				throw new MethodNotAllowedException("PUT");
			}
		} catch (NotFoundException e) {
			resp.getWriter().print("404 - not found - " + req.getPathInfo());
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			resp.getWriter().print("405 - method not allowed - PUT");
			resp.setStatus(405);
		}
	}

	public static void handleDelete(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		try {
			UrlParserResult r = parse(req.getPathInfo());

			switch (r.getResourceType()) {
			case Instance:
				resp.setStatus(200);
				break;
			case Binding:
				resp.setStatus(200);
				break;
			default:
				throw new MethodNotAllowedException("DELETE");
			}
		} catch (NotFoundException e) {
			resp.getWriter().print("404 - not found - " + req.getPathInfo());
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			resp.getWriter().print("405 - method not allowed - DELETE");
			resp.setStatus(405);
		}
	}

	public static UrlParserResult parse(String pathInfo)
			throws NotFoundException {
		UrlParserResult result = new UrlParserResult();

		if (pathInfo == null) {
			throw new NotFoundException(pathInfo);
		} else if ("/catalog".equals(pathInfo)) {
			result.setResourceType(ResourceType.Catalog);
		} else {
			String[] splits = pathInfo.split("/");
			if (splits.length == 3) {
				if ("service_instances".equals(splits[1])
						&& "".equals(splits[0])) {
					result.setResourceType(ResourceType.Instance);
					result.setInstanceId(splits[2]);
				} else {
					throw new NotFoundException(pathInfo);
				}
			} else if (splits.length == 5) {
				if ("service_instances".equals(splits[1])
						&& "service_bindings".equals(splits[3])
						&& "".equals(splits[0])) {
					result.setResourceType(ResourceType.Binding);
					result.setInstanceId(splits[2]);
					result.setBindingId(splits[4]);
				} else {
					throw new NotFoundException(pathInfo);
				}
			} else {
				throw new NotFoundException(pathInfo);
			}
		}

		return result;
	}

	public static String toJson(String scheme, String host, int port)
			throws IOException {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ServiceUri.class,
				new ServiceUriSerializer(scheme, host, port));
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		String json = gson.toJson(services);
		return json;

	}

	public static String fromStream(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder out = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		String line;
		while ((line = reader.readLine()) != null) {
			out.append(line);
			out.append(newLine);
		}
		return out.toString();
	}

}
