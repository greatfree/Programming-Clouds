package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/03/2020, Bing Li
class ReadObjectSync
{

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		String path = "/home/libing/Temp/student";
		Student s = (Student)FileManager.readObjectSync(path);
		System.out.println(s.getName() + ", " + s.getAge());
	}

}
