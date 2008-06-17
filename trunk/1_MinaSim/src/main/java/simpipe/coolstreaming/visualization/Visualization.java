package simpipe.coolstreaming.visualization;

public interface Visualization {

	public void set(String title,String xAxis, String yAxis);
	public void init();
	public void add(TimeSlot t);
	public void view(int time);
}
