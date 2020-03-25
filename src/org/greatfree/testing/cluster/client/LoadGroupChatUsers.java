package org.greatfree.testing.cluster.client;

import java.util.List;

import org.greatfree.util.XPathOnDiskReader;

// Created: 04/21/2019, Bing Li
class LoadGroupChatUsers
{

	public static void main(String[] args)
	{
		XPathOnDiskReader reader = new XPathOnDiskReader("/home/libing/Temp/UserNames.xml", false);
		List<String> userNames = reader.readStrings("/GroupChat/UserName/text()");
		
		System.out.println("userNames' size = " + userNames.size());
		
		for (String entry : userNames)
		{
			System.out.println(entry);
		}
		reader.close();
	}

}
