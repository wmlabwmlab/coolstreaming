/*
 * Created on Aug 11, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package se.peertv.peertvsim.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author sameh
 *
 */
public class DataBlob {	
	//private static final String SQL_TEXT = "VARCHAR";
	private static final String SQL_TEXT = "text";

	private String dbtable;
	
	HashMap<String,Object> data;
		
	public DataBlob(){
		data = new HashMap<String, Object>();
	}

	public DataBlob(HashMap<String, Object> data) {
		this.data = data;
	}

	public synchronized void inc(String item, double amount) throws Exception{
		//Retrieve the item from the data set
		Object o = data.get(item);
		//Determine the type of the object
		String jtype = o.getClass().getName();
		//If it is a double value, simply increase it
		if (jtype.compareTo("java.lang.Double")==0){
			double newValue = ((Double)o).doubleValue() + amount;
			data.put(item,new Double(newValue));
		}
		else
			throw(new Exception("Inconsistent increment."));
	}

	public synchronized void inc(String item, int amount)throws Exception{
		//Retrieve the item from the data set
		Object o = data.get(item);		
		//Determine the type of the object
		String jtype = o.getClass().getName();
		//If it is a double value, simply increase it
		if (jtype.compareTo("java.lang.Integer")==0){
			int newValue = ((Integer)data.get(item)).intValue() + amount;
			data.put(item,new Integer(newValue));
		}
		else
			throw(new Exception("Inconsistent increment."));		
	}
	
	public Object get(String item){
		if (data.containsKey(item) )
			return data.get(item);
		if (data.containsKey(item)) 
			return data.get(item);
		return data.get(item);
	}

	public int getInt(String item){
		return(((Integer)data.get(item)).intValue());
	}

	public double getDouble(String item){
		return(((Double)data.get(item)).doubleValue());
	}

	public String getString(String item){
		return((String)data.get(item));
	}
	
	public Object getObject(String item){
		return(data.get(item));
	}

	public  void set(String item, double value){
		data.put(item,value); 
	}
	public  void set(String item, int value){
		data.put(item,new Integer(value)); 
	}
	public  void set(String item, String value){
		data.put(item,value); 
	}
	public  void set(String item, Object value){
		data.put(item,value); 
	}
	
	public String toString(){
		String s = "";
		boolean first=true;
		Set<String> sortedKeys = new TreeSet<String>(data.keySet());
		for (String key : sortedKeys){
			String value;
			if (javaTypeToSqlType(data.get(key)) == SQL_TEXT)
				value =  "'"+data.get(key).toString()+"'";
			else
				value =  data.get(key).toString();
			if (first){
				s+= value;
				first = false;
			}
			else
			   s+= ","+value;	
		}
		return(s);
	}
	
//	public String sqlString(){
//		String s = "";
//		boolean first=true;
//		SortedSet sortedKeys = new TreeSet(data.keySet());
//		for (Iterator i =sortedKeys.iterator(); i.hasNext(); ){
//			String key   = (String) i.next();
//			String value =  data.get(key).toString();
//			if (first){
//				s+= value;
//				first = false;
//			}
//			else
//			   s+= " AND "+value;	
//		}
//		return(s);
//	}	
//	

	public String header() {
		String s = "";
		boolean first=true;
		Set<String> sortedKeys = new TreeSet<String>(data.keySet());
		for (String key : sortedKeys){
			if (first){
				s+= key;
				first = false;
			}
			else
			   s+= ","+key;	
		}
		return(s);
	}

	protected String javaTypeToSqlType(Object o){
		if(o == null)
			System.out.println("ERROR object = null");
		String jtype = o.getClass().getName(); 
		if (jtype=="java.lang.Double")
			return("double");
		if (jtype=="java.lang.Integer")
			return("int");
		if (jtype=="java.lang.String")
			return(SQL_TEXT);
		if (jtype=="java.lang.Long")
			return("int");
		if (jtype=="java.lang.Boolean")
			return("int");
		return(jtype);

	}
	

	public String sqlCreate(String tableName){
		dbtable = tableName;
		return "create table "+tableName+" ("+sqlHeader() +") TYPE=INNODB;";
	}

	public String sqlInsert(String tableName, boolean useFieldNames){
		if (useFieldNames)
			return "INSERT INTO "+tableName+" ("+header() +")   VALUES  ("+toString()+");";
		return"INSERT INTO "+tableName+" VALUES  ("+toString()+");";			
	}

	public String sqlInsert(boolean useFieldNames){
		if (useFieldNames)
			return "INSERT INTO "+this.dbtable+" ("+header() +")   VALUES  ("+toString()+");";
		return"INSERT INTO "+this.dbtable+" VALUES  ("+toString()+");";			
	}

	
	private String sqlHeader() {
		String s = "";
		boolean first=true;
		Set<String> sortedKeys = new TreeSet<String>(data.keySet());
		for (String key : sortedKeys){
			String type =  javaTypeToSqlType(data.get(key));
			if (first){
				s+= key+" "+type;
				first = false;
			}
			else
			   s+= ","+key+" "+type;	
		}
		return(s);
	}

	public String toRecord() {
		String s = "";
		Set<String> sortedKeys = new TreeSet<String>(data.keySet());
		for (Iterator<String> i =sortedKeys.iterator(); i.hasNext(); ){
			String key   = (String) i.next();
			String value =  data.get(key).toString();
			s+= " "+key+":"+value+" ";
		}	
		return(s);
	}

	public HashMap<String, Object> toMap(){
		return data;
	}

	public void merge(DataBlob b){
		data.putAll(b.toMap());
	}
	public void clear() {
		data.clear();
	}

	public Set<String> keySet() {
		return data.keySet();
	}
	
	public Object clone() throws CloneNotSupportedException {
		DataBlob db = new DataBlob();
		db.data = new HashMap<String, Object>(this.data);		
		return db;
	}

}