package org.greatfree.testing.cache.distributed;

import java.util.Calendar;

import org.greatfree.framework.cps.cache.data.MyStoreData;
import org.greatfree.framework.cps.cache.terminal.MyTerminalStackStore;

// Created: 07/16/2019, Bing Li
class TerminalStackStoreTester
{

	public static void main(String[] args)
	{
		MyTerminalStackStore.BACKEND().init("/Users/libing/Temp/");
		
		for (int i = 0; i < 10; i++)
		{
			for (int j = 0; j < 100; j++)
			{
				try
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				MyTerminalStackStore.BACKEND().push(new MyStoreData("cache" + i, "key" + i + j, j, Calendar.getInstance().getTime()));
			}
		}

		for (int i = 0; i < 10; i++)
		{
			System.out.println(MyTerminalStackStore.BACKEND().peerBottom("cache" + i));
		}
		
		MyTerminalStackStore.BACKEND().dispose();

	}

}
