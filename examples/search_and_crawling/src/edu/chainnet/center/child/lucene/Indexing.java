package edu.chainnet.center.child.lucene;

import java.io.IOException;

import org.greatfree.util.FileManager;

import edu.chainnet.center.CenterConfig;

// Created: 04/28/2021, Bing Li
public final class Indexing implements Runnable
{
	@Override
	public void run()
	{
		String key;
		Page page;
		for (int i = 0; i < CenterConfig.ONE_TIME_INDEXING_SIZE; i++)
		{
			if (!ChildProfile.CENTER().isPaused())
			{
				if (!PageCache.CENTER().isQueueEmpty())
				{
					key = PageCache.CENTER().dequeue();
					page = PageCache.CENTER().get(key);
					try
					{
						FileManager.createTextFile(ChildProfile.CENTER().getDocPath() + page.getPageKey(), page.getContent());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}

		if (!ChildProfile.CENTER().isPaused())
		{
			try
			{
				Indexer.perform(ChildProfile.CENTER().getIndexPath(), ChildProfile.CENTER().getDocPath());
			}
			catch (FilePathReadOnlyException e)
			{
				e.printStackTrace();
			}
			FileManager.removeFiles(ChildProfile.CENTER().getDocPath());
		}
	}
}

