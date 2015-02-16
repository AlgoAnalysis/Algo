package com.algotrado.extract.data;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public enum TimeFrameType {
	ONE_MINUTE(1) {
        @Override
        public boolean isTimeFrameStartTime(Date time) {
            return true;// Smalest timeframe every minute is a start of candle
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
            return true;// Smalest timeframe every minute is a start of candle
        }
    },
	FIVE_MINUTE(5){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	FIFTEEN_MINUTES(15){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	THIRTY_MINUTES(30){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (calendar.get(Calendar.MINUTE) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	ONE_HOUR(60){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	FOUR_HOURS(240){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	ONE_DAY(1440){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (((calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    },
	ONE_WEEK(10080){
        @Override
        public boolean isTimeFrameStartTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (( (calendar.get(Calendar.WEEK_OF_YEAR) * 10080) + (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == 0;
        }
        
        @Override
        public boolean isTimeFrameEndTime(Date time) {
        	Calendar calendar = GregorianCalendar.getInstance(); // creates a new calendar instance
			calendar.setTime(time);
            return (( (calendar.get(Calendar.WEEK_OF_YEAR) * 10080) + (calendar.get(Calendar.DAY_OF_WEEK) * 1440) + (calendar.get(Calendar.HOUR_OF_DAY) * 60) + calendar.get(Calendar.MINUTE)) % getValueInMinutes()) == (getValueInMinutes() - 1);
        }
    }/*,
	ONE_MONTH(40320)*/;//Comment: Month period is counted as 4 weeks and may not be accurate. Maybe we should not support month for now.
	
	private int valueInMinutes;
	
	private TimeFrameType(int valueInMinutes){
		this.valueInMinutes = valueInMinutes;
	}

	public int getValueInMinutes() {
		return valueInMinutes;
	}
	
	public boolean isLargerTimeFrame(TimeFrameType other) {
		return other != null && this.valueInMinutes > other.valueInMinutes;
	}
	
	/**
	 * Return if the time is the candle open time.
	 * @param time
	 * @return
	 */
	public abstract boolean isTimeFrameStartTime(Date time);
	
	/**
	 * Return if the time is the candle close time.
	 * @param time
	 * @return
	 */
	public abstract boolean isTimeFrameEndTime(Date time);
}
