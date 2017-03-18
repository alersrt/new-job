package org.student.downloader;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.cli.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Downloader {
	private static volatile long timeStart = System.currentTimeMillis();

	/**
	 * The class provide opportunity for create single downloading thread
	 */
	private static class DownloadThread implements Callable<Long> {
		URL fileURL;
		private String fileName;
		private String saveDir;
		private long speedLimit;

		private static final int MAX_BUFFER_SIZE = 4096;

		/**
		 * The constructor create one exemplary of thread
		 * @param fileURL the url for downloading
		 * @param fileName the name under which will have been saved the file
		 * @param saveDir the name of downloads dir
		 * @param speedLimit the speed limit
		 */
		DownloadThread(URL fileURL,
					   String fileName,
					   String saveDir,
					   long speedLimit) {
			this.fileURL = fileURL;
			this.fileName = fileName;
			this.saveDir = saveDir;
			this.speedLimit = speedLimit;
		}

		/**
		 * The body of thread.
		 */
		@Override
		public Long call() throws Exception {
			long bytesQuantity = 0;
			try {
				HttpURLConnection httpConn = (HttpURLConnection) fileURL.openConnection();
				int responseCode = httpConn.getResponseCode();
				String saveFilePath = saveDir + FileSystems.getDefault().getSeparator() + fileName;

				if (responseCode == HttpURLConnection.HTTP_OK) {
					InputStream inputStream = httpConn.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(saveFilePath);

					int bytesRead = -1;
					byte[] buffer = new byte[MAX_BUFFER_SIZE];
					// Using prepared function from {@code com.google.common}
					RateLimiter limiter = RateLimiter.create(speedLimit);
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
						limiter.acquire(bytesRead);
						bytesQuantity += bytesRead;
					}

					outputStream.close();
					inputStream.close();

					System.out.println(String.format("%s downloading is done", fileName));
				} else {
					System.out.println("No file to download. Server replied HTTP code: " + responseCode);
				}
				httpConn.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return bytesQuantity;
		}
	}

	/**
	 * The main method
	 *
	 * @param args the array of arguments
	 */
	public static void main(String[] args) throws ParseException, IOException {
		args = new String[]{"-f=/home/jogg/links", "-l=2m", "-n=4", "-o=/home/jogg/123/"};
		// The options handler from {@code org.apache.commons.cli} library
		Options options = new Options();
		// Adding options in options list
		options.addOption("n", true, "quantity of theads which download the files");
		options.addOption("l", true, "common limit of download speed");
		options.addOption("f", true, "path to the file which contains links");
		options.addOption("o", true, "name of dir in which the files will be donwloaded");
		CommandLineParser parser = new DefaultParser();
		// Values parser
		CommandLine cmd = parser.parse(options, args);

		// Set params from cli options
		int threadsCount = Integer.parseInt(cmd.getOptionValue("n"));
		String filePath = cmd.getOptionValue("f");
		String saveDir = cmd.getOptionValue("o");
		long speedLimit = Utility.getLongNumber(cmd.getOptionValue("l"));

		// Execution threads
		List<Future<Long>> futureList = new ArrayList<>();
		ExecutorService executorService = Executors.newFixedThreadPool(threadsCount);
		Utility.getURLsList(filePath)
				.forEach((k, v) -> futureList.add(
						executorService.submit(
								new DownloadThread(k, v, saveDir, speedLimit)
						)
				));
		executorService.shutdown();

		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException ignored) {
		}

		System.out.println(String.format("All bytes: %s. All time: %s ms",
				futureList.stream().mapToLong(p -> {
					long t = 0;
					try {
						t = p.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					return t;
				}).sum(),
				System.currentTimeMillis() - timeStart)
		);
	}
}
