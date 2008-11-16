/**
 * =============================================== 
 *  File     : $Id: BandwidthDistributionGenerator.java,v 1.3 2007/06/06 09:22:49 sameh Exp $
 *  Authors  : Sameh El-Ansary & Mohammed El-Beltagy (sameh,elbeltagy@sics.se)
 *  Version  :$Revision: 1.3 $
 *  Tag	  : $Name:  $
 *  Last edited by   : $Author: sameh $
 *  Last updated:    $Date: 2007/06/06 09:22:49 $
 *===============================================
 */

package se.peertv.peertvsim.network.tcp.bw.gen;

import se.peertv.peertvsim.utils.RandUtils;

/**
 * @author sameh
 *
 */
public class BandwidthDistributionGenerator {

	private String peerDistribution="";  
	private FractionOfDistribution fractions[];
	private double[] boundaries;                                 


	public BandwidthDistributionGenerator(String peerDistribution) throws Exception{
		this.peerDistribution = peerDistribution;
		init();
	}

	public class NodeBandwith{
		long up;
		long down;
		public NodeBandwith(long up, long down) {
			super();
			this.up = up;
			this.down = down;
		}
		public long getDown() {
			return down;
		}
		public long getUp() {
			return up;
		}
	}

	private class FractionOfDistribution {
		double percent;
		long up;
		long down;
		String s;
		int nGenerated=0;
		public FractionOfDistribution(String descr) throws Exception{
			s= descr;
			String parts[] = descr.split(":");
			if (parts.length!=2)
				throw new Exception("usage:   percentage:down-up");
			percent = Double.parseDouble(parts[0]);
			String partss[]  = parts[1].split("-");
			down = Long.parseLong(partss[0]);
			up = Long.parseLong(partss[1]);
		}
		public String toString() {
			return s +" "+nGenerated;
		}
	}

	void   init() throws Exception{	
		String[] parts  = peerDistribution.split("\\s* ");
		fractions = new FractionOfDistribution[parts.length];
		boundaries=new double[parts.length];
		double boundary =0.0;
		for (int i = 0; i < fractions.length; i++) {
			fractions[i] =  new FractionOfDistribution(parts[i]);
			boundaries[i] = boundary+fractions[i].percent;
			boundary = boundaries[i];
		}
		//		Make sure the distribution adds to one;
		if (boundaries[boundaries.length-1] != 1.0)
			throw new Exception("Node distribution must add to 1.0");	
	}
	
	public NodeBandwith getNewNodeBandwith(){
		int k=-1;
		double r = RandUtils.getInstance().nextDouble();
		for (int j = 0; j < boundaries.length; j++) {
			if (r < boundaries[j]){
				k =j;
				break;
			}
		}
		fractions[k].nGenerated++;
		//P.rint(fractions[k].up+"-"+fractions[k].down);
		return new NodeBandwith(fractions[k].up, fractions[k].down);
	}
}