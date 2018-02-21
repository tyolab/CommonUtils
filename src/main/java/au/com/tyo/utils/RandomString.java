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
		return instance;
	}

	private final Random random = new Random();

	private char[] buf;

	private int length;

    public RandomString() {

    }

	public RandomString(int length) {
		if (length < 1)
		  throw new IllegalArgumentException("length < 1: " + length);

		setLength(length);

		initializeBuffer();
	}

    public int getLength() {
        return length;
    }

    private void initializeBuffer() {
        buf = new char[length];
    }

    public void setLength(int length) {
		this.length = length;
		buf = null;
	}

	public String nextString() {
	    if (buf == null)
	        initializeBuffer();

		for (int idx = 0; idx < buf.length; ++idx)
		  buf[idx] = symbols[random.nextInt(symbols.length)];
		return new String(buf);
	}

	public static String createStringInLength(int length) {
        if (getInstance().getLength() != length) {
            getInstance().setLength(length);
            getInstance().initializeBuffer();
        }

        return getInstance().nextString();
	}
}


