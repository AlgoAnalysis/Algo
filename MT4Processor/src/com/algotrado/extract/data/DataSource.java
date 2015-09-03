package com.algotrado.extract.data;

import java.util.List;

import com.algotrado.broker.Account;
import com.algotrado.broker.IBroker;
import com.algotrado.data.event.DataEventType;
import com.algotrado.data.event.basic.japanese.JapaneseCandleBar;
import com.algotrado.data.event.basic.japanese.JapaneseTimeFrameType;
import com.algotrado.extract.data.file.FileDataExtractor;
import com.algotrado.interactive.brokers.tws.IBBrokerSubjectDataExtrator;
import com.algotrado.trade.PositionDirectionType;
import com.algotrado.trade.PositionStatus;
import com.algotrado.util.Setting;

public enum DataSource implements IBroker{
	FILE("File")
	{
		@Override
        public IDataExtractorSubject getSubjectDataExtractor(AssetType assetType,DataEventType dataEventType,List<Double> parameters) 
		{
            return FileDataExtractor.getSubjectDataExtractor(assetType, dataEventType, parameters, Setting.getAssetDirectoryPath());
        }

		@Override
		public List<JapaneseCandleBar> getHistory(AssetType assetTypes,
				JapaneseTimeFrameType timeFrame) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int openPosition(AssetType asset, double amount,
				PositionDirectionType direction, double stopLoss,double takeProfit) {
			return FileDataExtractor.openPosition(asset, amount, direction, stopLoss,takeProfit);
		}

		@Override
		public boolean closePosition(int positionId, double amountToClose) {
			return FileDataExtractor.closePosition(positionId, amountToClose);
		}

		@Override
		public boolean closePosition(int positionId) {
			return FileDataExtractor.closePosition(positionId);
		}
		
		@Override
		public PositionStatus getPositionStatus(int positionId) {
			return FileDataExtractor.getPositionStatus(positionId);
		}

		@Override
		public boolean modifyPosition(int positionId, double newStopLoss,double newTakeProfit) {
			return FileDataExtractor.modifyPosition(positionId, newStopLoss, newTakeProfit);
		}

		@Override
		public double getLiveSpread(AssetType asset) {
			return FileDataExtractor.getLiveSpread(asset);
		}

		@Override
		public double getContractAmount(AssetType asset) {
			return FileDataExtractor.getContractAmount(asset);
		}

		@Override
		public double getMinimumContractAmountMultiply(AssetType asset) {
			return FileDataExtractor.getMinimumContractAmountMultiply(asset);
		}

		@Override
		public double getCurrentAskPrice(AssetType asset) {
			return FileDataExtractor.getCurrentAskPrice(asset);
		}

		@Override
		public Account getAccountStatus() {
			return FileDataExtractor.getAccountStatus();
		}
	},
	IB_TWS_BROKER("IB TWS Broker") {

		@Override
		public IDataExtractorSubject getSubjectDataExtractor(AssetType assetType, DataEventType dataEventType, List<Double> parameters) {
			return IBBrokerSubjectDataExtrator.getSubjectDataExtractor(assetType, dataEventType, parameters);
		}

		@Override
		public List<JapaneseCandleBar> getHistory(AssetType assetTypes, JapaneseTimeFrameType timeFrame) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int openPosition(AssetType asset, double amount, PositionDirectionType direction, double stopLoss,
				double takeProfit) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean closePosition(int positionId, double amountToClose) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean closePosition(int positionId) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public PositionStatus getPositionStatus(int positionId) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean modifyPosition(int positionId, double newStopLoss,
				double newTakeProfit) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public double getLiveSpread(AssetType asset) {
			return IBBrokerSubjectDataExtrator.getIBBrokerSubjectDataExtractor(asset).getLiveSpread();
		}

		@Override
		public double getCurrentAskPrice(AssetType asset) {
			return IBBrokerSubjectDataExtrator.getIBBrokerSubjectDataExtractor(asset).getCurrentAskPrice();
		}

		@Override
		public double getContractAmount(AssetType asset) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public double getMinimumContractAmountMultiply(AssetType asset) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Account getAccountStatus() {
			// TODO Auto-generated method stub
			return null;
		}
		
	};

	private String valueString;
	
	private DataSource(String valueString){
		this.valueString = valueString;
	}
	
	@Override
	public String toString() {
		return valueString;
	}

	public static String[] getDataSourceStrings()
	{
		String[] ret = new String[DataSource.values().length];
		for(int index = 0;index<DataSource.values().length;index++)
		{
			ret[index] = DataSource.values()[index].valueString;
		}
		return ret;
	}
	
	public static DataSource getDataSourceFromString(String valueString)
	{
		for(DataSource source:DataSource.values())
		{
			if(source.valueString == valueString)
			{
				return source;
			}
		}
		return null;
	}
	
}
