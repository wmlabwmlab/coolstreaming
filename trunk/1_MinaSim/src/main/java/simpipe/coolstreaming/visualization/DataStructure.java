package simpipe.coolstreaming.visualization;

public class DataStructure{
	int[] members;
	String id;
	public DataStructure(int[] members,String id){
		this.members=new int[members.length];
		for(int i=0;i<members.length;i++)
			this.members[i]=members[i];
		this.id=new String(id);
	}
}