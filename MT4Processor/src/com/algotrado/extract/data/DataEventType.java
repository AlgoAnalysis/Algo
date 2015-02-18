package com.algotrado.extract.data;

public enum DataEventType {
	JAPANESE,/*After close of candle, send candle data*/
	NEW_QUOTE,/*Get price quote update when new quote arrives*/
	RSI;
}
