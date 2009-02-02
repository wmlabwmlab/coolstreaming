package simpipe.coolstreaming.visualization;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;


/**
 *
 * @author Ahmed El-Rayes
 */

public class NetworkVisualization extends Visualization{
    
	
	Graph<Integer, String> g;
	ArrayList<TimeSlot> slots = new ArrayList<TimeSlot>();
	int index=0;
	JButton prev = new JButton("< Prev");
	JButton next = new JButton("Next >");
	VisualizationViewer<Integer,String> chartPanel;
	boolean init=true;
	String title="Network Visualization";
	
	
	public void set(String title,String xAxis, String yAxis){
		this.title=title;
		
	}
	public void init(){
		for(int i=0;i<10;i++)
			slots.add(new TimeSlot());
		this.setBounds(150,150,800,600);
		this.setVisible(true);
		//this.setResizable(false);
		prev.setEnabled(false);
		prev.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				next.setEnabled(true);
				index--;
				if(index==0)
					prev.setEnabled(false);
				view(index);
			}
			
		});
		next.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				prev.setEnabled(true);
				index++;
				if(index==slots.size()-1)
					next.setEnabled(false);
				
				view(index);
			}
			
		});
		this.add(prev,BorderLayout.WEST);
		this.add(next,BorderLayout.EAST);
	}
	public void init(double[] data){
		
	}
	public void add(TimeSlot t){
		slots.add(t);
	}
	
	public String getTitle(){
		
		return title;
	}
	public boolean isDependent(){
		
		return false;
	}
	
	public void view(int time){
		if(!init){
		this.remove(chartPanel);
		
		}
		init=false;
		g = new SparseMultigraph<Integer, String>();
		
		index=time;
		next.setEnabled(true);
		prev.setEnabled(true);
		if(time==0)
			prev.setEnabled(false);
		if(time>=slots.size()-1){
			time=slots.size()-1;
			index=slots.size()-1;
			next.setEnabled(false);
		}
		
		TimeSlot timeSlot= slots.get(time);
		for(int i=0;i<timeSlot.slots.size();i++){
			g.addVertex(Integer.parseInt(timeSlot.slots.get(i).id));
		}
		int count=0;
		for(int i=0;i<timeSlot.slots.size();i++){
			double[] members=timeSlot.slots.get(i).members;
			for(int j=0;j<members.length;j++)
				if(members[j]!=0){
					String edgeName=""+count;
					if(Integer.parseInt(timeSlot.slots.get(i).id)<(int)members[j])
					g.addEdge(edgeName,Integer.parseInt(timeSlot.slots.get(i).id),(int)members[j],EdgeType.UNDIRECTED);
					count++;
				}
		}
		
		Layout<Integer, String> layout = new SpringLayout<Integer, String>(g);
        layout.setSize(new Dimension(500,400));
        chartPanel = new VisualizationViewer<Integer,String>(layout);
        chartPanel.setPreferredSize(new Dimension(500,400));
        // Show vertex and edge labels
        chartPanel.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        //chartPanel.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
        // Create a graph mouse and add it to the visualization component
        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        chartPanel.setGraphMouse(gm); 
        this.add(chartPanel);
        chartPanel.repaint();
        repaint();
		
	}
	
	    
}
