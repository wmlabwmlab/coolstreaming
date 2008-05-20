/**
 * =============================================== 
 *  File     : $Id: P.java,v 1.2 2007/06/03 10:35:16 elbeltagy Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.2 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: elbeltagy $
 *  Last updated:    $Date: 2007/06/03 10:35:16 $
 *===============================================
 */
package se.peertv.peertvsim.utils;

import java.util.Set;

import se.peertv.peertvsim.conf.Conf;


/**
 * @author sameh
 *
 */
public class P {
	
	public static void  rint(Object ...strings ){
		/*
		if(Conf.STDOT_TRACE){
			for (Object s : strings)
				System.out.print(s+"");
			System.out.print("\n");
		}
		*/
	}
	
	public static void  rint(Set<String>  set){
		/*
		//for (Object o: set )
		if(Conf.STDOT_TRACE){	
			System.out.println(set);
		}
		*/
	}
	
	public static void rintArray(double[] a) {
		/*
		if(Conf.STDOT_TRACE){
			for (int k = 0; k < a.length; k++)
				System.out.print("  " + a[k]);
			System.out.println();
		}
		*/
	}
	
	public static void rintArray(int[] a) {
		/*
		if(Conf.STDOT_TRACE){
			for (int k = 0; k < a.length; k++)
				System.out.print("  " + a[k]);
			System.out.println();
		}
		*/
	}
	
}
