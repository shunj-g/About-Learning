package com.org.sorm.utils;

/**
 * ��װ���ַ������õĲ���
 * @author Lenovo
 * @version 1.0
 */
public class StringUtils {
	
	/**
	 * ��Ŀ���ַ�������ĸ��Ϊ��д
	 * @param str Ŀ���ַ���
	 * @return ����ĸ��Ϊ��д���ַ���
	 */
	public static String firstChar2UpperCase(String str){
		//abcd-->Abcd
		//abcd-->ABCD-->Abcd
		return str.toUpperCase().substring(0, 1)+str.substring(1);
	}
	
}
