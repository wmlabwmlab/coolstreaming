/**
 * =============================================== 
*  File     : $Id: Query.java,v 1.1 2007/06/12 16:32:02 sameh Exp $
*  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
*  Version  :$Revision: 1.1 $
*  Tag	  : $Name:  $
*  Last edited by   : $Author: sameh $
*  Last updated:    $Date: 2007/06/12 16:32:02 $
*===============================================
 */
package se.peertv.peertvsim.data;

/**
 * @author sameh
 *
 */
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.*;
/**
 * This class encapsulates a subset of JDBC functions. It includes opening and
 closing the connection, execution of select and action queries, and transaction
 processing.
 
 */
public final class Query {
	public Connection con;
	private Statement stmt;	
/**
 * Constructor 
 */
public Query() { }
/**
 * Close the database connectivity.
 */
public void close() throws SQLException {
	try 
	{
		if (con != null)
		{
			if (!con.getAutoCommit())
				con.commit();		
			if (stmt!=null) stmt.close();
			con.close();
		}
		else if (stmt!=null) stmt.close();
	}
	catch (SQLException e)
	{
		throw e;
	}
}
/**
 * This method to execute Select queries.
 */
public ResultSet query(String strQuery) throws SQLException{
	ResultSet RS;
	//	System.out.println(strQuery);
	RS = stmt.executeQuery(strQuery);
	return RS;
}

/**
 * This method is to execute Insert,Delete and Update queries.
 * The method returns the number of rows that has changed; that is,
 * if no row changed, it returns 0
 */
public int update(String strQuery) throws SQLException {
	int iResult;
	//        System.out.println(strQuery);
	iResult = stmt.executeUpdate(strQuery);
	return iResult;
	}
/**
 * This method is to activate stored procedures that execute Insert,Delete and Update queries.
 * The method returns the number of rows that has changed; that is,
 * if no row changed, it returns 0
 */	
/*
public int executeCall(String proc,String par[]) throws SQLException
{
	int iResult;
	String sql="{call "+proc+"(";
	for (int i=0;i<par.length;i++)
	{		
		if (par.length-i==1)
			sql+=par[i];
		else sql+=par[i]+",";
		Log.log("Parameter number "+i+1+" set");
	}
	sql+=")}";
	Log.log("sql is: "+sql);
	CallableStatement cs = con.prepareCall(sql);	
	boolean b = cs.execute();
	if (b) return 1;
	else return 0; 
}
*/

public int call(String proc) throws SQLException
{
	int iResult;
	String sql="{call "+proc+"}";
	//	System.out.println("sql is: "+sql);
	CallableStatement cs = con.prepareCall(sql);	
	boolean b = cs.execute();
	if (b) return 1;
	else return 0; 
}
/**
 * Opens the database connectivity.
 */
public void open(String database) throws SQLException,ClassNotFoundException {
	Class.forName("com.mysql.jdbc.Driver");
	con = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database,"root","");
	//	System.out.println("connected");
	//con = DriverManager.getConnection(url);
	stmt = con.createStatement();
}

public static void init() throws ClassNotFoundException {

}

/**
 * begin a transaction so that multiple dependent queries can be
 executed while maintaining data integrity
 */
public void beginTransaction() throws SQLException {
	con.setAutoCommit(false);
}
/**
 * the transaction is commited so the effect of the queries become permanent.
 * normal mode is returned where AutoCommit=true
 */
public void endTransaction() throws SQLException {
	con.commit();
	con.setAutoCommit(true);
}
/**
 * the transaction is cancelled so that queries within the transaction has no effect.
 * normal mode is returned where AutoCommit=true
 */
public void cancelTransaction() throws SQLException {
	con.rollback();
	con.setAutoCommit(true);
}


}
