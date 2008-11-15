package simpipe.coolstreaming;
import java.util.ArrayList;
/*
 * A data structure for holding the bits of the buffered map (Array of bits and some operations for handling it)
 */

public class BitField {

	public int bits[] ;
	public int time;
	public ArrayList<Integer> a1 =new ArrayList<Integer>();
	public ArrayList<Integer> a2 =new ArrayList<Integer>();
	
	public BitField(int size){
		bits  = new int[size];
		
	}
	public void fill(int i){
		bits[i]=1;
	}
	public void empty(int i){
		bits[i]=0;
	}
	public int length(){
		return bits.length;
	}
	public void setBit(int index,int value){
		bits[index]=value;
	}
	public void setBits(String s,int time){
		this.time=time;
		for(int i=0;i<s.length();i++)
		{
			if(s.charAt(i)=='0')
				bits[i]=0;
			else
				bits[i]=1;
		}
	}
	public String toString(){
		String str="";
		for(int i=0;i<bits.length;i++){
			str=str+bits[i];
		}
		return str;
	}
}
