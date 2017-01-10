package main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import main.Controller.Choice;

/**
 * It stores a bunch of settings for the downloading.
 * 
 * @author Zhen Chen
 *
 */

public class Setting {
	String root;
	String folder;
	String preferredFormat;
	boolean separateFolder;
	boolean forceWrite;

	public Setting() throws IOException {
		String message;
		Map<String, Choice> options;
		Choice choice;

		// root directory
		System.out.println("Please enter the root directory:");
		root = Helper.input.readLine();
		if (root.length() == 0 || root.charAt(root.length() - 1) != '/') {
			root += "/";
		}

		// all in one folder
		message = "";
		message += "Do you want to put each target into separate folders or into one single folder?%n";
		message += "1. Separate folders for each target%n";
		message += "2. All targets into one single folder%n";
		options = new HashMap<String, Choice>();
		options.put("1", Choice.MULTIPLE);
		options.put("2", Choice.SINGLE);
		choice = Helper.getUserChoice(message, options);
		if (choice == Choice.MULTIPLE) {
			separateFolder = true;
		} else {
			separateFolder = false;
			// folder name
			System.out.println("Please enter the folder name:");
			folder = Helper.input.readLine();
		}

		// preferred quality
		System.out.println("Please enter the preferred quality, hit enter to use the highest quality by default:");
		preferredFormat = Helper.input.readLine();

		// overwrite existing file
		message = "";
		message += "Do you want to force overwriting any existing file? (y/n)%n";
		options = new HashMap<String, Choice>();
		options.put("y", Choice.YES);
		options.put("n", Choice.NO);
		choice = Helper.getUserChoice(message, options);
		if (choice == Choice.YES) {
			forceWrite = true;
		} else {
			forceWrite = false;
		}
	}

	public Setting(String json) {
		JsonObject jo = Helper.jsonParser.parse(json).getAsJsonObject();
		root = jo.get("root").getAsString();
		separateFolder = jo.get("separateFolder").getAsBoolean();
		if (!separateFolder) {
			folder = jo.get("folder").getAsString();
		}
		preferredFormat = jo.get("preferredFormat").getAsString();
		forceWrite = jo.get("forceWrite").getAsBoolean();
	}

	@Override
	public String toString() {
		String format = "";
		format += "Root directory: %1$s%n";
		if (separateFolder) {
			format += "Separate folders for each target%n";
		} else {
			format += "All targets into one single folder: %2$s%n";
		}
		format += "Preferred quality: %3$s%n";
		format += "Force overwriting any existing file: %4$b%n";
		return String.format(format, root, folder, preferredFormat, forceWrite);
	}

}
