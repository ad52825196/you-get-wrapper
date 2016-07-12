import java.net.MalformedURLException;
import java.net.URL;

/**
 * Each instance of this class represents a target to download.
 * 
 * @author Zhen Chen
 *
 */

public class Target {
	private URL url;
	private String title;

	public Target(String url) throws MalformedURLException {
		setUrl(url);
	}

	public Target(URL url) {
		setUrl(url);
	}

	public final URL getUrl() {
		return url;
	}

	public final void setUrl(String url) throws MalformedURLException {
		this.url = new URL(url);
	}

	public final void setUrl(URL url) {
		this.url = url;
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	// two targets are considered equal if they have the same URL
	@Override
	public boolean equals(Object o) {
		if (o instanceof Target) {
			return url.toString().equals(((Target) o).getUrl().toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return url.toString().hashCode();
	}

}
