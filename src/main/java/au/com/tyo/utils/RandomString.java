/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.util.Random;

public class RandomString {

	private static RandomString instance;

	private static final char[] symbols = new char[36];

	static {
		for (int idx = 0; idx < 10; ++idx)
		  symbols[idx] = (char) ('0' + idx);
		for (int idx = 10; idx < 36; ++idx)
		  symbols[idx] = (char) ('a' + idx - 10);
	}

	private static RandomString getInstance() {
		if (null == instance)
			instance = new RandomString();
	}

	private final Random random = new Random();

	private char[] buf;

	private int length;

	public RandomString(int length) {
		if (length < 1)
		  throw new IllegalArgumentException("length < 1: " + length);

	}

	public void setLength(int length) {
		this.length = length;
	}

	public String nextString() {
		for (int idx = 0; idx < buf.length; ++idx)
		  buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}

	public static String createStringInLength(int length) {

	}
}


