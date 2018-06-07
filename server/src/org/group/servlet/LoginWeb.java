package org.group.servlet;

import org.group.server.Request;
import org.group.server.Response;

public class LoginWeb extends Servlet {

	@Override
	public void doGet(Request req, Response rep) throws Exception {
		rep.println("success.....OK");
	}

	@Override
	public void doPost(Request req, Response rep) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
