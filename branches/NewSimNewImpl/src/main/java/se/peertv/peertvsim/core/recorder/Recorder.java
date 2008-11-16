package se.peertv.peertvsim.core.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.Queue;

import se.peertv.peertvsim.core.Event;
import se.peertv.peertvsim.utils.A;

public class Recorder {

	private static Recorder filmSingleton;

	private Queue<Event> film;

	public Recorder() {
		reset();
	}

	public static Recorder getInstance() {
		if (filmSingleton == null) {
			filmSingleton = new Recorder();
		}
		return filmSingleton;
	}

	public void record(Event e) {
		film.add(e);
	}

	public void dump(String filePath) throws IOException {

		File dumpFile = new File(filePath);

		FileOutputStream f;

		f = new FileOutputStream(dumpFile);

		ObjectOutputStream s = new ObjectOutputStream(f);

		s.writeObject(film);

		s.flush();
		s.close();
		f.flush();
		f.close();

	}

	@SuppressWarnings("unchecked")
	public void read(String filePath) throws IOException, ClassNotFoundException {

		FileInputStream in = new FileInputStream(filePath);

		ObjectInputStream s = new ObjectInputStream(in);

		Object queue = s.readObject();

		if (queue instanceof Queue) {
			try {
				A.ssert(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			film = (Queue<Event>) queue;
		}
	}

	public static void clear() {
		filmSingleton = new Recorder();
	}

	public Queue<Event> getFilm() {
		return film;
	}

	private void reset() {
		film = new LinkedList<Event>();
	}
}
