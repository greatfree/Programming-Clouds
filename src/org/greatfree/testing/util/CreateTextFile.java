package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/08/2021, Bing Li
class CreateTextFile
{
	public static void main(String[] args) throws IOException
	{
		FileManager.createTextFile("/Users/libing/GreatFreeLabs/Temp/1234.txt", "abc");
	}
}

