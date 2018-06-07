package com.org.sorm.core;

import java.lang.reflect.Field;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.org.sorm.bean.ColumnInfo;
import com.org.sorm.bean.TableInfo;
import com.org.sorm.utils.JDBCUtils;
import com.org.sorm.utils.ReflectUtils;

/**
 * 璐熻矗鏌ヨ锛堝澶栨彁渚涙湇鍔＄殑鏍稿績绫伙級
 * @author Lenovo
 * @version 1.0
 */
@SuppressWarnings("all")//必须要这个接口，要用到clone方法
public abstract class Query implements Cloneable {

	/**
	 * 閲囩敤妯℃澘鏂规硶妯″紡灏咼DBC鎿嶄綔灏佽鎴愭ā鏉匡紝渚夸簬閲嶇�?
	 * @param sql sql璇�?
	 * @param params sql鐨勫弬鏁�?	 * @param clazz 璁板綍瑕佸皝瑁呭埌鐨刯ava绫�
	 * @param back CallBack鐨勫疄鐜扮被锛屽疄鐜板洖璋�
	 * @return 
	 */
public Object executeQueryTemplate(String sql,Object[] params,Class clazz,CallBack back){
	Connection conn = DBManager.getConn();
	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
		ps = conn.prepareStatement(sql);
		//缁檚ql璁惧�?
		JDBCUtils.handleParams(ps, params);
		System.out.println(ps);
		rs = ps.executeQuery();
		//在真正实现的时候就会重写callback类中doExecute
		//使用callback定制查询方式
		return  back.doExecute(conn, ps, rs);
		
	} catch (Exception e) {
		e.printStackTrace();
		return null;
	}finally{
		DBManager.close(ps, conn);
	}
}
	
	
	/**
	 * 鐩存帴鎵ц涓�釜DML璇�?
	 * @param sql sql璇�?
	 * @param params 鍙傛�?
	 * @return 鎵цsql璇彞鍚庡奖鍝嶈褰曠殑琛屾�?
	 */
	public int executeDML(String sql,Object[] params){
		Connection conn = DBManager.getConn();
		int count = 0; 
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			//缁檚ql璁惧�?
			JDBCUtils.handleParams(ps, params);
			System.out.println(ps);
			
			count  = ps.executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			DBManager.close(ps, conn);
		}
		
		return count;
	
	}
	
	/**
	 * 灏嗕竴涓璞�?瓨鍌ㄥ埌鏁版嵁搴撲腑
	 * 鎶婂璞′腑涓嶄负null鐨勫睘鎬у線鏁版嵁搴撲腑�?樺偍锛佸鏋滄暟瀛椾负null鍒欐�?.
	 * @param obj 瑕佸瓨鍌ㄧ殑瀵硅�?
	 */
	public void insert(Object obj){

		//obj-->琛ㄤ腑銆�?           insert into 琛ㄥ�? (id,uname,pwd) values (?,?,?)
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();   //瀛樺偍sql鐨勫弬鏁板璞�
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		StringBuilder sql  = new StringBuilder("insert into "+tableInfo.getTname()+" (");
		int countNotNullField = 0;   //璁＄畻涓嶄负null鐨勫睘鎬у�?
		Field[] fs = c.getDeclaredFields();
		for(Field f:fs){
			String fieldName = f.getName();
			Object fieldValue = ReflectUtils.invokeGet(fieldName, obj);
			
			if(fieldValue!=null){
				countNotNullField++;
				sql.append(fieldName+",");
				params.add(fieldValue);
			}
		}
		
		sql.setCharAt(sql.length()-1, ')');
		sql.append(" values (");
		for(int i=0;i<countNotNullField;i++){
			sql.append("?,");
		}
		sql.setCharAt(sql.length()-1, ')');
		
		executeDML(sql.toString(), params.toArray());
	
	}
	
	/**
	 * 鍒犻櫎clazz琛ㄧず绫诲搴旂殑琛ㄤ腑鐨勮褰�鎸囧畾涓婚敭鍊糹d鐨勮褰�?
	 * @param clazz 璺熻〃�?瑰簲鐨勭被鐨凜lass瀵硅�?
	 * @param id 涓婚敭鐨勫�
	 */
	public void delete(Class clazz,Object id){
		//Emp.class,2-->delete from emp where id=2
		//閫氳繃Class瀵硅薄鎵綯ableInfo
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		//鑾峰緱涓婚敭
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		
		String sql = "delete from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=? ";
		
		executeDML(sql, new Object[]{id});
	
	}
	/**
	 * 鍒犻櫎�?硅薄鍦ㄦ暟鎹簱涓搴旂殑璁板�?瀵硅薄鎵�湪鐨勭被�?瑰簲鍒拌〃锛屽璞＄殑涓婚敭鐨勫��?瑰簲鍒拌褰�?
	 * @param obj
	 */
	public void delete(Object obj){
		Class c = obj.getClass();
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();  //涓婚�?
		
		//閫氳繃鍙嶅皠鏈哄埗锛岃皟鐢ㄥ睘鎬у搴旂殑get鏂规硶鎴杝et鏂规�?
		Object priKeyValue = ReflectUtils.invokeGet(onlyPriKey.getName(), obj);

		delete(c, priKeyValue);
	
	}
	
	/**
	 * 鏇存柊�?硅薄瀵瑰簲鐨勮褰曪紝骞朵笖鍙洿鏂版寚瀹氱殑�?楁鐨勫�?
	 * @param obj 鎵�鏇存柊鐨勫璞�?	 * @param fieldNames 鏇存柊鐨勫睘鎬у垪琛�
	 * @return 鎵цsql璇彞鍚庡奖鍝嶈褰曠殑琛屾�?
	 */
	public int update(Object obj,String[] fieldNames){

		//obj{"uanme","pwd"}-->update 琛ㄥ�? set uname=?,pwd=? where id=?
		Class c = obj.getClass();
		List<Object> params = new ArrayList<Object>();   //瀛樺偍sql鐨勫弬鏁板璞�
		TableInfo tableInfo = TableContext.poClassTableMap.get(c);
		/**
		 * TODO StringBuilder sql  = new StringBuilder("update "+tableInfo.getTname()+" (");
		 * 这个语句有问题
		 */
		StringBuilder sql  = new StringBuilder("update "+tableInfo.getTname()+" (");
		ColumnInfo  priKey = tableInfo.getOnlyPriKey();   //鑾峰緱鍞竴鐨勪富閿�?		StringBuilder sql  = new StringBuilder("update "+tableInfo.getTname()+" set ");
		
		for(String fname:fieldNames){
			Object fvalue = ReflectUtils.invokeGet(fname,obj);
			params.add(fvalue);
			sql.append(fname+"=?,");
		}
		sql.setCharAt(sql.length()-1, ' ');
		sql.append(" where ");
		sql.append(priKey.getName()+"=? ");
		
		params.add(ReflectUtils.invokeGet(priKey.getName(), obj));    //涓婚敭鐨勫�
		
		return executeDML(sql.toString(), params.toArray()); 
	
	}
	
	/**
	 * 鏌ヨ杩斿洖澶氳璁板綍锛屽苟灏嗘瘡琛岃褰曞皝瑁呭埌clazz鎸囧畾鐨勭被鐨勫璞′腑
	 * @param sql 鏌ヨ璇彞
	 * @param clazz 灏佽鏁版嵁鐨刯avabean绫荤殑Class瀵硅�?
	 * @param params sql鐨勫弬鏁�?	 * @return 鏌ヨ鍒扮殑缁撴�?
	 */
	public List queryRows(final String sql,final Class clazz,final Object[] params){
		
		return (List)executeQueryTemplate(sql, params, clazz, new CallBack() {//内部实现
			
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				List list = null;
				try {
					ResultSetMetaData metaData = rs.getMetaData();
					//澶氳�?
					while(rs.next()){
						if(list==null){
							list = new ArrayList();
						}
						
						Object rowObj = clazz.newInstance();   //璋冪敤javabean鐨勬棤鍙傛�?閫犲�?
						
						//澶氬�?      select username ,pwd,age from user where id>? and age>18
						for(int i=0;i<metaData.getColumnCount();i++){
							String columnName = metaData.getColumnLabel(i+1);  //username
							Object columnValue = rs.getObject(i+1);
							
							//璋冪敤rowObj瀵硅薄鐨剆etUsername(String uname)鏂规硶锛屽皢columnValue鐨勫�璁剧疆杩涘�?
							ReflectUtils.invokeSet(rowObj, columnName, columnValue);
						}
						
						list.add(rowObj);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				return list;
			}
		});
	
	
	}
	
	
	
	
	/**
	 * 鏌ヨ杩斿洖涓�璁板綍锛屽苟灏嗚璁板綍灏佽鍒癱lazz鎸囧畾鐨勭被鐨勫璞′腑
	 * @param sql 鏌ヨ璇彞
	 * @param clazz 灏佽鏁版嵁鐨刯avabean绫荤殑Class瀵硅�?
	 * @param params sql鐨勫弬鏁�?	 * @return 鏌ヨ鍒扮殑缁撴�?
	 */
	public Object queryUniqueRow(String sql,Class clazz,Object[] params){
		List list = queryRows(sql, clazz, params);
		return (list!=null&&list.size()>0)?list.get(0):null;
	}
	
	/**
	 * 鏍规嵁涓婚敭鐨勫�鐩存帴鏌ユ壘�?瑰簲鐨勫璞�?	 
	 * @param clazz 
	 * @param id
	 * @return
	 */
	public Object queryById(Class clazz,Object id){
		//select * from emp where id=?   //delete from emp where id=?
		TableInfo tableInfo = TableContext.poClassTableMap.get(clazz);
		//鑾峰緱涓婚敭
		ColumnInfo onlyPriKey = tableInfo.getOnlyPriKey();
		String sql = "select * from "+tableInfo.getTname()+" where "+onlyPriKey.getName()+"=? ";
		return queryUniqueRow(sql, clazz, new Object[]{id});
	}
	
	
	/**
	 * 鏌ヨ杩斿洖涓�釜鍊�涓�涓��?锛屽苟灏嗚鍊艰繑鍥�?	 * @param sql 鏌ヨ璇彞
	 * @param params sql鐨勫弬鏁�?	 * @return 鏌ヨ鍒扮殑缁撴�?
	 */
	public Object queryValue(String sql,Object[] params){
		return executeQueryTemplate(sql, params, null, new CallBack() {
			@Override
			public Object doExecute(Connection conn, PreparedStatement ps, ResultSet rs) {
				Object value = null;
				try {
					while(rs.next()){
						value = rs.getObject(1);//获取当前行的指定列的值。
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				return value;
			}
		});
	}
	
	/**
	 * 鏌ヨ杩斿洖涓�釜鏁板瓧(涓�涓�垪)锛屽苟灏嗚鍊艰繑鍥�?	 * @param sql 鏌ヨ璇彞
	 * @param params sql鐨勫弬鏁�?	 * @return 鏌ヨ鍒扮殑鏁板�?
	 */
	public Number queryNumber(String sql,Object[] params){
		return (Number)queryValue(sql, params);
	}
	
	/**
	 * 鍒嗛〉鏌ヨ
	 * @param pageNum 绗嚑椤垫暟鎹�
	 * @param size 姣忛〉鏄剧ず澶氬皯璁板綍
	 * @return
	 */
	public abstract Object queryPagenate(int pageNum,int size);
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
