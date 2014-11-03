package com.klaeff.cf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class BrokerServletTest {

	@Test
	public void parseNonsens() {
		String[] nonsens = { null, "/", "abc", "", "a/b/b/b", "/catalog/",
				"/catalog/123", "/service_instances",
				"/service_instances/ssdd/dgdfgd",
				"/service_instances/ssdd/service_bindings",
				"/service_instances/ssdd/service_bindings/",
				"/service_instances/ssdd/service_bindings/dd/ghj", };
		for (String pathInfo : nonsens) {
			try {
				UrlParserResult r1 = BrokerServlet.parse(pathInfo);
				fail(pathInfo + " didn't fail");
			} catch (NotFoundException e) {
				// expected
			}
		}
	}

	@Test
	public void parseCatalog() throws Exception {
		UrlParserResult r1 = BrokerServlet.parse("/catalog");
		assertNotNull(r1);
		assertNull(r1.getBindingId());
		assertNull(r1.getInstanceId());
		assertEquals(ResourceType.Catalog, r1.getResourceType());
	}

	@Test
	public void parseInstanceUrl() throws Exception {
		UrlParserResult r1 = BrokerServlet
				.parse("/service_instances/1234abcd/");
		assertNotNull(r1);
		assertNull(r1.getBindingId());
		assertEquals("1234abcd", r1.getInstanceId());
		assertEquals(ResourceType.Instance, r1.getResourceType());
	}

	@Test
	public void parseBindingUrl() throws Exception {
		UrlParserResult r1 = BrokerServlet
				.parse("/service_instances/1234abcd/service_bindings/12345");
		assertNotNull(r1);
		assertEquals("12345", r1.getBindingId());
		assertEquals("1234abcd", r1.getInstanceId());
		assertEquals(ResourceType.Binding, r1.getResourceType());
	}

	@Test
	public void getRequest() throws Exception {
		handleRequest("/catalog", 200, "get");
		handleRequest("/abc", 404, "get");
		handleRequest("/service_instances/123", 405, "get");
		handleRequest("/service_instances/123/service_bindings/456", 405, "get");
	}

	@Test
	public void putRequest() throws Exception {
		handleRequest("/catalog", 405, "put");
		handleRequest("/abc", 404, "put");
		handleRequest("/service_instances/123", 200, "put");
		handleRequest("/service_instances/123/service_bindings/456", 200, "put");
	}

	@Test
	public void testCatalog() throws Exception {
		String catalog = BrokerServlet.createCatalog("localhost", 8080);

		System.out.println(catalog);
		
		assertNotNull(catalog);
		assertTrue(catalog.contains("http://localhost:8080/service-broker/"));
	}
	
	@Test
	public void deleteRequest() throws Exception {
		handleRequest("/catalog", 405, "delete");
		handleRequest("/abc", 404, "delete");
		handleRequest("/service_instances/123", 200, "delete");
		handleRequest("/service_instances/123/service_bindings/456", 200,
				"delete");
	}

	private void handleRequest(String pathInfo, int status, String method)
			throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);

		when(req.getPathInfo()).thenReturn(pathInfo);
		when(resp.getWriter()).thenReturn(
				new PrintWriter(new NullOutputStream()));

		if ("get".equalsIgnoreCase(method)) {
			BrokerServlet.handleGet(req, resp);
		} else if ("put".equalsIgnoreCase(method)) {
			BrokerServlet.handlePut(req, resp);
		} else if ("delete".equalsIgnoreCase(method)) {
			BrokerServlet.handleDelete(req, resp);
		}

		verify(resp, times(1)).setStatus(status);
	}

	public class NullOutputStream extends OutputStream {
		@Override
		public void write(int b) throws IOException {
		}
	}
}
