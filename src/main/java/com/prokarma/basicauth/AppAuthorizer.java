package com.prokarma.basicauth;

import io.dropwizard.auth.Authorizer;

public class AppAuthorizer implements Authorizer<BasicAuthUser> {

	@Override
	public boolean authorize(BasicAuthUser user, String role) {
		return user.getRoles() != null && user.getRoles().contains(role);
	}
}