package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.Tools;

/**
 * 
 * @author libing
 * 
 *         01/04/2022, Bing Li
 *
 */
class SerializeDeserializeObject
{

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		Student s = new Student("greatree", 20);
		byte[] bs = Tools.serialize(s);
		Student d = (Student)Tools.deserialize(bs);
		System.out.println(d);

	}

}
