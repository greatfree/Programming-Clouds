package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/08/2021, Bing Li
class RemoveFile
{

	public static void main(String[] args) throws IOException
	{
//		FileManager.removeDir("/Users/libing/Temp/n3w.tar");
		FileManager.removeFile("/Users/libing/Temp/n3w.tar");
	}

}
