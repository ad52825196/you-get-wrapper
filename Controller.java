import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
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
		ADD, DELETE, TITLE, DOWNLOAD, LOAD, SAVE, EXIT, YES, NO;
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
	 * Delete all target URLs specified by user.
	 * 
	 * An empty line indicates the end of input.
	 * 
	 * @throws IOException
	 */
	protected static void deleteTarget() throws IOException {
		if (processSet.isEmpty()) {
			return;
		}
		int count = 0;
		String line;
		System.out.println("Please enter ids of all target URLs to delete, one line for each:");
		Set<String> options = new HashSet<String>();
		options.add("");
		for (int i = 1; i <= processSet.size(); i++) {
			// for user, index starts from 1
			options.add(Integer.toString(i));
		}
		Set<Integer> toRemove = new HashSet<Integer>();
		while (!(line = getUserChoice(options)).equals("")) {
			// for program, index starts from 0
			toRemove.add(Integer.parseInt(line) - 1);
			count++;
		}
		removeProcess(toRemove);
		System.out.printf("%d URLs deleted, %d URLs in working list now.%n", count, processSet.size());
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
	 * @throws IOException
	 */
	protected static void startTaskAll(YouGet.Task task) throws IOException {
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

	/**
	 * For each failed process, it shows the target URL and the task on which it
	 * failed. Then it asks user whether to delete these failed processes from
	 * URL list.
	 * 
	 * @throws IOException
	 */
	protected static void reportFailure() throws IOException {
		if (failedProcessSet.isEmpty()) {
			return;
		}
		for (YouGet yg : failedProcessSet) {
			System.out.printf("%s failed in task %s.%n", yg.getTarget().toString(), yg.getTask().toString());
		}
		String message = "";
		message += "Do you want to remove these failed URLs from the working list? (y/n)%n";
		Map<String, Choice> options = new HashMap<String, Choice>();
		options.put("y", Choice.YES);
		options.put("n", Choice.NO);
		if (getUserChoice(message, options) == Choice.YES) {
			removeFailed();
		}
	}

	/**
	 * It removes all processes existing in the failedProcessSet from processSet
	 * and makes failedProcessSet clear.
	 */
	private static void removeFailed() {
		for (YouGet yg : failedProcessSet) {
			processSet.remove(yg);
		}
		failedProcessSet.clear();
	}

	/**
	 * It creates an empty set and put all processes that are not asked to
	 * remove into this new set. At the end, it replaces the old processSet with
	 * the new one.
	 * 
	 * Note: remove() method will not work in this case as the hashCode() of a
	 * process may have been changed after it calls info() method.
	 * 
	 * @param toRemove
	 *            indexes of all processes to be removed
	 */
	protected static void removeProcess(Set<Integer> toRemove) {
		YouGet yg;
		Set<YouGet> temp = new HashSet<YouGet>();
		Iterator<YouGet> it = processSet.iterator();
		for (int i = 0; it.hasNext(); i++) {
			yg = it.next();
			if (toRemove.contains(i)) {
				System.out.printf("%s has been removed.%n", yg.getTarget().toString());
			} else {
				temp.add(yg);
			}
		}
		processSet = temp;
	}

	protected static Choice displayMenu() throws IOException {
		String message = "";
		message += "Menu:%n";
		message += "1. Input target URLs%n";
		message += "2. Delete target URLs%n";
		message += "3. Show all titles%n";
		message += "0. Exit%n";

		Map<String, Choice> options = new HashMap<String, Choice>();
		options.put("1", Choice.ADD);
		options.put("2", Choice.DELETE);
		options.put("3", Choice.TITLE);
		options.put("4", Choice.DOWNLOAD);
		options.put("5", Choice.LOAD);
		options.put("6", Choice.SAVE);
		options.put("0", Choice.EXIT);
		options.put("e", Choice.EXIT);
		options.put("q", Choice.EXIT);

		return getUserChoice(message, options);
	}

	protected static void displayTarget() {
		if (processSet.isEmpty()) {
			return;
		}
		int id = 0;
		System.out.println("Targets:");
		for (YouGet yg : processSet) {
			System.out.printf("%d. %s%n", ++id, yg.getTarget().toString());
		}
	}

	protected static void displayTitle() throws IOException {
		if (processSet.isEmpty()) {
			return;
		}
		startTaskAll(YouGet.Task.INFO);
		int id = 0;
		System.out.println("Titles:");
		for (YouGet yg : processSet) {
			System.out.printf("%d. %s    %s%n", ++id, yg.getTitle(), yg.getTarget().toString());
		}
		String message = "";
		message += "Do you want to delete URLs from the working list? (y/n)%n";
		Map<String, Choice> options = new HashMap<String, Choice>();
		options.put("y", Choice.YES);
		options.put("n", Choice.NO);
		if (getUserChoice(message, options) == Choice.YES) {
			deleteTarget();
		}
	}

	protected static void displayExit() {
		System.out.println("Exit. Thank you!");
	}

	protected static String getUserChoice(Set<String> options) throws IOException {
		String line;
		do {
			line = input.readLine().toLowerCase();
		} while (!options.contains(line));
		return line;
	}

	protected static String getUserChoice(String message, Set<String> options) throws IOException {
		String line;
		do {
			System.out.printf(message);
			line = input.readLine().toLowerCase();
		} while (!options.contains(line));
		return line;
	}

	protected static <V> V getUserChoice(String message, Map<String, V> options) throws IOException {
		String line;
		do {
			System.out.printf(message);
			line = input.readLine().toLowerCase();
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
				case ADD:
					getInput();
					break;
				case DELETE:
					displayTarget();
					deleteTarget();
					break;
				case TITLE:
					displayTitle();
					break;
				case DOWNLOAD:
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
