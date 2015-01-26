package com.algotrado.mt4.impl;

import java.util.HashMap;
import java.util.Map;

public class StandartTimeframe {

	public static String getTimeFrame(AbstractCandleBar[] candles) {
		long fiveMinutes = 60 * 5 * 1000;
		long fifteenMinutes = 3 * fiveMinutes;
		long oneHour = 4 * fifteenMinutes;
		long fourHours = 4 * oneHour;
		long oneDay = 6 * fourHours;
		long fourDay = 4 * oneDay;
		long oneWeek = 7 * oneDay;
		Map<Long, Integer> timeFrameStats = new HashMap<Long, Integer>();
		Map<Long, String> timeFrameNames = new HashMap<Long, String>();
		timeFrameNames.put(fiveMinutes, "5 Minutes");
		timeFrameNames.put(fifteenMinutes, "15 Minutes");
		timeFrameNames.put(oneHour, "1 Hour");
		timeFrameNames.put(fourHours, JapaneseCandleBar._4_HOUR);
		timeFrameNames.put(oneDay, JapaneseCandleBar._1_DAY);
		timeFrameNames.put(oneWeek, "1 Week");
		for (int i = 1 ; i < candles.length; i++) {
			long currTimeDifference = ((JapaneseCandleBar)candles[i]).getTime().getTime() - ((JapaneseCandleBar)candles[i - 1]).getTime().getTime();
			if (currTimeDifference == fiveMinutes) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );
			}
			else if (currTimeDifference == fifteenMinutes) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );
			}
			else if (currTimeDifference == oneHour) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );		
			}
			else if (currTimeDifference == fourHours) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );
			}
			else if (currTimeDifference >= oneDay && currTimeDifference <= fourDay) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );
			}
			else if (currTimeDifference == oneWeek) {
				timeFrameStats.put(currTimeDifference, (timeFrameStats.get(currTimeDifference) == null) ? 0 : (timeFrameStats.get(currTimeDifference) + 1) );
			}
		}
		
		// find maximum value key.
		Map.Entry<Long, Integer> maxEntry = null;
		for (Map.Entry<Long, Integer> entry : timeFrameStats.entrySet())
		{
			if (maxEntry == null || entry.getValue() > maxEntry.getValue())
		    {
		        maxEntry = entry;
		    }
		}
		
		return  timeFrameNames.get(maxEntry.getKey());
	}

}
