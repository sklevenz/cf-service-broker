package com.klaeff.cf;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.klaeff.cf.service.Service;
import com.klaeff.cf.service.Services;
import com.klaeff.cf.service.ServicesFactory;

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
		handleRequest("/catalog", 200, "get", null);
		handleRequest("/abc", 404, "get", null);
		handleRequest("/service_instances/123", 405, "get", null);
		handleRequest("/service_instances/123/service_bindings/456", 405,
				"get", null);
	}

	@Test
	public void putRequest() throws Exception {
		handleRequest("/catalog", 405, "put", null);
		handleRequest("/abc", 404, "put", null);

		handleRequest("/service_instances/123", 201, "put",
				"/instanceBody.json");
		assertTrue(BrokerServlet.containsInstance("123"));
		handleRequest("/service_instances/123", 200, "put",
				"/instanceBody.json");
		handleRequest("/service_instances/123", 409, "put",
				"/instanceBodyDiff.json");

		handleRequest("/service_instances/123/service_bindings/456", 201,
				"put", "/bindingBody.json");
		assertTrue(BrokerServlet.containsBinding("456"));
		handleRequest("/service_instances/123/service_bindings/456", 200,
				"put", "/bindingBody.json");
		handleRequest("/service_instances/123/service_bindings/456", 409,
				"put", "/bindingBodyDiff.json");

		handleRequest("/service_instances/xxx/service_bindings/456", 404,
				"put", "/bindingBody.json");
	}

	@Test
	public void testCatalog() throws Exception {
		String catalog = BrokerServlet.createCatalog("http", "localhost", 8080);

		assertNotNull(catalog);
		assertTrue(catalog.contains("http://localhost:8080/service-broker/"));
	}

	@Test
	public void deleteRequest() throws Exception {
		handleRequest("/catalog", 405, "delete", null);
		handleRequest("/abc", 404, "delete", null);
		handleRequest("/service_instances/123", 200, "delete", null);
		handleRequest("/service_instances/123/service_bindings/456", 200,
				"delete", null);

		handleRequest("/service_instances/abc", 201, "put",
				"/instanceBody.json");
		assertTrue(BrokerServlet.containsInstance("abc"));
		handleRequest("/service_instances/abc/service_bindings/xyz", 201,
				"put", "/bindingBody.json");
		assertTrue(BrokerServlet.containsBinding("xyz"));

		// delete binding
		handleRequest("/service_instances/abc/service_bindings/xyz", 200,
				"delete", "/bindingBody.json");
		assertFalse(BrokerServlet.containsBinding("xyz"));
		handleRequest("/service_instances/abc/service_bindings/xyz", 410,
				"delete", "/bindingBody.json");

		// instance
		handleRequest("/service_instances/abc", 200,
				"delete", "/bindingBody.json");
		assertFalse(BrokerServlet.containsInstance("abc"));
		handleRequest("/service_instances/abc", 410,
				"delete", "/bindingBody.json");

	}

	private void handleRequest(String pathInfo, int status, String method,
			String bodyRef) throws IOException {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);

		when(req.getPathInfo()).thenReturn(pathInfo);
		when(resp.getWriter()).thenReturn(new PrintWriter(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// > null
			}
		}));

		if (bodyRef != null) {
			ServletInputStream sis = new DelegatingServletInputStream(
					BrokerServletTest.class.getResourceAsStream(bodyRef));
			when(req.getInputStream()).thenReturn(sis);
		}

		if ("get".equalsIgnoreCase(method)) {
			BrokerServlet.handleGet(req, resp);
		} else if ("put".equalsIgnoreCase(method)) {
			BrokerServlet.handlePut(req, resp);
		} else if ("delete".equalsIgnoreCase(method)) {
			BrokerServlet.handleDelete(req, resp);
		}

		verify(resp, times(1)).setStatus(status);
	}

	@Test
	public void createService() {
		Services ss = ServicesFactory.create();

		assertNotNull(ss);

		Service s = ss.getServices()[0];
		assertNotNull(s);

		assertNotNull(s.getId());
		assertNotNull(s.getName());
		assertNotNull(s.getDescription());

		assertNotNull(s.getTags());
		assertEquals("tag1", s.getTags()[0]);
		assertEquals("tag2", s.getTags()[1]);

		assertNotNull(s.getDashboardClient());
		assertEquals("client-id-1", s.getDashboardClient().getId());
		assertEquals("secret-1", s.getDashboardClient().getSecret());
		assertEquals("{0}://{1}:{2,number,#}/service-broker/", s
				.getDashboardClient().getRedirectUrl());

		assertNotNull(s.getMetadata());
		assertEquals("mock service", s.getMetadata().getDisplayName());
		assertEquals("{0}://{1}:{2,number,#}/service-broker/", s.getMetadata()
				.getDocumentationUrl());
		assertEquals("{0}://{1}:{2,number,#}/service-broker/img/service.jpg", s
				.getMetadata().getImageUrl());
		assertEquals("This is a mock service provider", s.getMetadata()
				.getLongDescription());
		assertEquals("mock service provider", s.getMetadata()
				.getProviderDisplayName());
		assertEquals("{0}://{1}:{2,number,#}/service-broker/", s.getMetadata()
				.getSupportUrl());

		assertNotNull(s.getPlans());
		assertNotNull(s.getPlans()[0].getId());
		assertEquals("small", s.getPlans()[0].getName());
		assertEquals("A small plan", s.getPlans()[0].getDescription());

		assertNotNull(s.getPlans()[0].getMetadata());
		assertEquals("service plan costs", s.getPlans()[0].getMetadata()
				.getDisplayName());
		assertEquals("10GB", s.getPlans()[0].getMetadata().getBullets()[0]);
		assertEquals("20GB", s.getPlans()[0].getMetadata().getBullets()[1]);
		assertEquals("service plan costs", s.getPlans()[0].getMetadata()
				.getDisplayName());
		assertNotNull(s.getPlans()[0].getMetadata().getCosts());
		assertEquals("1GB of messages over 20GB", s.getPlans()[0].getMetadata()
				.getCosts()[0].getUnit());
		assertNotNull(s.getPlans()[0].getMetadata().getCosts()[0].getAmounts());
		assertNotNull(s.getPlans()[0].getMetadata().getCosts()[0].getAmounts()[0]
				.getAmount());
		assertEquals("12.54",
				s.getPlans()[0].getMetadata().getCosts()[0].getAmounts()[0]
						.getAmount().get("usd"));
		assertEquals("10.12",
				s.getPlans()[0].getMetadata().getCosts()[0].getAmounts()[0]
						.getAmount().get("eur"));

		assertNotNull(s.getPlans()[1].getId());
		assertEquals("large", s.getPlans()[1].getName());
		assertEquals("A large plan", s.getPlans()[1].getDescription());

		assertNotNull(s.getPlans()[1].getMetadata());
		assertEquals("service plan costs", s.getPlans()[1].getMetadata()
				.getDisplayName());
		assertEquals("10GB", s.getPlans()[1].getMetadata().getBullets()[0]);
		assertEquals("20GB", s.getPlans()[1].getMetadata().getBullets()[1]);
		assertEquals("service plan costs", s.getPlans()[1].getMetadata()
				.getDisplayName());
		assertNotNull(s.getPlans()[1].getMetadata().getCosts());
		assertEquals("1GB of messages over 20GB", s.getPlans()[1].getMetadata()
				.getCosts()[0].getUnit());
		assertNotNull(s.getPlans()[1].getMetadata().getCosts()[0].getAmounts());
		assertNotNull(s.getPlans()[1].getMetadata().getCosts()[0].getAmounts()[0]
				.getAmount());
		assertEquals("12.54",
				s.getPlans()[1].getMetadata().getCosts()[0].getAmounts()[0]
						.getAmount().get("usd"));
		assertEquals("10.12",
				s.getPlans()[1].getMetadata().getCosts()[0].getAmounts()[0]
						.getAmount().get("eur"));

		assertTrue(s.isBindable());
	}

	public class DelegatingServletInputStream extends ServletInputStream {

		private final InputStream sourceStream;

		/**
		 * Create a DelegatingServletInputStream for the given source stream.
		 * 
		 * @param sourceStream
		 *            the source stream (never <code>null</code>)
		 */
		public DelegatingServletInputStream(InputStream sourceStream) {
			assertNotNull(sourceStream);
			this.sourceStream = sourceStream;
		}

		/**
		 * Return the underlying source stream (never <code>null</code>).
		 */
		public final InputStream getSourceStream() {
			return this.sourceStream;
		}

		@Override
		public int read() throws IOException {
			return this.sourceStream.read();
		}

		@Override
		public void close() throws IOException {
			super.close();
			this.sourceStream.close();
		}

	}
}
