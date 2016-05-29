import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * This class contains all helper methods for the program.
 * 
 * @author Zhen Chen
 *
 */

public final class Helper {
	private static final ExecutableFileFilter EXECUTABLE_FILE_FILTER = new ExecutableFileFilter();

	private static final class ExecutableFileFilter implements FileFilter {
		private static final String EXECUTABLE_PATTERN = "[\\S]+(\\.(?i)(exe))$";
		private static final Pattern P = Pattern.compile(EXECUTABLE_PATTERN);

		public boolean accept(File pathname) {
			return P.matcher(pathname.getName()).matches();
		}
	}

	/**
	 * Return an array of executable files in the given directory using the
	 * EXECUTABLE_FILE_FILTER. If the given path is already an executable file,
	 * return an array containing this file only, otherwise return an empty
	 * array.
	 * 
	 * @param path
	 * @return
	 */
	public static final File[] getExecutable(String path) {
		File file = new File(path);
		File[] files = new File[1];
		if (file.isDirectory()) {
			return file.listFiles(EXECUTABLE_FILE_FILTER);
		} else if (EXECUTABLE_FILE_FILTER.accept(file)) {
			files[0] = file;
		}
		return files;
	}

	public static final String getFirstExecutablePath(String path) throws NoExecutableFileFoundException {
		File firstExecutable = getExecutable(path)[0];
		if (firstExecutable == null) {
			throw new NoExecutableFileFoundException();
		}
		return firstExecutable.getAbsolutePath();
	}

}
