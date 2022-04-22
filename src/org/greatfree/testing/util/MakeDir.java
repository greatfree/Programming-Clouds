package org.greatfree.testing.util;

import java.io.File;

import org.greatfree.util.FileManager;

/**
 * 
 * @author libing
 * 
 * 01/22/2022, Bing Li
 *
 */
class MakeDir
{

	public static void main(String[] args)
	{
//		String dir = "/Users/libing/Temp/great/free/";
		String dir = "/Users/libing/Temp/great/free/lb/labs/";
//		String dir = "/Users/libing/Temp/great/";
		
		File fd = new File(dir);
		System.out.println(fd.getParent());
		
		FileManager.makeDir(dir);
	}

}
