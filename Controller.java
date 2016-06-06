import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * This is the controller for downloading videos from multiple URLs given by the
 * user and putting them into separate folders named after corresponding video
 * titles.
 * 
 * By default, it uses You-Get as the downloading tool.
 * 
 * @author Zhen Chen
 * 
 */

public class Controller implements Runnable {
	private static final int NUMBER_OF_THREADS = 1;
	private static final String LOCATION = "E:/软件/You-Get/";
	private static final String CHARSET = "GBK";
	private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	private static Thread[] threadPool = new Thread[NUMBER_OF_THREADS];
	private static int count = 0;
	private final int id = count;
	private String outputPath = "/";
	private List<String> targetList = new ArrayList<String>();

	// constructor
	public Controller() {
		count++;
	}

	public Controller(String outputPath) {
		this();
		setOutputPath(outputPath);
	}

	// getter for count
	public final int getCount() {
		return count;
	}

	// getter for id
	public final int getId() {
		return id;
	}

	// getter and setter for outputPath
	public final String getOutputPath() {
		return outputPath;
	}

	public final void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	// getter for targetList
	public final List<String> getTargetList() {
		return targetList;
	}

	private static synchronized void getInput(Controller c) throws IOException {
		String line;
		System.out.format("Please enter all target URLs for Process %d, one line for each:%n", c.getId());
		while (!(line = input.readLine()).equals("")) {
			c.getTargetList().add(line);
		}
		System.out.format("%d targets added to Process %d.%n%n", c.getTargetList().size(), c.getId());
	}

	public void run() {
		try {
			getInput(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			YouGet.setExecutable(LOCATION);
			YouGet.setCharset(CHARSET);
			for (int i = 0; i < NUMBER_OF_THREADS; i++) {
				threadPool[i] = new Thread(new Controller());
			}
			for (Thread t : threadPool) {
				t.start();
			}
			for (Thread t : threadPool) {
				t.join();
			}
		} catch (NoExecutableFileFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
