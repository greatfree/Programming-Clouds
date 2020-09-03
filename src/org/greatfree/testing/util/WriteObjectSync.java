package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/03/2020, Bing Li
class WriteObjectSync
{

	public static void main(String[] args) throws IOException
	{
		Student s = new Student("Li", 30);
		String path = "/home/libing/Temp/student";
		System.out.println("Object is being written!");
		FileManager.writeObjectSync(path, s);
		System.out.println("Object is written!");
	}

}
