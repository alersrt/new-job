package org.student.downloader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utilities which to use for support operations
 * @author www.codejava.net
 * @author jogg
 */

public class Utility {
	private static final int BUFFER_SIZE = 4096;

	/**
	 * Return URL list from file
	 * @param fileName name of file which contains URL list
	 * @throws java.io.IOException
	 */
	public static Map<URL, String> getURLsList(String fileName) throws IOException {
		return Files
				.lines(Paths.get(fileName))
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

	/**
	 * Downloads a file from a URL
	 * @param fileURL HTTP URL of the file to be downloaded
	 * @param fileName name under which will be save the dowloaded file
	 * @param saveDir path of the directory to save the file
	 * @throws java.io.IOException
	 */
	public static void downloadFile(String fileURL, String fileName, String saveDir) throws IOException {
		URL url = new URL(fileURL);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		int responseCode = httpConn.getResponseCode();

		// always check HTTP response code first
		if (responseCode == HttpURLConnection.HTTP_OK) {
			String disposition = httpConn.getHeaderField("Content-Disposition");
			String contentType = httpConn.getContentType();
			int contentLength = httpConn.getContentLength();

			System.out.println("Content-Type = " + contentType);
			System.out.println("Content-Disposition = " + disposition);
			System.out.println("Content-Length = " + contentLength);
			System.out.println("fileName = " + fileName);

			// opens input stream from the HTTP connection
			InputStream inputStream = httpConn.getInputStream();
			String saveFilePath = saveDir + FileSystems.getDefault().getSeparator() + fileName;

			// opens an output stream to save into file
			FileOutputStream outputStream = new FileOutputStream(saveFilePath);

			int bytesRead = -1;
			byte[] buffer = new byte[BUFFER_SIZE];
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.close();
			inputStream.close();

			System.out.println("File downloaded");
		} else {
			System.out.println("No file to download. Server replied HTTP code: " + responseCode);
		}
		httpConn.disconnect();
	}
}
