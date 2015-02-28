package com.algotrado.data.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public enum TimeFrameType {
	ONE_MINUTE(1,"1 minute") {
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//            return true;// Smalest timeframe every minute is a start of candle
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//            return true;// Smalest timeframe every minute is a start of candle
//        }
    },
	FIVE_MINUTE(5,"5 minute"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	return isTimeFrameStartTimeForUnderHourTimes(time, prevTime, getValueInMinutes());
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	return isTimeFrameEndTimeForUnderHourTimes(time, lowerInterval, getValueInMinutes());
//        }
    },
	FIFTEEN_MINUTES(15,"15 minute"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	return isTimeFrameStartTimeForUnderHourTimes(time, prevTime, getValueInMinutes());
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	return isTimeFrameEndTimeForUnderHourTimes(time, lowerInterval, getValueInMinutes());
//        }
    },
	THIRTY_MINUTES(30,"30 minute"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	return isTimeFrameStartTimeForUnderHourTimes(time, prevTime, getValueInMinutes());
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	return isTimeFrameEndTimeForUnderHourTimes(time, lowerInterval, getValueInMinutes());
//        }
    },
	ONE_HOUR(60,"1 hour"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	return isTimeFrameStartTimeForUnderHourTimes(time, prevTime, getValueInMinutes());
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	return isTimeFrameEndTimeForUnderHourTimes(time, lowerInterval, getValueInMinutes());
//        }
    },
	FOUR_HOURS(240,"4 hours"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
//        }
    },
	ONE_DAY(1440,"day"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
//        }
    },
	ONE_WEEK(10080,"week"){
//        @Override
//        public boolean isTimeFrameStartTime(Date time, Date prevTime) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (( (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
//        }
        
//        @Override
//        public boolean isTimeFrameEndTime(Date time, int lowerInterval) {
//        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
//			calendar.setTime(time);
//            return (( (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
//        }
    }/*,
	ONE_MONTH(40320)*/;//Comment: Month period is counted as 4 weeks and may not be accurate. Maybe we should not support month for now.
	
	public String getValueString() {
		return valueString;
	}

	private int valueInMinutes;
	private String valueString;
	
	private TimeFrameType(int valueInMinutes,String valueString){
		this.valueInMinutes = valueInMinutes;
		this.valueString = valueString;
	}

	public int getValueInMinutes() {
		return valueInMinutes;
	}
	
	public boolean isLargerTimeFrame(TimeFrameType other) {
		return other != null && this.valueInMinutes > other.valueInMinutes;
	}
	
	public static boolean isIntervalValid(float interval)
	{
		for(TimeFrameType eInterval:TimeFrameType.values())
		{
			if(eInterval.getValueInMinutes() == interval)
			{
				return true;
			}
		}
		return false;
	}
	
	public static TimeFrameType getTimeFrameFromInterval(float interval)
	{
		for(TimeFrameType eInterval:TimeFrameType.values())
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
		String[] ret = new String[TimeFrameType.values().length];
		for(int index = 0;index<TimeFrameType.values().length;index++)
		{
			ret[index] = TimeFrameType.values()[index].valueString;
		}
		return ret;
	}
	
	public static TimeFrameType getTimeFrameTypeFromString(String valueString)
	{
		for(TimeFrameType timeFrame:TimeFrameType.values())
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
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
		calendar.setTime(time);
		long currTimeInMinutes;
    	if (valueInMinutes <= 60) {
    		currTimeInMinutes = calendar.get(Calendar.MINUTE);
    	} else if (valueInMinutes <= 1440) {
    		currTimeInMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
    	} else { //week timeframe
    		currTimeInMinutes = (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
    	}
        return (currTimeInMinutes / lowerInterval) % (valueInMinutes/ lowerInterval) == ((valueInMinutes/ lowerInterval) - 1);
	}
	
	/**
	 * Return if the time is the candle open time.
	 * @param time
	 * @return
	 */
	public boolean isTimeFrameStartTime(Date time, Date prevTime) {
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
    	calendar.setTime(time);
    	long currTimeInMinutes;
    	if (valueInMinutes <= 60) {
    		currTimeInMinutes = calendar.get(Calendar.MINUTE);
    	} else if (valueInMinutes <= 1440) {
    		currTimeInMinutes = (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
    	} else { //week timeframe
    		currTimeInMinutes = (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE);
    	}
    	if (prevTime == null || (currTimeInMinutes % valueInMinutes) == 0) {
    		return (currTimeInMinutes % valueInMinutes) == 0;
    	}
    	Calendar calendarPrev = GregorianCalendar.getInstance(); // creates a new calendar instance
    	calendarPrev.setTime(prevTime);
    	long prevTimeInMinutes;
    	if (valueInMinutes <= 60) {
    		prevTimeInMinutes = calendarPrev.get(Calendar.MINUTE);
    	} else if (valueInMinutes <= 1440) {
    		prevTimeInMinutes = (calendarPrev.get(Calendar.HOUR_OF_DAY) * 60) + calendarPrev.get(Calendar.MINUTE);
    	} else { //week timeframe
    		prevTimeInMinutes = (calendarPrev.get(Calendar.DAY_OF_WEEK) * 1440) + (calendarPrev.get(Calendar.HOUR_OF_DAY) * 60) + calendarPrev.get(Calendar.MINUTE);
    	}
        return time.after(prevTime) && 
        		( ((currTimeInMinutes % valueInMinutes) <= (prevTimeInMinutes % valueInMinutes))
        		 || ((time.getTime() - prevTime.getTime())/(60000)) > valueInMinutes);
	}
}
