package com.klaeff.cf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.klaeff.cf.service.ServiceBinding;
import com.klaeff.cf.service.ServiceInstance;
import com.klaeff.cf.service.ServiceUri;
import com.klaeff.cf.service.ServiceUriSerializer;
import com.klaeff.cf.service.Services;
import com.klaeff.cf.service.ServicesFactory;

public class BrokerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Services services = ServicesFactory.create();
	private static Hashtable<String, ServiceInstance> instanceRegistry = new Hashtable<String, ServiceInstance>();
	private static Hashtable<String, ServiceBinding> bindingRegistry = new Hashtable<String, ServiceBinding>();

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
			errorResponseBody(req, resp, "not found - " + req.getPathInfo(), 404);
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			errorResponseBody(req, resp, "method not allowed - GET" , 405);
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
				String instanceBody = fromStream(req.getInputStream());

				if (instanceRegistry.containsKey(r.getInstanceId())) {
					ServiceInstance si1 = instanceRegistry.get(r
							.getInstanceId());
					ServiceInstance si2 = deserializeInstance(instanceBody);

					if (si1.equals(si2)) {
						resp.getWriter().print(
								creatDashboardUrlJson(req.getScheme(),
										req.getServerName(),
										req.getServerPort()));
						;
						resp.setStatus(200); // ok
					} else {
						resp.getWriter().print("{}");
						resp.setStatus(409); // conflict
					}
				} else {
					ServiceInstance si = deserializeInstance(instanceBody);
					instanceRegistry.put(r.getInstanceId(), si);
					resp.getWriter().print(
							creatDashboardUrlJson(req.getScheme(),
									req.getServerName(), req.getServerPort()));
					;
					resp.setStatus(201); // created
				}

				break;
			case Binding:
				String bindingBody = fromStream(req.getInputStream());

				if (instanceRegistry.containsKey(r.getInstanceId())) {

					if (bindingRegistry.containsKey(r.getBindingId())) {
						ServiceBinding sb1 = bindingRegistry.get(r
								.getBindingId());
						ServiceBinding sb2 = deserializeBinding(bindingBody);

						if (sb1.equals(sb2)) {
							resp.setStatus(200); // ok
						} else {
							errorResponseBody(req, resp, "conflict" , 409);
							resp.setStatus(409); // conflict
						}
					} else {
						ServiceBinding sb = deserializeBinding(bindingBody);
						bindingRegistry.put(r.getBindingId(), sb);
						resp.setStatus(201); // created
					}
				} else {
					throw new NotFoundException("instance(" + r.getInstanceId()
							+ ")");
				}

				break;
			default:
				throw new MethodNotAllowedException("PUT");
			}
		} catch (NotFoundException e) {
			errorResponseBody(req, resp, "not found - " + req.getPathInfo(), 404);
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			errorResponseBody(req, resp, "method not allowed - GET" , 405);
			resp.setStatus(405);
		}
	}

	private static String creatDashboardUrlJson(String scheme, String host,
			int port) {
		String url = MessageFormat.format(
				"{0}://{1}:{2,number,#}/service-broker/", scheme, host, port);
		String json = "{\n\t\"dashboard_url\": \"" + url + "\"\n}";
		return json;
	}

	private static ServiceBinding deserializeBinding(String body) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(body, ServiceBinding.class);
	}

	private static ServiceInstance deserializeInstance(String body) {
		Gson gson = new GsonBuilder().create();
		return gson.fromJson(body, ServiceInstance.class);
	}

	public static void handleDelete(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		try {
			UrlParserResult r = parse(req.getPathInfo());

			switch (r.getResourceType()) {
			case Instance:
				if (instanceRegistry.containsKey(r.getInstanceId())) {
					instanceRegistry.remove(r.getInstanceId());
					resp.setStatus(200);
				} else {
					errorResponseBody(req, resp, "gone" , 410);
					resp.setStatus(410);
				}
				break;
			case Binding:
				if (bindingRegistry.containsKey(r.getBindingId())) {
					bindingRegistry.remove(r.getBindingId());
					resp.setStatus(200);
				} else {
					errorResponseBody(req, resp, "gone" , 410);
					resp.setStatus(410);
				}
				break;
			default:
				throw new MethodNotAllowedException("DELETE");
			}
		} catch (NotFoundException e) {
			errorResponseBody(req, resp, "not found - " + req.getPathInfo(), 404);
			resp.setStatus(404);
		} catch (MethodNotAllowedException e) {
			errorResponseBody(req, resp, "method not allowed - GET" , 405);
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

	public static boolean containsInstance(String instanceId) {
		return instanceRegistry.containsKey(instanceId);
	}

	public static boolean containsBinding(String bindingId) {
		return bindingRegistry.containsKey(bindingId);
	}

	private static String registryAsJson(Hashtable<?, ?> tab) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();
		String json = gson.toJson(tab);
		return json;
	}

	public static String bindingRegistryAsJson() {
		return registryAsJson(bindingRegistry);
	}

	public static String instanceRegistryAsJson() {
		return registryAsJson(instanceRegistry);
	}

	private static void errorResponseBody(HttpServletRequest req,
			HttpServletResponse resp, String msg, int code) throws IOException {
		GsonBuilder gsonBuilder = new GsonBuilder();
		Gson gson = gsonBuilder.setPrettyPrinting().create();

		JsonObject error = new JsonObject();
		error.addProperty("description", msg + " (" + code + ")");

		String json = gson.toJson(error);

		resp.getWriter().print(json);
	}

}
