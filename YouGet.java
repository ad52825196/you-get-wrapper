/**
 * Each instance of this class represents a You-Get process.
 * 
 * @author Zhen Chen
 *
 */

public class YouGet implements Downloader {
	private static String executable;
	private String outputPath;
	private String outputFilename;

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

	/**
	 * Set the default output path to the root directory.
	 */
	YouGet() {
		this.outputPath = "/";
	}

	YouGet(String outputPath) {
		this.outputPath = outputPath;
	}

	public void download() {
		// TODO
	}

}
