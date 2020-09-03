package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/01/2020, Bing Li
class ReadTextSync
{

	public static void main(String[] args) throws IOException
	{
		String path = "/home/libing/Temp/mytext.txt";
//		String text = FileManager.loadTextSync(path, 10);
		String text = FileManager.loadTextSync(path);
		System.out.println(text);
	}

}
