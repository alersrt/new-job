package org.student.downloader;

import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;

public class Downloader {
	private static int threadsCount;
	private static String filePath;

	/**
	 * The class provide opportunity for create single downloading thread
	 */
	private class DownloadThread implements Runnable {
		private String fileURL;
		private String fileName;
		private String saveDir;
		private int speedLimit;

		private static final long TIME_SLOT = 1000;

		/**
		 * The constructor create one exemplary of thread
		 * @param fileURL the url for downloading
		 * @param fileName the name under which will have been saved the file
		 * @param saveDir the name of downloads dir
		 * @param speedLimit the speed limit
		 */
		public DownloadThread(String fileURL, String fileName, String saveDir, int speedLimit) {
			this.fileURL = fileURL;
			this.fileName = fileName;
			this.saveDir = saveDir;
			this.speedLimit = speedLimit;
		}

		/**
		 * The body of thread.
		 * Speed limit realized with Thread.sleep(TIME_SLOT - (nowTime - startTime))
		 * where startTime is time of downloading starting and nowTime is time at moment
		 * sleeping of current thread
		 */
		@Override
		public void run() {
			try {
				URL url = new URL(fileURL);
				HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
				int responseCode = httpConn.getResponseCode();
				String saveFilePath = saveDir + FileSystems.getDefault().getSeparator() + fileName;

				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = httpConn.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(saveFilePath);

					long timeSleep = 0;
					int bytesRead = -1;
					byte[] buffer = new byte[speedLimit];
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						timeSleep = System.currentTimeMillis();
						outputStream.write(buffer, 0, bytesRead);
						Thread.sleep(TIME_SLOT - (System.currentTimeMillis() - timeSleep));
					}

					outputStream.close();
					inputStream.close();

					System.out.println("File downloaded");
				} else {
					System.out.println("No file to download. Server replied HTTP code: " + responseCode);
				}
				httpConn.disconnect();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The main method
	 * @param args the array of arguments
	 */
	public static void main(String[] args) throws ParseException, IOException {
		args = new String[]{"--help"};
		 // The options handler from {@code org.apache.commons.cli} library
		Options options = new Options();
		// Adding options in options list
		options.addOption("n", true, "quantity of theads which download the files");
		options.addOption("l", true, "common limit of download speed");
		options.addOption("f", true, "path to the file which contains links");
		options.addOption("o", true, "name of dir in which the files will be donwloaded");
		options.addOption(Option.builder("h").longOpt("help").build());
		// Formatter for help output
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "downloader", options );
		CommandLineParser parser = new DefaultParser();
		// Values parser
		CommandLine cmd = parser.parse(options, args);
		// Set params from cli options
		threadsCount = Integer.parseInt(cmd.getOptionValue("n"));
		filePath = cmd.getOptionValue("f");

		//TODO: The function of downloading with many threads need will have realized
	}
}
