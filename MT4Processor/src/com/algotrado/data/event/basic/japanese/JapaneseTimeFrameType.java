package com.algotrado.data.event.basic.japanese;

import java.util.Date;



public enum JapaneseTimeFrameType {
	ONE_MINUTE(1,"1 minute"),
	FIVE_MINUTE(5,"5 minute"),
	FIFTEEN_MINUTES(15,"15 minute"),
	THIRTY_MINUTES(30,"30 minute"),
	ONE_HOUR(60,"1 hour"),
	FOUR_HOURS(240,"4 hours"),
	ONE_DAY(1440,"day"),
	ONE_WEEK(10080,"week")/*,
	ONE_MONTH(40320)*/;//Comment: Month period is counted as 4 weeks and may not be accurate. Maybe we should not support month for now.
	
	public String getValueString() {
		return valueString;
	}
	private static final long MINUTES_IN_MILISEC = 60*1000;
	private static final long DAY_IN_MINUTES = 24*60;
	private int valueInMinutes;
	private String valueString;
	private long timeOffsetFromZeroDateInMinutes;
	private static final int weekStartDay = 0; // sunday
	
	private JapaneseTimeFrameType(int valueInMinutes,String valueString){
		this.valueInMinutes = valueInMinutes;
		this.valueString = valueString;
		if(valueInMinutes == 10080) // one week - TODO, check way we can't use this == ONE_WEEK ???
		{
			Date tempDate = new Date(0);
			timeOffsetFromZeroDateInMinutes = (tempDate.getDay() - weekStartDay)*DAY_IN_MINUTES;
		}
		else
		{
			timeOffsetFromZeroDateInMinutes = 0;
		}
	}

	public int getValueInMinutes() {
		return valueInMinutes;
	}
	
	public boolean isLargerTimeFrame(JapaneseTimeFrameType other) {
		return other != null && this.valueInMinutes > other.valueInMinutes;
	}
	
	public Date getRoundDate(Date date)
	{
		long dateInMinutes = date.getTime()/MINUTES_IN_MILISEC;
		long newDateInMinutes = ((long)dateInMinutes/valueInMinutes) * valueInMinutes;
		Date newDate = new Date(newDateInMinutes*MINUTES_IN_MILISEC);
		return newDate;
	}
	public static boolean isIntervalValid(float interval)
	{
		for(JapaneseTimeFrameType eInterval:JapaneseTimeFrameType.values())
		{
			if(eInterval.getValueInMinutes() == interval)
			{
				return true;
			}
		}
		return false;
	}
	
	public static JapaneseTimeFrameType getTimeFrameFromInterval(float interval)
	{
		for(JapaneseTimeFrameType eInterval:JapaneseTimeFrameType.values())
		{
			if(eInterval.getValueInMinutes() == interval)
			{
				return eInterval;
			}
		}
		return null;		
	}
	
	public static String[] getTimeFrameStrings()
	{
		String[] ret = new String[JapaneseTimeFrameType.values().length];
		for(int index = 0;index<JapaneseTimeFrameType.values().length;index++)
		{
			ret[index] = JapaneseTimeFrameType.values()[index].valueString;
		}
		return ret;
	}
	
	public static JapaneseTimeFrameType getTimeFrameTypeFromString(String valueString)
	{
		for(JapaneseTimeFrameType timeFrame:JapaneseTimeFrameType.values())
		{
			if(timeFrame.valueString == valueString)
			{
				return timeFrame;
			}
		}
		return null;
	}
	
	
	/**
	 * Return if the time is the candle close time.
	 * @param time
	 * @return
	 */
	public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//		calendar.setTime(time);
//		long currTimeInMinutes;
//    	if (valueInMinutes <= 60) {
//    		currTimeInMinutes = calendar.get(Calendar.MINUTE);
//    	} else if (valueInMinutes <= 1440) {
//    		currTimeInMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
//    	} else { //week timeframe
//    		currTimeInMinutes = (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
//    	}
//      return (currTimeInMinutes / lowerInterval) % (valueInMinutes/ lowerInterval) == ((valueInMinutes/ lowerInterval) - 1);
		long currTimeInMinutes = time.getTime()/MINUTES_IN_MILISEC - timeOffsetFromZeroDateInMinutes;
		return (currTimeInMinutes + lowerInterval) % valueInMinutes == 0;
	}
	
	/**
	 * Return if the time is the candle open time.
	 * @param time
	 * @return
	 */
	public boolean isTimeFrameStartTime(Date time, Date openTime) {
//		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//    	calendar.setTime(time);
//    	long currTimeInMinutes;
//    	if (valueInMinutes <= 60) {
//    		currTimeInMinutes = calendar.get(Calendar.MINUTE);
//    	} else if (valueInMinutes <= 1440) {
//    		currTimeInMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
//    	} else { //week timeframe
//    		currTimeInMinutes = (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
//    	}
//
//    	Calendar calendarPrev = GregorianCalendar.getInstance(); // creates a new calendar instance
//    	calendarPrev.setTime(prevStartTime);
//    	long prevTimeInMinutes;
//    	if (valueInMinutes <= 60) {
//    		prevTimeInMinutes = calendarPrev.get(Calendar.MINUTE);
//    	} else if (valueInMinutes <= 1440) {
//    		prevTimeInMinutes = (calendarPrev.get(Calendar.HOUR_OF_DAY) * 60) + calendarPrev.get(Calendar.MINUTE);
//    	} else { //week timeframe
//    		prevTimeInMinutes = (calendarPrev.get(Calendar.DAY_OF_WEEK) * 1440) + (calendarPrev.get(Calendar.HOUR_OF_DAY) * 60) + calendarPrev.get(Calendar.MINUTE);
//    	}
//        return time.after(prevStartTime) && 
//        		( ((currTimeInMinutes % valueInMinutes) <= (prevTimeInMinutes % valueInMinutes))
//        		 || ((time.getTime() - prevStartTime.getTime())/(60000)) > valueInMinutes);
		long openTimeInMinutes = (openTime.getTime() / MINUTES_IN_MILISEC) - timeOffsetFromZeroDateInMinutes; // in JapaneseCandleDataExtractor the prevStartTime is Floor to interval
		long timeInMinutesFloorInMinutes = (time.getTime()/ MINUTES_IN_MILISEC) - timeOffsetFromZeroDateInMinutes;
		return (openTimeInMinutes/valueInMinutes) < (timeInMinutesFloorInMinutes/valueInMinutes);
	}
}
