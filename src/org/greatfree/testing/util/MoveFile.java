package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 06/30/2020, Bing Li
class MoveFile
{

	public static void main(String[] args)
	{
		try
		{
			FileManager.moveFile("/Users/libing/Temp/0.jpeg", "/Users/libing/Temp/1.jpg");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
