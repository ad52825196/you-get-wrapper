import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
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

public class Controller {
	// at least 1 thread
	private static final int MAX_NUMBER_OF_THREADS = 1;
	// location of the downloading engine
	private static final String LOCATION = "E:/软件/You-Get/";
	// Windows platform uses GBK as charset in Chinese version
	private static final String CHARSET = "GBK";
	private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	private static Map<Thread, YouGet> threadPool = new HashMap<Thread, YouGet>();
	private static Set<YouGet> processSet = new LinkedHashSet<YouGet>();
	private static Set<YouGet> failedProcessSet = new HashSet<YouGet>();

	protected static enum Choice {
		INPUT, DOWNLOAD, TITLE, LOAD, SAVE, EXIT, YES, NO;
	}

	/**
	 * Get all target URLs from user.
	 * 
	 * An empty line indicates the end of input.
	 * 
	 * @throws IOException
	 */
	protected static void getInput() throws IOException {
		int count = 0;
		String line;
		System.out.println("Please enter all target URLs, one line for each:");
		while (!(line = input.readLine()).equals("")) {
			processSet.add(new YouGet(line));
			count++;
		}
		System.out.printf("%d URLs entered, %d URLs in working list now.%n", count, processSet.size());
	}

	/**
	 * It starts the task on each process in the processSet. It will call
	 * reportFailure() method to show information of failed processes at the
	 * end.
	 * 
	 * Only MAX_NUMBER_OF_THREADS number of threads are allowed to be running at
	 * the same time.
	 * 
	 * @param task
	 *            a task for each process to do
	 */
	protected static void startTaskAll(YouGet.Task task) {
		for (YouGet yg : processSet) {
			if (threadPool.size() == MAX_NUMBER_OF_THREADS) {
				clearThreadPool();
			}
			startTask(yg, task);
		}
		clearThreadPool();
		reportFailure();
	}

	/**
	 * It assigns the given task to the process and starts a new thread to do
	 * that.
	 * 
	 * @param yg
	 * @param task
	 *            a task for the process to do
	 */
	protected static void startTask(YouGet yg, YouGet.Task task) {
		yg.setTask(task);
		Thread t = new Thread(yg);
		threadPool.put(t, yg);
		t.start();
	}

	/**
	 * It waits for each thread in the threadPool to finish and then makes the
	 * threadPool clear.
	 * 
	 * It adds all failed processes to failedProcessSet.
	 */
	protected static void clearThreadPool() {
		YouGet yg;
		for (Thread t : threadPool.keySet()) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			yg = threadPool.get(t);
			if (!yg.isSuccess()) {
				failedProcessSet.add(yg);
			}
		}
		threadPool.clear();
	}

	protected static void reportFailure() {
		if (failedProcessSet.isEmpty()) {
			return;
		}
		for (YouGet yg : failedProcessSet) {
			System.out.printf("%s failed in task %s.%n", yg.getTarget().toString(), yg.getTask().toString());
		}
	}

	protected static Choice displayMenu() throws IOException {
		String message = "";
		message += "Menu:%n";
		message += "1. Input target URLs%n";
		message += "3. Show all titles%n";
		message += "6. Exit%n";

		Map<String, Choice> options = new HashMap<String, Choice>();
		options.put("1", Choice.INPUT);
		options.put("2", Choice.DOWNLOAD);
		options.put("3", Choice.TITLE);
		options.put("4", Choice.LOAD);
		options.put("5", Choice.SAVE);
		options.put("6", Choice.EXIT);

		return getUserChoice(message, options);
	}

	protected static void displayTitle() {
		startTaskAll(YouGet.Task.INFO);
		if (processSet.isEmpty()) {
			return;
		}
		System.out.println("Titles:");
		for (YouGet yg : processSet) {
			System.out.printf("%s    %s%n", yg.getTitle(), yg.getTarget().toString());
		}
	}

	protected static void displayExit() {
		System.out.println("Exit. Thank you!");
	}

	protected static String getUserString(String message) throws IOException {
		System.out.printf(message);
		return (input.readLine());
	}

	protected static String getUserChoice(String message, Set<String> options) throws IOException {
		String line;
		do {
			System.out.printf(message);
			line = input.readLine();
		} while (!options.contains(line));
		return line;
	}

	protected static <V> V getUserChoice(String message, Map<String, V> options) throws IOException {
		String line;
		do {
			System.out.printf(message);
			line = input.readLine();
		} while (!options.containsKey(line));
		return options.get(line);
	}

	public static void main(String[] args) {
		try {
			YouGet.setExecutable(LOCATION);
			YouGet.setCharset(CHARSET);

			boolean again = true;
			do {
				switch (displayMenu()) {
				case INPUT:
					getInput();
					break;
				case DOWNLOAD:
					break;
				case TITLE:
					displayTitle();
					break;
				case LOAD:
					break;
				case SAVE:
					break;
				case EXIT:
					again = false;
					displayExit();
					break;
				default:
					break;
				}
			} while (again);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
