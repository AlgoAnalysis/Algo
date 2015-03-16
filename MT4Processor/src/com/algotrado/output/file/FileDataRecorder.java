package com.algotrado.output.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.algotrado.data.event.DataEventType;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.SubjectState;

public class FileDataRecorder implements IDataExtractorObserver, Comparable<FileDataRecorder> {
	private final int chuckSizeToWrite = 1<<9;
	private String saveFilePath;
	private IDataExtractorSubject dataExtractorSubject;
	private boolean appendFileMode;
	private IGUIController guiController;
	private FileWriter destinationFile;
	private String bufferString;

	public FileDataRecorder(String saveFilePath, IGUIController errorOutputter) {
		super();
		this.saveFilePath = saveFilePath;
		this.appendFileMode = false;
		this.guiController = errorOutputter;
		this.destinationFile = null;
		this.bufferString = "";
		String directoryPathStr = saveFilePath.substring(0, saveFilePath.lastIndexOf(File.separator));
		Path directoryPath = Paths.get(directoryPathStr);
		if (!Files.exists(directoryPath)) {
			if(guiController != null) 
				this.guiController.setErrorMessage("Directory: " + directoryPathStr, true);
			return;
		}
		try {
			destinationFile = new FileWriter(saveFilePath, this.appendFileMode);
		} catch (IOException e) {
			if(guiController != null)
				this.guiController.setErrorMessage("File: " + saveFilePath + " could not be created for some reason. " + e.getMessage(), true);
			return;
		}
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Double> parameters) {
		
		bufferString += this.dataExtractorSubject.toString() + "\n";
		if(bufferString.length() >= chuckSizeToWrite)
		{
			try {
				destinationFile.append(bufferString);
			} catch (IOException e1) {
				if(guiController != null)
					this.guiController.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
				closeResourcesAndExit();
				return;
			}
			bufferString = "";
		}
		
		try {
			destinationFile.flush();
		} catch (IOException e) {
			if(guiController != null)
				this.guiController.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
			return;
		}
		
		if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE) {
			try {
				destinationFile.append(bufferString);
			} catch (IOException e1) {
				if(guiController != null)
					this.guiController.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
				closeResourcesAndExit();
				return;
			}
			this.dataExtractorSubject.unregisterObserver(this);
			if(guiController != null)
				this.guiController.resetGUI();
			closeResourcesAndExit();
			destinationFile = null;
		}
	}

	public void closeResourcesAndExit() {
		try {
//				destinationFile.flush();
			destinationFile.close();
		} catch (IOException e) {
			if(guiController != null)
				this.guiController.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
			return;
		}
		return;
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;
		bufferString += this.dataExtractorSubject.getDataHeaders() + "\n";
	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject  = null;
	}

//	@Override
//	public void run() {
//		RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters, this);
//	}

	@Override
	public int compareTo(FileDataRecorder o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		} /*else {
			if (o.saveFilePath.equals(this.saveFilePath) && o.assetType == this.assetType && o.dataEventType == this.dataEventType) {
				if (o.parameters != null && this.parameters != null) {
					if (o.parameters.size() != this.parameters.size()) {
						return this.parameters.size() - o.parameters.size();
					}
					Iterator<Double> fileDataRecorderIterator = this.parameters.iterator();
					for (Iterator<Double> oIterator = o.parameters.iterator(); oIterator.hasNext() && fileDataRecorderIterator.hasNext();) {
						Double oParam = oIterator.next();
						Double fileDataRecorderParam = fileDataRecorderIterator.next();
						if (oParam != fileDataRecorderParam) {
							return new Double(fileDataRecorderParam - oParam).intValue();
						}
						
					}
				} else if (o.parameters == null) {
					return 1;
				}
			}
		}*/
		return -1;
	}

}
