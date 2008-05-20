package simpipe.coolstreaming;

public class BitField {

	int bits[] ;
	int time;
	public BitField(int size){
		bits  = new int[size];
	}
	void fill(int i){
		bits[i]=1;
	}
	void empty(int i){
		bits[i]=0;
	}
	int length(){
		return bits.length;
	}
	void setBit(int index,int value){
		bits[index]=value;
	}
	void setBits(String s,int time){
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
