package org.student.downloader;

import java.io.*;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A utilities which to use for support operations
 *
 * @author jogg
 */

public class Utility {
	/**
	 * The method parse the human readable format of bytes quantity and
	 * return <code>long<code/> value
	 * @param numberString string which contains string representstion
	 *                     of figure
	 * @return {@code long} value of figure
	 */
	public static long getLongNumber(String numberString) {
		long returnValue = -1;

		Pattern patt = Pattern.compile("([\\d.]+)([GMK])", Pattern.CASE_INSENSITIVE);
		Matcher matcher = patt.matcher(numberString);

		Map<String, Integer> powerMap = new HashMap<>();
		powerMap.put("G", 3);
		powerMap.put("m", 2);
		powerMap.put("k", 1);

		if (matcher.find()) {
			String number = matcher.group(1);
			int pow = powerMap.get(matcher.group(2).toUpperCase());
			BigDecimal bytes = new BigDecimal(number);
			bytes = bytes.multiply(BigDecimal.valueOf(1024).pow(pow));
			returnValue = bytes.longValue();
		}

		return returnValue;
	}

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
