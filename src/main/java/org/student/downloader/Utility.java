package org.student.downloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utilities which to use for support operations
 * @author jogg
 */

public class Utility {
	/**
	 * Return URL list from file
	 * @param filePath name of file which contains URL list
	 * @return {@code Map<URL,String>} which contains the URL of file which
	 * need download and the name under which the file will have been saved
	 * @throws java.io.IOException
	 */
	public static Map<URL, String> getURLsList(String filePath) throws IOException {
		return Files
				.lines(Paths.get(filePath))
				.collect(Collectors
						.toMap(s -> {
							URL url = null;
							try {
								url = new URL(s.split(" ")[0]);
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							return url;
						}, s -> s.split(" ")[1]));
	}
}
