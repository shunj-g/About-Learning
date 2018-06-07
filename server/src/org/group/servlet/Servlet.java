package org.group.servlet;

import org.group.server.Request;
import org.group.server.Response;

/**
 * ����Ϊһ������
 * 
 *
 */
public abstract class Servlet {
	public void service(Request req,Response rep) throws Exception{
		this.doGet(req,rep);
		this.doPost(req,rep);
	}
	
	protected abstract void doGet(Request req,Response rep) throws Exception;
	protected abstract void doPost(Request req,Response rep) throws Exception;
}
