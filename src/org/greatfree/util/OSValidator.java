package org.greatfree.util;

// Created: 04/07/2017, Bing Li
public class OSValidator
{
	private static String OS = System.getProperty(UtilConfig.OS_NAME).toLowerCase();

	public static boolean isWindows()
	{
		return (OS.indexOf(UtilConfig.WIN) >= 0);
	}

	public static boolean isMac()
	{
		return (OS.indexOf(UtilConfig.MAC) >= 0);
	}

	public static boolean isUnix()
	{
		return (OS.indexOf(UtilConfig.NIX) >= 0 || OS.indexOf(UtilConfig.NUX) >= 0 || OS.indexOf(UtilConfig.AIX) > 0);
	}

	public static boolean isSolaris()
	{
		return (OS.indexOf(UtilConfig.SUN_OS) >= 0);
	}
}
