package org.greatfree.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*
 * The class consists of some common constant and methods to process time. 08/26/2014, Bing Li
 */

// Created: 08/26/2014, Bing Li
public class Time
{
	// The date format in the Chinese convention. 08/26/2014, Bing Li
	public static final DateFormat CHINA_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd EEE HH:mm:ss", Locale.CHINA);
	// The date format in the US convention. 08/26/2014, Bing Li
	public static final DateFormat US_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy EEE hh:mm:ss a", Locale.US);
	// The initial time in the type of String defined in the sample code. Readers can select another time to fit your requirements. 08/26/2014, Bing Li
	public static final String INIT_TIME_US = "06/04/1989 SUN 00:00:00 AM";
	// A constant to represent the null value of Date. 08/26/2014, Bing Li
	public static final Date NO_TIME = null;
	// The initial time in the type of Date defined in the sample code. Readers can select another time to fit your requirements. 08/26/2014, Bing Li
	public static final Date INIT_TIME = Time.convertUSTimeToDate(Time.INIT_TIME_US);
	public static final long INIT_TIME_LONG = Time.convertUSTimeToDate(INIT_TIME_US).getTime();
	public static final long TIME_SEED = 70L * 365 * 24 * 60 * 60 * 1000;

	/*
	 * Calculate the time span in the unit of millisecond between two time moments. The argument, endTime, is the later moment and the one of beginTime is the earlier moment. 08/26/2014, Bing Li
	 */
	public static long getTimespanInMilliSecond(Date endTime, Date beginTime)
	{
		return (endTime.getTime() - beginTime.getTime());
	}
	
	/*
	 * Calculate the time span in the unit of second between two time moments. The argument, endTime, is the later moment and the one of beginTime is the earlier moment. 08/26/2014, Bing Li
	 */
	public static long getTimeSpanInSecond(Date endTime, Date beginTime)
	{
		return (endTime.getTime() - beginTime.getTime()) / 1000;
	}

	/*
	 * Convent the US time in the type of String to the type of Date. 08/26/2014, Bing Li
	 */
	public synchronized static Date convertUSTimeToDate(String usTimeString)
	{
		try
		{
			return Time.US_DATE_FORMAT.parse(usTimeString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return UtilConfig.NO_TIME;
		}
	}
	
	/*
	 * Convent the Chinese time in the type of String to the type of Date. 08/26/2014, Bing Li
	 */
	public synchronized static Date convertChinaTimeToDate(String chinaTimeString)
	{
		try
		{
			return Time.CHINA_DATE_FORMAT.parse(chinaTimeString);
		}
		catch (ParseException e)
		{
			e.printStackTrace();
			return UtilConfig.NO_TIME;
		}
	}

	/*
	 * Pause for a moment. The time of pausing is equal to the argument of time in the unit of millisecond. 08/26/2014, Bing Li
	 */
	public static void pause(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
	
	public static Date getRandomTime()
	{
		return new Date(INIT_TIME_LONG + (Math.abs(Rand.getNextLong()) % TIME_SEED));
	}
	
	public static Date getTime(long time)
	{
		return new Date(time);
	}
	
	public static Date getEarlyTime(long time)
	{
		return new Date(Calendar.getInstance().getTimeInMillis() - time);
	}
	
	public static String convert(Date time, SimpleDateFormat format)
	{
		return format.format(time);
	}
	
	public static String addMinutes(String time, int length, SimpleDateFormat format) throws ParseException
	{
		Date t = format.parse(time);
		Calendar cal = Calendar.getInstance();
		cal.setTime(t);
		cal.add(Calendar.MINUTE, length);
		return format.format(cal.getTime());
	}
}
