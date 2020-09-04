package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 09/04/2020, Bing Li
class ReadWriteObjectSync
{

	public static void main(String[] args) throws IOException
	{
		String path = "/home/libing/Temp/student";
		Student s = (Student)FileManager.readObjectSync(path);
		if (s != null)
		{
			System.out.println(s);
		}
		else
		{
			s = new Student("Li", 30);
			System.out.println("Object is being written!");
			FileManager.writeObjectSync(path, s);
			System.out.println("Object is written!");
		}
		s = (Student)FileManager.readObjectSync(path);
		if (s != null)
		{
			System.out.println(s);
		}
		else
		{
			System.out.println("Student is NULL");
		}
	}

}
