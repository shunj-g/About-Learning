package com.org.sorm.core;

/**
 * 閸掓稑缂換uery鐎电钖勯惃鍕紣閸樺倻琚?
 * @author Lenovo
 * @version 1.0
 */
public class QueryFactory {
	
	private static Query prototypeObj;  //閸樼喎鐎风€电钖?
	static {
		
		
		//鍔犺浇鐨勬椂鍊欐墽琛屼竴娆?
		try {
			Class c = Class.forName(DBManager.getConf().getQueryClass());  ////閸旂姾娴囬幐鍥х暰閻ㄥ墑uery缁拷
			prototypeObj = (Query) c.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}  
		
		//閸旂姾娴噋o閸栧懍绗呴棃銏″閺堝娈戠猾浼欑礉娓氬じ绨柌宥囨暏閿涘本褰佹妯绘櫏閻滃浄绱?
		TableContext.loadPOTables();
		
		
	}
	
	private QueryFactory(){  //缁変焦婀侀弸鍕拷閸ｏ拷
	}
	
	
	public static Query createQuery(){
		try {
			return (Query) prototypeObj.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
