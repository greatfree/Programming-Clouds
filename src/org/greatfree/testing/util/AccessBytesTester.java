package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 03/06/2020, Bing Li
class AccessBytesTester
{

	public static void main(String[] args) throws IOException
	{
		byte[] picBytes = FileManager.loadFile("/Users/libing/Temp/me.jpg");
		FileManager.saveFile("/Users/libing/GreatFreeLabs/Temp/me_copy.jpg", picBytes);
	}

}
