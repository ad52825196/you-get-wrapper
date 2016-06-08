import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
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
	private static Set<Thread> threadPool = new HashSet<Thread>();
	private static Set<YouGet> processSet = new LinkedHashSet<YouGet>();
	private static Set<YouGet> failedProcessSet = new HashSet<YouGet>();

	private static enum Choice {
		INPUT, DOWNLOAD, TITLE, LOAD, SAVE, EXIT;
	}

	/**
	 * Get all target URLs from user.
	 * 
	 * An empty line indicates the end of input.
	 * 
	 * @throws IOException
	 */
	private static void getInput() throws IOException {
		int count = 0;
		String line;
		System.out.println("Please enter all target URLs, one line for each:");
		while (!(line = input.readLine()).equals("")) {
			YouGet yg = new YouGet(line);
			processSet.add(yg);
			count++;
		}
		System.out.printf("%d URLs newly added, %d URLs in total.%n", count, processSet.size());
	}

	private static void threadDistribute(YouGet.Task t) {
		for (YouGet yg : processSet) {
			if (threadPool.size() == MAX_NUMBER_OF_THREADS) {
				clearThreadPool();
			}
			yg.setTask(t);
			threadPool.add(yg);
			yg.start();
		}
		clearThreadPool();
		reportFailure();
	}

	private static void clearThreadPool() {
		for (Thread t : threadPool) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threadPool.remove(t);
			YouGet yg = (YouGet) t;
			if (!yg.isSuccess()) {
				failedProcessSet.add(yg);
			}
		}
	}

	private static void reportFailure() {
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

	private static void displayTitle() {
		threadDistribute(YouGet.Task.INFO);
		for (YouGet yg : processSet) {
			System.out.printf("%s    %s%n", yg.getTitle(), yg.getTarget().toString());
		}
	}

	private static void displayExit() {
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
				case SAVE:
					break;
				case LOAD:
					break;
				case EXIT:
					again = false;
					displayExit();
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
