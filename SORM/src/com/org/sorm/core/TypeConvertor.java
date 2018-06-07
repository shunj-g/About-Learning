package com.org.sorm.core;

/**
 * 负责java数据类型和数据库数据类型的互相转�?
 * @author Lenovo
 * @version 1.0
 */
public interface TypeConvertor {
	
	/**
	 * 将数据库数据类型转化成java的数据类�?
	 * @param columnType 数据库字段的数据类型
	 * @return java的数据类�?
	 */
	public String databaseType2JavaType(String columnType);
	
	/**
	 * 将java数据类型转化成数据库数据类型
	 * @param javaDataType java数据类型
	 * @return 数据库类�?
	 */
	public String javaType2DatabaseType(String javaDataType);
	
}
