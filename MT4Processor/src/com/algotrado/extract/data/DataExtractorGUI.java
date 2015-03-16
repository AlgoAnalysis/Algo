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
import javax.swing.SwingUtilities;

import org.eclipse.wb.swing.FocusTraversalOnArray;

import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.output.file.FileDataRecorder;
import com.algotrado.output.file.IGUIController;

public class DataExtractorGUI implements IGUIController,Runnable {

	private JFrame frmDf;
	private JTextField tfdSaveFilePath;
	private boolean testRun = false;
	private JButton btnStart;
	private JComboBox<String> cbxIntervalTime;
	private JComboBox<String> cbxDataEvant;
	private JComboBox<String> cbxAsset;
	private JLabel lblDataSource;
	private JComboBox<String> cbxDataSource;
	private JButton btnSaveFile;
	private JTextField txdTime;

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
		
		cbxAsset = new JComboBox<String>();
		cbxAsset.setBounds(146, 68, 259, 27);
		cbxAsset.setModel(new DefaultComboBoxModel<String>(AssetType.getAssetsStrings()));
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
		
		cbxIntervalTime = new JComboBox<String>();
		cbxIntervalTime.setBounds(146, 136, 259, 27);
		cbxIntervalTime.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxIntervalTime.setModel(new DefaultComboBoxModel<String>(JapaneseTimeFrameType.getTimeFrameStrings()));
		frmDf.getContentPane().add(cbxIntervalTime);
		
		cbxDataEvant = new JComboBox<String>();
		cbxDataEvant.setBounds(146, 101, 259, 27);
		cbxDataEvant.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxDataEvant.setModel(new DefaultComboBoxModel<String>(DataEventType.getDataEventStrings()));
		cbxDataEvant.addActionListener(new ActionListener() {
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
				}
				else
				{
					endTest();	
				}
			}
		});
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(btnStart);
		
		lblDataSource = new JLabel("Data Source:");
		lblDataSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblDataSource.setBounds(32, 35, 103, 27);
		frmDf.getContentPane().add(lblDataSource);
		
		cbxDataSource = new JComboBox<String>();
		cbxDataSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cbxDataSource.setModel(new DefaultComboBoxModel<String>(DataSource.getDataSourceStrings()));
		cbxDataSource.setBounds(146, 35, 259, 27);
		frmDf.getContentPane().add(cbxDataSource);
		
		JLabel lblTime = new JLabel("Time:");
		lblTime.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblTime.setBounds(32, 268, 103, 27);
		frmDf.getContentPane().add(lblTime);
		
		txdTime = new JTextField();
		txdTime.setEditable(false);
		txdTime.setEnabled(false);
		txdTime.setFont(new Font("Tahoma", Font.PLAIN, 16));
		txdTime.setColumns(10);
		txdTime.setBounds(146, 265, 259, 27);
		frmDf.getContentPane().add(txdTime);
		frmDf.getContentPane().setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{lblAsset, cbxAsset, lblDataEvent, lblDataSource, lblIntervalTime, cbxIntervalTime, lblSaveFile, cbxDataEvant, tfdSaveFilePath, btnSaveFile, btnStart, cbxDataSource}));
	}
	private long timeMili;
	private IDataExtractorObserver dataRecorder;
	private AssetType assetType;
	private DataEventType dataEventType;
	private List<Double> parameters;
	private DataSource dataSource;
	private void startTest()
	{
		dataSource = DataSource.getDataSourceFromString(cbxDataSource.getSelectedItem().toString());
		assetType = AssetType.getAssetTypeFromString(cbxAsset.getSelectedItem().toString());
		dataEventType = DataEventType.getDataEventTypeFromString(cbxDataEvant.getSelectedItem().toString());
		parameters = new ArrayList<Double>();
		if(dataEventType == DataEventType.JAPANESE)
		{
			parameters.add((double) JapaneseTimeFrameType.getTimeFrameTypeFromString(cbxIntervalTime.getSelectedItem().toString()).getValueInMinutes());
			parameters.add((double)0); // TODO - check if we want history
		}
		
//		RegisterDataExtractor.setDataSource(dataSource);
		String filePath = tfdSaveFilePath.getText();
		dataRecorder = new FileDataRecorder(filePath, this);
		
		
		testRun = true;
		cbxDataSource.setEnabled(false);
		cbxAsset.setEnabled(false);
		cbxDataEvant.setEnabled(false);
		cbxIntervalTime.setEnabled(false);
		tfdSaveFilePath.setEnabled(false);
		btnSaveFile.setEnabled(false);
		btnStart.setText("Stop");
		timeMili = System.currentTimeMillis();
		SwingUtilities.invokeLater((DataExtractorGUI)this);
	}
	@Override
	public void run()
	{
		RegisterDataExtractor.register(dataSource, assetType, dataEventType, parameters, dataRecorder);
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
		btnStart.setText("Start");
		testRun = false;
		Double deffTime = Double.valueOf((double)(System.currentTimeMillis() - timeMili)/1000);
		txdTime.setText(deffTime.toString() + " Sec");
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
