package com.klaeff.cf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
		handleGetRequest("/catalog", 200);
		handleGetRequest("/abc", 404);
		handleGetRequest("/service_instances/123", 405);
		handleGetRequest("/service_instances/123/service_bindings/456", 405);
	}

	@Test
	public void putRequest() throws Exception {
		handlePutRequest("/catalog", 405);
		handlePutRequest("/abc", 404);
		handlePutRequest("/service_instances/123", 200);
		handlePutRequest("/service_instances/123/service_bindings/456", 200);
	}

	@Test
	public void deleteRequest() throws Exception {
		handleDeleteRequest("/catalog", 405);
		handleDeleteRequest("/abc", 404);
		handleDeleteRequest("/service_instances/123", 200);
		handleDeleteRequest("/service_instances/123/service_bindings/456", 200);
	}

	private void handleGetRequest(String pathInfo, int status) throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp= mock(HttpServletResponse.class);

		when(req.getPathInfo()).thenReturn(pathInfo);
		when(resp.getWriter()).thenReturn(new PrintWriter(new NullOutputStream()));
		
		BrokerServlet.handleGet(req, resp);
		
		verify(resp, times(1)).setStatus(status);
	}	
	
	private void handlePutRequest(String pathInfo, int status) throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp= mock(HttpServletResponse.class);

		when(req.getPathInfo()).thenReturn(pathInfo);
		when(resp.getWriter()).thenReturn(new PrintWriter(new NullOutputStream()));
		
		BrokerServlet.handlePut(req, resp);
		
		verify(resp, times(1)).setStatus(status);
	}	
	
	private void handleDeleteRequest(String pathInfo, int status) throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp= mock(HttpServletResponse.class);

		when(req.getPathInfo()).thenReturn(pathInfo);
		when(resp.getWriter()).thenReturn(new PrintWriter(new NullOutputStream()));
		
		BrokerServlet.handleDelete(req, resp);
		
		verify(resp, times(1)).setStatus(status);
	}	
	
	public class NullOutputStream extends OutputStream {
		  @Override
		  public void write(int b) throws IOException {
		  }
		}
}
