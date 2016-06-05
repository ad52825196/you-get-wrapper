import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * Each instance of this class represents a You-Get process.
 * 
 * @author Zhen Chen
 *
 */

public class YouGet {
	private static String executable;
	private URL target;
	private String outputPath = "/";
	private String filename;

	public static final String getExecutable() {
		return executable;
	}

	/**
	 * If the given path is a directory, locate the first executable file in it.
	 * If the given path is a valid executable file, set it to the executable.
	 * 
	 * @param path
	 *            a path to a directory containing executable files or a path of
	 *            an executable file
	 * @throws NoExecutableFileFoundException
	 *             if the given path does not contain any executable files and
	 *             the given path is not a valid executable file either
	 */
	public static final void setExecutable(String path) throws NoExecutableFileFoundException {
		try {
			executable = Helper.getFirstExecutablePath(path);
		} catch (NoExecutableFileFoundException e) {
			throw new NoExecutableFileFoundException("No YouGet program found in the given path: " + path, e);
		}
	}

	YouGet(String target) throws NoExecutableSetException, IOException, InterruptedException, ProcessErrorException {
		this.setTarget(target);
		info();
	}

	YouGet(URL target) throws NoExecutableSetException, IOException, InterruptedException, ProcessErrorException {
		this.setTarget(target);
		info();
	}

	YouGet(String target, String outputPath)
			throws NoExecutableSetException, IOException, InterruptedException, ProcessErrorException {
		this(target);
		this.setOutputPath(outputPath);
	}

	YouGet(URL target, String outputPath)
			throws NoExecutableSetException, IOException, InterruptedException, ProcessErrorException {
		this(target);
		this.setOutputPath(outputPath);
	}

	public final URL getTarget() {
		return target;
	}

	public final void setTarget(String target) throws MalformedURLException {
		this.target = new URL(target);
	}

	public final void setTarget(URL target) {
		this.target = target;
	}

	public final String getOutputPath() {
		return outputPath;
	}

	public final void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public final String getFilename() {
		return filename;
	}


	private void info() throws NoExecutableSetException, IOException, InterruptedException, ProcessErrorException {
		if (executable == null) {
			throw new NoExecutableSetException();
		}
		Process p = new ProcessBuilder(executable, "--json", target.toString()).start();
		ProcessReader pr = new ProcessReader(p, "GBK");
		p.waitFor();
		if (p.exitValue() != 0) {
			throw new ProcessErrorException(pr.getError());
		}
	}

	public void download() {
		// TODO
	}

}
