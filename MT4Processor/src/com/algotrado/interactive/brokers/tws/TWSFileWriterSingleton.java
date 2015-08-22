package com.algotrado.interactive.brokers.tws;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.algotrado.util.Setting;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ib.controller.Bar;

public class TWSFileWriterSingleton {

	private FileWriter destinationFile;
	private static String filePath;
	
	private static TWSFileWriterSingleton instance;
	private static ExecutorService executor;
	
	static {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
        .setNameFormat("HistoryWriter-%d")
        .setDaemon(true)
        .build();
		executor = Executors.newFixedThreadPool(1, threadFactory);
	}

	private TWSFileWriterSingleton(String filePath) {
		super();
		try {
			this.destinationFile = new FileWriter(filePath, false);
		} catch (IOException e) {
			Setting.errShow("Exception occoured while trying to open file for writing.");
			e.printStackTrace();
		}
	}
	
	public static TWSFileWriterSingleton getInstance() {
		if (filePath == null) {
			throw new RuntimeException("filePath = null!!!! Please set file path.");
		}
		if (instance == null) {
			instance = new TWSFileWriterSingleton(filePath);
		}
		return instance;
	}

	public static void setFilePath(String filePath) {
		TWSFileWriterSingleton.filePath = filePath;
	}
	
	public static void writeDataToFile(List<Bar> bars) {
		String dataForWriting = "";
		for (Bar bar : bars) {
			dataForWriting += bar.toString() + "\n";
		}
		try {
			getInstance().destinationFile.append(dataForWriting);
			getInstance().destinationFile.flush();
		} catch (IOException e) {
			Setting.outShow("Error trying to append data to file." + TWSFileWriterSingleton.class);
			e.printStackTrace();
		}
	}
	
	
	public static void closeFileWriter() {
		try {
			getInstance().destinationFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ExecutorService getExecutor() {
		return executor;
	}

	public static void writeHistoryBarsToFile(List<Bar> bars) {
		TWSFileWriterSingleton.getExecutor().submit(new TWSFileHistoryWriterRunnable(bars));
	}
	
}
