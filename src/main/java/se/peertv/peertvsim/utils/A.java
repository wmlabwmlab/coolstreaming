/**
 * =============================================== 
*  File     : $Id: A.java,v 1.2 2007/06/06 09:22:49 sameh Exp $
*  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
*  Version  :$Revision: 1.2 $
*  Tag	  : $Name:  $
*  Last edited by   : $Author: sameh $
*  Last updated:    $Date: 2007/06/06 09:22:49 $
*===============================================
 */
package se.peertv.peertvsim.utils;

/**
 * @author sameh
 *
 */
public class A {
	public static void ssert(boolean expr, String comment)  throws Exception{
		if (!expr){		
			System.out.println("Asseration Failed: "+ comment);
			throw new Exception("AssertionFailed"+comment);
			//System.exit(-1);
		}
	}
	public static void ssert(boolean expr) throws Exception{
		if (!expr){
			System.out.println("Asseration Failed");
			throw new Exception("AssertionFailed");
			//System.exit(-1);
		}
	}
}
