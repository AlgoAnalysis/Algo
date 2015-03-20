package com.algotrado.entry.strategy.test;

import javax.swing.SwingUtilities;

public class EntryStrategyTest {

	static InternalEntryStrategyTest test;
	public static void main(String[] args) {
		test = new InternalEntryStrategyTest();
		SwingUtilities.invokeLater((InternalEntryStrategyTest)test);
	}
}
