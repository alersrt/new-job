package org.student.downloader;

import org.apache.commons.cli.*;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Created by jogg on 16.03.17.
 */
public class Downloader {
	private static String filePath;
	private static String dirName;
	private static int speedLimit;
	private static int threadsCount;

	public static void main(String[] args) throws ParseException, IOException {
		args = new String[]{"--help"};

		Options options = new Options();
		options.addOption("n", true, "quantity of theads which download the files");
		options.addOption("l", true, "common limit of download speed");
		options.addOption("f", true, "path to the file which contains links");
		options.addOption("o", true, "name of dir in which the files will be donwloaded");
		options.addOption(Option.builder("h").longOpt("help").build());

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp( "downloader", options );

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		setFilePath(cmd.getOptionValue("f"));
		setDirName(cmd.getOptionValue("o"));
		setSpeedLimit(Integer.parseInt(cmd.getOptionValue("l")));
		setThreadsCount(Integer.parseInt(cmd.getOptionValue("n")));

		Map<URL, String> urlList = Utility.getURLsList(cmd.getOptionValue("f"));
	}

	public static String getFilePath() {
		return filePath;
	}

	public static void setFilePath(String filePath) {
		Downloader.filePath = filePath;
	}

	public static String getDirName() {
		return dirName;
	}

	public static void setDirName(String dirName) {
		Downloader.dirName = dirName;
	}

	public static int getSpeedLimit() {
		return speedLimit;
	}

	public static void setSpeedLimit(int speedLimit) {
		Downloader.speedLimit = speedLimit;
	}

	public static int getThreadsCount() {
		return threadsCount;
	}

	public static void setThreadsCount(int threadsCount) {
		Downloader.threadsCount = threadsCount;
	}
}
