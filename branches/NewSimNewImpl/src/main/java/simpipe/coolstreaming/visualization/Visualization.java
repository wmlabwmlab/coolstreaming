package simpipe.coolstreaming.visualization;

import javax.swing.JPanel;

public abstract class Visualization extends JPanel {

	public abstract void set(String title,String xAxis, String yAxis);
	public abstract void init();
	public abstract void init(double[] data);
	public abstract void add(TimeSlot t);
	public abstract void view(int time);
	public abstract String getTitle();
	public abstract boolean isDependent();
}
