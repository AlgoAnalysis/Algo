package com.algotrado.extract.data;

public enum TimeFrameType {
	ONE_MINUTE(1),
	FIVE_MINUTE(5),
	FIFTEEN_MINUTES(15),
	THIRTY_MINUTES(30),
	ONE_HOUR(60),
	FOUR_HOURS(240),
	ONE_DAY(1440),
	ONE_WEEK(10080)/*,
	ONE_MONTH(40320)*/;//Comment: Month period is counted as 4 weeks and may not be accurate. Maybe we should not support month for now.
	
	private int valueInMinutes;
	
	private TimeFrameType(int valueInMinutes){
		this.valueInMinutes = valueInMinutes;
	}

	public int getValueInMinutes() {
		return valueInMinutes;
	}
}
