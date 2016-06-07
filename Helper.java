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
	 * Return an array of executable programs in the given directory using the
	 * executableFileFilter. If the given path is already an executable program,
	 * return an array containing the path of this program only, otherwise
	 * return an empty array.
	 * 
	 * @param path
	 * @return an array of executable files in the given directory, or an array
	 *         containing the given path only if it is an executable program
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

	/**
	 * This method calls getExecutable(String path) to get paths of all
	 * executable programs in the given path and return the first one.
	 * 
	 * @param path
	 *            a path to a directory containing executable programs or a path
	 *            of an executable program
	 * @return the path of first executable program found in the given path
	 * @throws NoExecutableFileFoundException
	 *             if the given path does not contain any executable programs
	 *             and the given path is not a valid executable program either
	 */
	public static final String getFirstExecutablePath(String path) throws NoExecutableFileFoundException {
		File firstExecutable = getExecutable(path)[0];
		if (firstExecutable == null) {
			throw new NoExecutableFileFoundException();
		}
		return firstExecutable.getAbsolutePath();
	}

}
