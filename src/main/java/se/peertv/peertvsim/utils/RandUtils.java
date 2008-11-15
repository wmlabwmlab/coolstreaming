/*
 * Created on Jul 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sameh
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package se.peertv.peertvsim.utils;

import java.util.Random;

import se.peertv.peertvsim.conf.Conf;


public class RandUtils {
	private Random rand;
	//private cern.jet.random.Uniform rand;
	private static RandUtils singeltonRandUtils = null;
	public static RandUtils getInstance() {
		if (singeltonRandUtils == null)
			singeltonRandUtils = new RandUtils();
		return singeltonRandUtils;
	}
	boolean firstInt = true;
	int firstIntVal = -1;
	
	public RandUtils() {		
		reset();
	}
	
	public void reset() {
		//System.out.println("rand reset; seed = " + Conf.RANDOM_SEED);
		rand = new Random(Conf.RANDOM_SEED);
		rand.setSeed(Conf.RANDOM_SEED);
		if(firstInt)
		{
			firstIntVal = rand.nextInt();
			firstInt = false;
		} 
//		else
//			try {
//				A.ssert(firstIntVal == rand.nextInt());
//			} catch (Exception e) {				
//				e.printStackTrace();
//				System.exit(0);
//			}
	}
	
	public int nextInt(int n) {
		return (rand.nextInt(n));
	}

	public double nextDouble() {
		return (rand.nextDouble());
	}
	
	public double nextExponential(double mean)
    {		
        return - mean * StrictMath.log( nextDouble() );                
    }

//	public int RandomExcludingKeys(HashMap D, int N) {
//		while (true) {
//			int x = rand.nextInt(N);
//			if (!D.containsKey(new Integer(x))) {
//				return (x);
//			}
//		}
//	}
//
//	public Set randomSubset(int subsetSize, Set set) {
//		int setSize = set.size();
//		RandomSample rs = new RandomSample(setSize,subsetSize , rand);
//		return rs.nextSampleAsAset();		
//	}
//	
//	
//	
//	public long[] P_SortedUniqueTakenFrom_N(int N, int P) {
//		RandomSample rs = new RandomSample(N, P, rand);
//		long[] Ids = rs.nextSample();
//		for (int i = 0; i < Ids.length; i++)
//			Ids[i] = Ids[i] - 1;
//		Arrays.sort(Ids);
//		return (Ids);
//	}
//
//	public int Pick(HashMap Nodes) {
//		Object Ids[] = Nodes.keySet().toArray();
//		int Len = Ids.length;
//		int pickedIndex = rand.nextInt(Len);
//		return (((Integer) Ids[pickedIndex]).intValue());
//	}
//
//	public int[] RandomPermuationOfNodes(HashMap Nodes) {
//		Object Ids[] = Nodes.keySet().toArray();
//		int a[] = new int[Ids.length];
//
//		for (int i = 0; i < Ids.length; i++)
//			a[i] = ((Integer) Ids[i]).intValue();
//
//		int[] b = (int[]) a.clone();
//
//		for (int k = b.length - 1; k > 0; k--) {
//			int w = (int) Math.floor(rand.nextDouble() * (k + 1));
//			int temp = b[w];
//			b[w] = b[k];
//			b[k] = temp;
//		}
//		return (b);
//	}
//
//	public double floatfrom0to1() {
//		return (rand.nextDouble());
//	}

	public static void main(String[] args) throws Exception {
//		HashMap Nodes = new HashMap();
//
//		for (int i = 0; i < 1000; i++) {
//			Nodes.put(new Integer(i), "goo");
//		}
//
//		RandUtils x = new RandUtils();
//
//		for (int k = 0; k < 10; k++)
//			Utils.printArray(x.RandomPermuationOfNodes(Nodes));
		
//		HashSet<Integer> set = new HashSet<Integer>();
//		for (int k = 0; k < 1000; k++)
//			set.add(k);
//		
//		RandUtils r  = RandUtils.getInstance();
//		Set smallSet = r.randomSubset(5, set);		
//		P.rint(smallSet);
//		
//		for(int i = 0; i < Integer.MAX_VALUE; i ++)
//			 P.rint(RandUtils.getInstance().nextExponential(1000));
		
		for(int k = 0; k < 10000; k++)
		{
			RandUtils.getInstance().reset();
			int i = RandUtils.getInstance().nextInt(300)+1;
			for(int s = 0; s < 100000; s ++)
				RandUtils.getInstance().nextDouble();
			RandUtils.getInstance().reset();
			int j = RandUtils.getInstance().nextInt(300)+1;
			P.rint(i + " == " + j);
			A.ssert(i == j);			
		}
	}

}
