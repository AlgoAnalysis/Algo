package com.algotrado.extract.data;

import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataExtractorGUI {

	private JFrame frmDf;
	private JTextField tfdSaveFilePath;
	private boolean testRun = false;
	private JButton btnStart;
	private JComboBox cbxIntervalTime;
	private JComboBox cbxDataEvant;
	private JComboBox cbxAsset;
	private JLabel lblDataSource;
	private JComboBox cBxDataSource;

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
					cbxIntervalTime.enable(true);
				}
				else
				{
					//cbxIntervalTime.setModel(null);
					cbxIntervalTime.enable(false);					
				}
			}
		});
		frmDf.getContentPane().add(cbxDataEvant);
		
		tfdSaveFilePath = new JTextField();
		tfdSaveFilePath.setBounds(146, 170, 259, 27);
		tfdSaveFilePath.setFont(new Font("Tahoma", Font.PLAIN, 16));
		frmDf.getContentPane().add(tfdSaveFilePath);
		tfdSaveFilePath.setColumns(10);
		
		JButton btnSaveFile = new JButton("...");
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
		
		cBxDataSource = new JComboBox();
		cBxDataSource.setFont(new Font("Tahoma", Font.PLAIN, 16));
		cBxDataSource.setModel(new DefaultComboBoxModel(DataSource.getDataSourceStrings()));
		cBxDataSource.setBounds(146, 35, 259, 27);
		frmDf.getContentPane().add(cBxDataSource);
	}
	
	private void startTest()
	{
		DataSource dataSource = DataSource.getDataSourceFromString(cBxDataSource.getSelectedItem().toString());
		AssetType assetType = AssetType.getAssetTypeFromString(cbxAsset.getSelectedItem().toString());
		DataEventType dataEventType = DataEventType.getDataEventTypeFromString(cbxDataEvant.getSelectedItem().toString());
		List<Float> parameters = new ArrayList<Float>();
		if(dataEventType == DataEventType.JAPANESE)
		{
			parameters.add((float) TimeFrameType.getTimeFrameTypeFromString(cbxIntervalTime.getSelectedItem().toString()).getValueInMinutes());
			parameters.add((float)0); // TODO - check if we want history
		}
		
		// TODO
	}
	
	private void endTest()
	{
		// TODO
	}
}
