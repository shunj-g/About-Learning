package com.org.sorm.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * 
 * @author Lenovo
 * @version 1.0
 */
public interface CallBack {
	public Object doExecute(Connection conn,PreparedStatement ps,ResultSet rs);
}
