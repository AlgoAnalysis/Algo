package com.algotrado.extract.data;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;

public class DataExtractorGUI implements IGUIController {

	private JFrame frmDf;
	private JTextField tfdSaveFilePath;
	private boolean testRun = false;
	private JButton btnStart;
	private JComboBox cbxIntervalTime;
	private JComboBox cbxDataEvant;
	private JComboBox cbxAsset;
	private JLabel lblDataSource;
	private JComboBox cbxDataSource;
	private JButton btnSaveFile;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DataExtractorGUI window = new DataExtractorGUI();
					window.frmDf.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public DataExtractorGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDf = new JFrame();
		frmDf.setTitle("Data Extractor GUI");
		frmDf.setBounds(100, 100, 983, 451);
		frmDf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDf.getContentPane().setLayout(null);
		
		cbxAsset = new JComboBox();
		cbxAsset.setBounds(146, 68, 259, 27);
		cbxAsset.setModel(new DefaultComboBoxModel(AssetType.getAssetsStrings()));
		cbxAsset.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(cbxAsset);
		
		
		JLabel lblAsset = new JLabel("Asset:");
		lblAsset.setBounds(32, 68, 103, 27);
		lblAsset.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(lblAsset);
		
		JLabel lblDataEvent = new JLabel("Data Event:");
		lblDataEvent.setBounds(32, 101, 103, 27);
		lblDataEvent.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(lblDataEvent);
		
		JLabel lblIntervalTime = new JLabel("Interval Time:");
		lblIntervalTime.setBounds(32, 136, 103, 27);
		lblIntervalTime.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(lblIntervalTime);
		
		JLabel lblSaveFile = new JLabel("Save File:");
		lblSaveFile.setBounds(32, 169, 103, 27);
		lblSaveFile.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(lblSaveFile);
		
		cbxIntervalTime = new JComboBox();
		cbxIntervalTime.setBounds(146, 136, 259, 27);
		cbxIntervalTime.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxIntervalTime.setModel(new DefaultComboBoxModel(TimeFrameType.getTimeFrameStrings()));
		frmDf.getContentPane().add(cbxIntervalTime);
		
		cbxDataEvant = new JComboBox();
		cbxDataEvant.setBounds(146, 101, 259, 27);
		cbxDataEvant.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxDataEvant.setModel(new DefaultComboBoxModel(DataEventType.getDataEventStrings()));
		cbxDataEvant.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				if(DataEventType.getDataEventTypeFromString(cbxDataEvant.getSelectedItem().toString()) == DataEventType.JAPANESE)
				{
					//cbxIntervalTime.setModel(new DefaultComboBoxModel(TimeFrameType.getTimeFrameStrings()));
					cbxIntervalTime.setEnabled(true);
				}
				else
				{
					//cbxIntervalTime.setModel(null);
					cbxIntervalTime.setEnabled(false);					
				}
			}
		});
		frmDf.getContentPane().add(cbxDataEvant);
		
		tfdSaveFilePath = new JTextField();
		tfdSaveFilePath.setBounds(146, 170, 259, 27);
		tfdSaveFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(tfdSaveFilePath);
		tfdSaveFilePath.setColumns(10);
		
		btnSaveFile = new JButton("...");
		btnSaveFile.setBounds(417, 169, 46, 27);
		btnSaveFile.addActionListener(new ActionListener() {
			@Override
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser saveFile = new JFileChooser();
                if(saveFile.showSaveDialog(null) == JFileChooser.APPROVE_OPTION )
                {
                	tfdSaveFilePath.setText(saveFile.getSelectedFile().getPath());
                }	
            }
		});
		frmDf.getContentPane().add(btnSaveFile);
		
		btnStart = new JButton("Start");
		btnStart.setBounds(146, 210, 259, 42);
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!testRun)
				{
					startTest();
					btnStart.setText("Stop");
				}
				else
				{
					endTest();
					btnStart.setText("Start");
				}
				testRun = !testRun;
			}
		});
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(btnStart);
		
		lblDataSource = new JLabel("Data Source:");
		lblDataSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDataSource.setBounds(32, 35, 103, 27);
		frmDf.getContentPane().add(lblDataSource);
		
		cbxDataSource = new JComboBox();
		cbxDataSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxDataSource.setModel(new DefaultComboBoxModel(DataSource.getDataSourceStrings()));
		cbxDataSource.setBounds(146, 35, 259, 27);
		frmDf.getContentPane().add(cbxDataSource);
		frmDf.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{lblAsset, cbxAsset, lblDataEvent, lblDataSource, lblIntervalTime, cbxIntervalTime, lblSaveFile, cbxDataEvant, tfdSaveFilePath, btnSaveFile, btnStart, cbxDataSource}));
	}
	
	private void startTest()
	{
		DataSource dataSource = DataSource.getDataSourceFromString(cbxDataSource.getSelectedItem().toString());
		AssetType assetType = AssetType.getAssetTypeFromString(cbxAsset.getSelectedItem().toString());
		DataEventType dataEventType = DataEventType.getDataEventTypeFromString(cbxDataEvant.getSelectedItem().toString());
		List<Float> parameters = new ArrayList<Float>();
		if(dataEventType == DataEventType.JAPANESE)
		{
			parameters.add((float) TimeFrameType.getTimeFrameTypeFromString(cbxIntervalTime.getSelectedItem().toString()).getValueInMinutes());
			parameters.add((float)0); // TODO - check if we want history
		}
		
		RegisterDataExtractor.setDataSource(dataSource);
		String filePath = tfdSaveFilePath.getText();
		IDataExtractorObserver dataRecorder = new FileDataRecorder(dataSource, assetType, dataEventType, parameters, filePath, this);
		
		
		
		cbxDataSource.setEnabled(false);
		cbxAsset.setEnabled(false);
		cbxDataEvant.setEnabled(false);
		cbxIntervalTime.setEnabled(false);
		tfdSaveFilePath.setEnabled(false);
		btnSaveFile.setEnabled(false);
		// TODO
		
		new Thread((FileDataRecorder)dataRecorder).run();
	}
	
	private void endTest()
	{
		// TODO
		
		cbxDataSource.setEnabled(true);
		cbxAsset.setEnabled(true);
		cbxDataEvant.setEnabled(true);
		cbxIntervalTime.setEnabled(true);
		tfdSaveFilePath.setEnabled(true);
		btnSaveFile.setEnabled(true);
	}
	
	public void setErrorMessage(String ErrorMsg, boolean endProgram) {
		System.out.println("Error: " + ErrorMsg);
		if (endProgram) {
			endTest();
		}
	}

	@Override
	public void resetGUI() {
		endTest();
	}
}
