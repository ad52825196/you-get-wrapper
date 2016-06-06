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
	private static final String LOCATION = "E:/软件/You-Get/";
	private static final String CHARSET = "GBK";
	private String outputPath = "/";
	public Controller(String outputPath) {
		setOutputPath(outputPath);
	// getter and setter for outputPath
	public final String getOutputPath() {
		return outputPath;
	}

	public final void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}


	public static void main(String[] args) {
		try {
			YouGet.setExecutable(LOCATION);
			YouGet.setCharset(CHARSET);
		} catch (NoExecutableFileFoundException e) {
			e.printStackTrace();
		}
	}

}
