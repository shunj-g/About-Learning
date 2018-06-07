package com.org.sorm.pool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.sorm.core.DBManager;

/**
 * ���ӳص���
 * @author Lenovo
 * @version 1.0
 */
public class DBConnPool {
	/**
	 * ���ӳض���
	 */
	private  List<Connection> pool;  
	
	/**
	 * ���������
	 */
	private static final int POOL_MAX_SIZE = DBManager.getConf().getPoolMaxSize(); 
	/**
	 * ��С���ӳ�
	 */
	private static final int POOL_MIN_SIZE = DBManager.getConf().getPoolMinSize();  
	
	
	/**
	 * ��ʼ�����ӳأ�ʹ���е��������ﵽ��Сֵ
	 */
	public void initPool() {
		if(pool==null){
			pool = new ArrayList<Connection>();
		}
		
		while(pool.size()<DBConnPool.POOL_MIN_SIZE){
			pool.add(DBManager.createConn());
			System.out.println("��ʼ���أ�������������"+pool.size());
		}
	}
	
	
	/**
	 * �����ӳ���ȡ��һ������
	 * @return
	 */
	public synchronized Connection getConnection() {
		int last_index = pool.size()-1;
		Connection conn = pool.get(last_index);
		pool.remove(last_index);
		
		return conn;
	}
	
	/**
	 * �����ӷŻس���
	 * @param conn
	 */
	public synchronized void close(Connection conn){
		
		if(pool.size()>=POOL_MAX_SIZE){
			try {
				if(conn!=null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}else {
			pool.add(conn);
		}
	}
	
	
	public DBConnPool() {
		initPool();
	}
	
}
