package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/01/2020, Bing Li
class WriteTextSync
{

	public static void main(String[] args) throws IOException
	{
		String path = "/home/libing/Temp/mytext.txt";
		String text = "abcdefghijklmnopqrstuvwxyz";
		System.out.println("File is being written!");
		FileManager.createTextFileSync(path, text, false);
		System.out.println("File is written!");
	}

}
