package com.algotrado.pattern.test;

import javax.swing.SwingUtilities;

public class PatternTest{

	static InternalPatternTest test;
	public static void main(String[] args) {
		test = new InternalPatternTest();
		SwingUtilities.invokeLater((InternalPatternTest)test);
	}
}
