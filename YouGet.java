import java.io.File;

/**
 * Each instance of this class represents a You-Get process.
 * 
 * @author Zhen Chen
 *
 */

public class YouGet implements Downloader {
	private static final String LOCATION = "E:/软件/You-Get/";
	private static String executable;
	private String outputPath;
	private String outputFilename;

	public static final void setExecutable() throws NoExecutableFileFoundException {
		try {
			executable = Helper.getFirstExecutablePath(LOCATION);
		} catch (NoExecutableFileFoundException e) {
			throw new NoExecutableFileFoundException("No YouGet program found in the given path: " + LOCATION, e);
		}
	}

	/**
	 * Set the default output path to the root.
	 */
	YouGet() {
		this.outputPath = "/";
	}

	YouGet(String outputPath) {
		this.outputPath = outputPath;
	}

	public static final String getLocation() {
		return LOCATION;
	}

	public static final String getExecutable() {
		return executable;
	}

	public void download() {
		// TODO
	}

}
