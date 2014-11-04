package com.klaeff.cf.servcie;

import static org.junit.Assert.*;

import org.junit.Test;

import com.klaeff.cf.service.ServiceInstance;

public class ServiceInstanceTest {

	@Test
	public void equals() {
		ServiceInstance si1 = new ServiceInstance();
		ServiceInstance si2 = new ServiceInstance();
		
		si2.setOrganizationGuid("1");
		si2.setPlanId("2");
		si2.setServiceId("3");
		si2.setSpaceGuid("4");
		
		assertTrue(si1.equals(si1));
		assertTrue(si2.equals(si2));
		assertFalse(si1.equals(si2));
	}

}
