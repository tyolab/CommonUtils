/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SimpleDateUtils {
	
	public static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
	
	public static final long EIGHT_HOURS_IN_MILLIS = ONE_HOUR_IN_MILLIS * 8;
	
	public static final long HALF_DAY_IN_MILLIS = ONE_HOUR_IN_MILLIS * 12;
	
	public static final long ONE_DAY_IN_MILLIS = ONE_HOUR_IN_MILLIS * 24;
	
	public static final long ONE_WEEK_IN_MILLIS = ONE_DAY_IN_MILLIS * 7;

	private static SimpleDateFormat JSON_SIMPLE_DATE_FORMAT;

	public static String[] MONTHS = {
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	};
	
	public static String[] MONTHS_SHORT_FORM = {
		"Jan",
		"Feb",
		"Mar",
		"Apr",
		"May",
		"Jun",
		"Jul",
		"Aug",
		"Sep",
		"Oct",
		"Nov",
		"Dec"
	};
	
	public static String[] months = {
		"january",
		"february",
		"march",
		"april",
		"may",
		"june",
		"july",
		"august",
		"september",
		"october",
		"november",
		"december"
	};
	
	public static String[] months_short_form = {
		"jan",
		"feb",
		"mar",
		"apr",
		"may",
		"jun",
		"jul",
		"aug",
		"sep",
		"oct",
		"nov",
		"dec"
	};
	
	public static String whatMonthIsIt(String what) {
		String separators = "(\\.(\\s+|)|,(\\s+|)|\\|/|-|\\s)";
		String[] tokens = what.split(separators);
		String whatShouldBe = what;
		boolean found = false;
		String month = "";
		for (String token : tokens) {
			token = token.trim();
			if (token != null && token.length() > 0)
				for (int i = 0; i < months.length; ++i) {
					month = months[i];
					if (month.contains(token.toLowerCase())) {
						whatShouldBe = whatShouldBe.replace(token, MONTHS_SHORT_FORM[i]);
						found = true;
						break;
					}
				}
			if (found)
				break;
		}
		return whatShouldBe;
	}
	
	public static String formatDateByLocale(Date date, Locale locale) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
		return df.format(date);
	}
	
	public static Calendar parsetDateToCalendar(String what) throws ParseException {
		return dateToCalendar(parseDate(what));
	}

	public static Date parseSlashDelimAussieDate(String what) throws ParseException {
		Date date = null;
		date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(what);
		return date;
	}

	public static Date parseDate(String what) throws ParseException {
		Date date = null;
		try {
			date = new SimpleDateFormat("MMMM. d, yyyy", Locale.ENGLISH).parse(what);
		}
		catch (Exception ex) {}
		
		if (date == null)
			date = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(what);
		return date;
	}
	
	public static Calendar getGmtCalendar() {
		return Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	}
	
	public static Calendar dateToCalendar(Date date){ 
		  Calendar cal = getGmtCalendar();
		  cal.setTime(date);
		  
		  return cal;
	}
	
	public static void setDateToMidnight(Calendar date) {
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
	}

	public static Calendar getMidnightTime(Calendar oldDate) {
		Calendar date = (Calendar) oldDate.clone();
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 60);
		date.set(Calendar.SECOND, 59);
		date.set(Calendar.MILLISECOND, 0);
		return date;
	}
	
	public static Calendar get0000Date(Calendar date) {
		Calendar newDate = (Calendar) date.clone();
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		return newDate;
	}
	
	public static Calendar getGmtCalendar000() {
		Calendar now = getGmtCalendar();

		setDateToMidnight(now);
		return now;
	}
	
	public static int howManyDaysDifference(Calendar date1, Calendar date2) {
		return (int) (Math.abs(date1.getTimeInMillis() - date2.getTimeInMillis()) / ONE_DAY_IN_MILLIS); //(date2.get(Calendar.YEAR) - date1.get(Calendar.YEAR)) * 365 + date2.get(Calendar.DAY_OF_YEAR) - date2.get(Calendar.DAY_OF_YEAR);
	}
	
	/**
	 * @param date
	 * @return date format like "20140501"
	 */
	public static String dateToYMD(Date date) {
		return new SimpleDateFormat("yyyyMMdd", Locale.US).format(date);
	}

    /**
     * @param date
     * @return date format like "Aug 19"
     */
    public static String toMMMDD(Date date) {
        return new SimpleDateFormat("MMM dd", Locale.US).format(date);
    }

    /**
     * @param date
     * @return date format like "12:00 AM"
     */
    public static String toTimeIn12HourFormat(Date date) {
        return new SimpleDateFormat("hh:mm aa", Locale.US).format(date);
    }

    /**
     *
     * @return
     */
	public static String todayDateToYMD() {
		return dateToYMD(Calendar.getInstance().getTime());
	}

    /**
     *
     *  can be formatted into something like "2013-03-11T01:38:18.309Z" / "2013-03-11T01:38:18.309+0000"
     *
     * @param date
     * @return
     */
	public static String toJSONDateString(Date date) {
        if (null == JSON_SIMPLE_DATE_FORMAT)
            JSON_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		return JSON_SIMPLE_DATE_FORMAT.format(date);
	}

    /**
     *  can parse both "2013-03-11T01:38:18.309Z" and "2013-03-11T01:38:18.309+0000"
     *
     * @param jsonDate
     * @return
     * @throws ParseException
     */
	public static Date fromJSONDate(String jsonDate) throws ParseException {
        if (null == JSON_SIMPLE_DATE_FORMAT)
            JSON_SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		return JSON_SIMPLE_DATE_FORMAT.parse(jsonDate);
	}

    /**
     *
     * @param date
     * @param d
     * @return
     */
	public static Date plusHours(Date date, long d) {
        long mills = TimeUnit.HOURS.toMillis(d);
        return new Date(date.getTime() + mills);
    }

    /**
     *
     * @param date
     * @param d
     * @return
     */
    public static Date plusDays(Date date, long d) {
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(d));
    }

    /**
     *
     * @param date
     * @param d
     * @return
     */
    public static Date plusMinutes(Date date, long d) {
        return new Date(date.getTime() + TimeUnit.MINUTES.toMillis(d));
    }

    /**
     *
     * @param date
     * @return
     */
    public static String toMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy");
        return dateFormat.format(date);
    }

    /**
     * pattern "DD MMMM yyyy"
     *
     * @param date
     * @return
     */
    public static String toDayMonthYear(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("DD MMMM yyyy");
        return dateFormat.format(date);
    }

    /**
     * pattern "dd/MM/yyyy"
     *
     * @param date
     * @return
     */
    public static String toSlashDelimAussieDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static String toHoursMinutesSeconds(long seconds) {
		int day = (int)TimeUnit.SECONDS.toDays(seconds);
		long thatHour, thatMinute, thatSecond;
		thatHour = TimeUnit.SECONDS.toHours(seconds);
		thatMinute = TimeUnit.SECONDS.toMinutes(seconds);
		thatSecond = TimeUnit.SECONDS.toSeconds(seconds);
		long hours = thatHour - (day * 24);
		long minute = thatMinute - (thatHour * 60);
		long second = thatSecond - (thatMinute * 60);
		String text = String.format("%02d:%02d:%02d", hours, minute, second);
		return text;
    }
}
