import java.io.File;

/**
 * Each instance of this class represents a You-Get process.
 * 
 * @author Zhen Chen
 *
 */

public class YouGet implements Downloader {
	private static final String LOCATION = "E:/Èí¼þ/You-Get/";
	private static String executable;
	private String outputPath;
	private String outputFilename;

	public static final void setExecutable() throws NoExecutableFileFoundException {
		executable = Helper.getFirstExecutablePath(LOCATION);
	}

	YouGet() {
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
