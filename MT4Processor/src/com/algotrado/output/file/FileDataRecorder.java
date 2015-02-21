package com.algotrado.output.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.algotrado.extract.data.AssetType;
import com.algotrado.extract.data.DataEventType;
import com.algotrado.extract.data.DataSource;
import com.algotrado.extract.data.IDataExtractorObserver;
import com.algotrado.extract.data.IDataExtractorSubject;
import com.algotrado.extract.data.RegisterDataExtractor;
import com.algotrado.extract.data.SubjectState;

public class FileDataRecorder implements IDataExtractorObserver {
	
	DataSource dataSource;
	AssetType assetType;
	DataEventType dataEventType;
	List<Float> parameters;
	String saveFilePath;
	IDataExtractorSubject dataExtractorSubject;
	boolean appendFileMode;
	IGUIMessageOutputer errorOutputter;

	public FileDataRecorder(DataSource dataSource, AssetType assetType,
			DataEventType dataEventType, List<Float> parameters, 
			String saveFilePath, IGUIMessageOutputer errorOutputter) {
		super();
		this.dataSource = dataSource;
		this.assetType = assetType;
		this.dataEventType = dataEventType;
		this.parameters = parameters;
		this.saveFilePath = saveFilePath;
		this.appendFileMode = false;
		this.errorOutputter = errorOutputter;
		
		RegisterDataExtractor.register(assetType, dataEventType, parameters, this);
	}

	@Override
	public void notifyObserver(DataEventType dataEventType,
			List<Float> parameters) {
		String directoryPathStr = saveFilePath.substring(0, saveFilePath.lastIndexOf(File.separator));
		Path directoryPath = Paths.get(directoryPathStr);
		if (!Files.exists(directoryPath)) {
			this.errorOutputter.setErrorMessage("Directory: " + directoryPathStr, true);
			return;
		}
		FileWriter destinationFile = null;
		try {
			destinationFile = new FileWriter(saveFilePath, this.appendFileMode);
		} catch (IOException e) {
			this.errorOutputter.setErrorMessage("File: " + saveFilePath + " could not be created for some reason. " + e.getMessage(), true);
			return;
		}
		if (!this.appendFileMode) {
			try {
				destinationFile.append(this.dataExtractorSubject.getDataHeaders());
				destinationFile.append("\n");
			} catch (IOException e1) {
				this.errorOutputter.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
				try {
					destinationFile.flush();
					destinationFile.close();
				} catch (IOException e) {
					this.errorOutputter.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
					return;
				}
				return;
			}
			this.appendFileMode = true;
		}
		
		try {
			destinationFile.append(this.dataExtractorSubject.toString());
			destinationFile.append("\n");
		} catch (IOException e1) {
			this.errorOutputter.setErrorMessage("File: " + saveFilePath + " could not be changed for some reason. " + e1.getMessage(), true);
		}
		
		try {
			destinationFile.flush();
			destinationFile.close();
		} catch (IOException e) {
			this.errorOutputter.setErrorMessage("File: " + saveFilePath + " could not be closed for some reason. " + e.getMessage(), false);
			return;
		}
		
		if (this.dataExtractorSubject.getSubjectState() == SubjectState.END_OF_LIFE) {
			this.dataExtractorSubject.unregisterObserver(this);
		}
	}

	@Override
	public void setSubject(IDataExtractorSubject dataExtractorSubject) {
		this.dataExtractorSubject = dataExtractorSubject;
	}

	@Override
	public void removeSubject(IDataExtractorSubject dataExtractorSubject) {
		setSubject(null);
	}

}
