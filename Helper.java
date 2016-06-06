import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

import com.google.gson.JsonParser;

/**
 * This class contains all helper methods for the program.
 * 
 * @author Zhen Chen
 *
 */

public final class Helper {
	private static final ExecutableFileFilter executableFileFilter = new ExecutableFileFilter();
	public static final JsonParser jsonParser = new JsonParser();

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
	 * @return an array of executable files in the given directory, or an array
	 *         containing the given path only if it is an executable file
	 */
	public static final File[] getExecutable(String path) {
		File file = new File(path);
		File[] files = new File[1];
		if (file.isDirectory()) {
			return file.listFiles(executableFileFilter);
		} else if (executableFileFilter.accept(file)) {
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
