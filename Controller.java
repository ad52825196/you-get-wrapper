import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
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

public class Controller {
	private static final int NUMBER_OF_THREADS = 1;
	private static final String LOCATION = "E:/软件/You-Get/";
	// Windows platform uses GBK in Chinese version
	private static final String CHARSET = "GBK";
	private static final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	private static Map<Thread, YouGet> threadPool = new HashMap<Thread, YouGet>(NUMBER_OF_THREADS);
	private static List<String> targetList = new ArrayList<String>();
	private static Map<String, String> targetMap = new HashMap<String, String>();
	private static int targetsNumber = 0;
	private static int activeThreadsNumber = 0;

	/**
	 * Get all target URLs from user.
	 * 
	 * An empty line indicates the end of input.
	 * 
	 * @throws IOException
	 */
	private static void getInput() throws IOException {
		String line;
		System.out.println("Please enter all target URLs, one line for each:");
		while (!(line = input.readLine()).equals("")) {
			targetList.add(line);
		}
		targetsNumber = targetList.size();
		System.out.format("%d targets added.%n", targetsNumber);
	}

	private static List<YouGet> clearThreadPool() throws InterruptedException {
		List<YouGet> ProcessList = new ArrayList<YouGet>();
		for (Thread t : threadPool.keySet()) {
			t.join();
			YouGet yg = threadPool.get(t);
			threadPool.remove(t);
			activeThreadsNumber--;
			if (yg.getState()) {
				ProcessList.add(yg);
			}
		}
		return ProcessList;
	}

	private static void addTitleFromThreadPool(List<YouGet> ProcessList) {
		for (YouGet yg : ProcessList) {
			targetMap.put(yg.getTarget().toString(), yg.getFilename());
		}
	}

	private static void getAllTitles()
			throws InterruptedException, NoExecutableSetException, IOException, ProcessErrorException {
		while (!targetList.isEmpty()) {
			if (activeThreadsNumber < NUMBER_OF_THREADS) {
				YouGet yg = new YouGet(targetList.get(0));
				targetList.remove(0);
				yg.setTask(YouGet.Task.INFO);
				Thread t = new Thread(yg);
				threadPool.put(t, yg);
				t.start();
				activeThreadsNumber++;
			} else {
				addTitleFromThreadPool(clearThreadPool());
			}
		}
		addTitleFromThreadPool(clearThreadPool());
	}

	public static void main(String[] args) {
		try {
			YouGet.setExecutable(LOCATION);
			YouGet.setCharset(CHARSET);
			getInput();
			getAllTitles();
			for (String target : targetMap.keySet()) {
				System.out.println(target + ": " + targetMap.get(target));
			}
		} catch (NoExecutableFileFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoExecutableSetException e) {
			e.printStackTrace();
		} catch (ProcessErrorException e) {
			e.printStackTrace();
		}
	}

}
