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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PAverageOverTime extends JPanel implements Visualization{
	
	int index=0;
	ChartPanel chartPanel;
	boolean init=true;
	String title="Average Score Visualization over time";
	String xAxis=new String("Time");
	String yAxis=new String("Average Score");
	double data[];
	
	
	public void set(String title,String xAxis, String yAxis){
		this.title=title;
		this.xAxis=xAxis;
		this.yAxis=yAxis;
	}
	
	public void init(){
		
	}
	public void init(double[] data){
		this.data=data;
	}
	
	public void add(TimeSlot t){
		
	}
	public void view(int time){
		
		final XYSeries series = new XYSeries("Random Data");
        for(int i =0; i<data.length;i++)
        {
            series.add(i,data[i]);
        }
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
        	title,  // chart title
            xAxis,                  // domain axis label
            yAxis,  
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        if(init){
            chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new java.awt.Dimension(500, 400));
          
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
		return null;
        
    }
	
}
