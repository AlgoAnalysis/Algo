package com.algotrado.output.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataEventType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;

public class FileDataRecorder implements IDataExtractorObserver, Runnable, Comparable<FileDataRecorder> {
	
	DataSource dataSource;
	AssetType assetType;
	DataEventType dataEventType;
	List<Float> parameters;
	String saveFilePath;
	IDataExtractorSubject dataExtractorSubject;
	boolean appendFileMode;
	IGUIController guiController;
	private FileWriter destinationFile;

	public FileDataRecorder(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Float> parameters, 
			String saveFilePath, IGUIController errorOutputter) {
		super();
		this.dataSource = dataSource;
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
		this.saveFilePath = saveFilePath;
		this.appendFileMode = false;
		this.guiController = errorOutputter;
		this.destinationFile = null;
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		if (destinationFile == null) {
			String directoryPathStr = saveFilePath.substring(0, saveFilePath.lastIndexOf(File.separator));
			Path directoryPath = Paths.get(directoryPathStr);
			if (!Files.exists(directoryPath)) {
				this.guiController.setErrorMessage("Directory: " + directoryPathStr, true);
				return;
			}
			try {
				destinationFile = new FileWriter(saveFilePath, this.appendFileMode);
			} catch (IOException e) {
				this.guiController.setErrorMessage("File: " + saveFilePath + " could not be created for some reason. " + e.getMessage(), true);
				return;
			}
		}
		
		if (!this.appendFileMode) {
			try {
				destinationFile.append(this.dataExtractorSubject.getDataHeaders());
				destinationFile.append("\n");
			} catch (IOException e1) {
				this.guiController.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
				closeResourcesAndExit();
				return;
			}
			this.appendFileMode = true;
		}
		
		try {
			destinationFile.append(this.dataExtractorSubject.toString());
			destinationFile.append("\n");
		} catch (IOException e1) {
			this.guiController.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
			closeResourcesAndExit();
			return;
		}
		
		try {
			destinationFile.flush();
		} catch (IOException e) {
			this.guiController.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
			return;
		}
		
		if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE) {
			this.dataExtractorSubject.unregisterObserver(this);
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
			this.guiController.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
			return;
		}
		return;
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;
	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		setSubject(null);
	}

	@Override
	public void run() {
		RegisterDataExtractor.register(assetType, dataEventType, parameters, this);
	}

	@Override
	public int compareTo(FileDataRecorder o) {
		if (o == null) {
			return 1;
		} else if (o == this) {
			return 0;
		} else {
			if (o.dataSource == this.dataSource && o.assetType == this.assetType && o.dataEventType == this.dataEventType) {
				if (o.parameters != null && this.parameters != null) {
					if (o.parameters.size() != this.parameters.size()) {
						return this.parameters.size() - o.parameters.size();
					}
					Iterator<Float> fileDataRecorderIterator = this.parameters.iterator();
					for (Iterator<Float> oIterator = o.parameters.iterator(); oIterator.hasNext() && fileDataRecorderIterator.hasNext();) {
						Float oParam = oIterator.next();
						Float fileDataRecorderParam = fileDataRecorderIterator.next();
						if (oParam != fileDataRecorderParam) {
							return new Float(fileDataRecorderParam - oParam).intValue();
						}
						
					}
				} else if (o.parameters == null) {
					return 1;
				}
			}
		}
		return -1;
	}

}
