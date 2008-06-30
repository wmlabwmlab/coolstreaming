package simpipe.coolstreaming.visualization;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class MCacheOverPeers extends JPanel implements Visualization{
	
	ArrayList<TimeSlot> slots = new ArrayList<TimeSlot>();
	int index=0;
	JButton prev = new JButton("< Prev");
	JButton next = new JButton("Next >");
	ChartPanel chartPanel;
	boolean init=true;
	String title="Visualization";
	String xAxis=new String("Domain");
	String yAxis=new String("Range");
	
	
	public void set(String title,String xAxis, String yAxis){
		this.title=title;
		this.xAxis=xAxis;
		this.yAxis=yAxis;
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
	
	public void add(TimeSlot t){
		slots.add(t);
	}
	public void view(int time){
		
		TimeSlot slot= slots.get(time);
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i=0;i<slot.slots.size();i++){
			double[] members=slot.slots.get(i).members;
			for(int j=0;j<members.length;j++)
				if(members[j]!=0)
				dataset.addValue(1.0, "Peer "+members[j], ""+slot.slots.get(i).id);
		}
			
        JFreeChart chart = createChart(dataset,time);
        
        if(init){
            chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 400));
            
            CategoryPlot plot = chart.getCategoryPlot();
            BarRenderer  barRenderer = (BarRenderer)plot.getRenderer();
            barRenderer.setItemLabelsVisible(true);
            
            this.add(chartPanel);
            init=false;
         
           }
        else{
        	chartPanel.setChart(chart);
        }
        //setContentPane(chartPanel);
        
        repaint();
        
	}
	private JFreeChart createChart(final CategoryDataset dataset,int time) {

		int incTime=time+1;
        final JFreeChart chart = ChartFactory.createStackedBarChart(
            title+" at time = "+incTime,  // chart title
            xAxis,                  // domain axis label
            yAxis,                     // range axis label
            dataset,                     // data
            PlotOrientation.VERTICAL,    // the plot orientation
            true,                        // legend
            true,                        // tooltips
            true                        // urls
        );
        
        return chart;
        
    }
	
}
