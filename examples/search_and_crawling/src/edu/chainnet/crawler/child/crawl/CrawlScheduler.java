package edu.chainnet.crawler.child.crawl;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.batch.BalancedAdaptable;
import org.greatfree.concurrency.batch.BalancedDispatcher;

import ca.mama.util.Time;
import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.HubSource;

// Created: 04/24/2021, Bing Li
public class CrawlScheduler implements BalancedAdaptable<HubSource>
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.child.crawl");

	private BalancedDispatcher<HubSource, CrawlNotifier, CrawlThread, CrawlThreadCreator> interactor;

	private CrawlScheduler()
	{
	}

	private static CrawlScheduler instance = new CrawlScheduler();

	public static CrawlScheduler CRAWL()
	{
		if (instance == null)
		{
			instance = new CrawlScheduler();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	@Override
	public void dispose() throws InterruptedException
	{
		log.info("Disposing CrawlScheduler ...");
		this.interactor.dispose();
		log.info("CrawlScheduler is disposed ...");
	}

	@Override
	public void init()
	{
		this.interactor = new BalancedDispatcher.BalanceDispatcherBuilder<HubSource, CrawlNotifier, CrawlThread, CrawlThreadCreator>()
				.fastPoolSize(CrawlConfig.CRAWLER_FAST_POOL_SIZE)
				.slowPoolSize(CrawlConfig.CRAWLER_SLOW_POOL_SIZE)
				.threadQueueSize(CrawlConfig.CRAWLER_THREAD_TASK_SIZE)
				.idleTime(CrawlConfig.CRAWLER_THREAD_IDLE_TIME)
				.notifier(new CrawlNotifier())
				.creator(new CrawlThreadCreator())
				.scheduler(Scheduler.GREATFREE().getScheduler())
				.build();

		this.interactor.setIdleChecker(CrawlConfig.CRAWLER_IDLE_CHECK_DELAY, CrawlConfig.CRAWLER_IDLE_CHECK_PERIOD);
	}

	@Override
	public boolean isShutdown()
	{
		return this.interactor.isShutdown();
	}

	@Override
	public void setPause() throws InterruptedException
	{
		this.interactor.setPause();
	}

	@Override
	public boolean isPaused()
	{
		return this.interactor.isPaused();
	}

	@Override
	public void holdOn(long time) throws InterruptedException
	{
		this.interactor.holdOn(time);
	}

	@Override
	public void keepOn()
	{
		this.interactor.keepOn();
	}

	@Override
	public Set<String> getFastThreadKeys()
	{
		return this.interactor.getFastThreadKeys();
	}

	@Override
	public Date getFastStartTime(String key)
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getStartTime();
		}
		return Time.INIT_TIME;
	}

	@Override
	public Date getFastEndTime(String key)
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getEndTime();
		}
		return Time.INIT_TIME;
	}

	@Override
	public String getFastTask(String key)
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getTaskInString();
		}
		return CrawlConfig.NO_HUB_URL;
	}

	@Override
	public Date getSlowStartTime(String key)
	{
		CrawlThread thread = this.interactor.getSlowFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getStartTime();
		}
		return Time.INIT_TIME;
	}

	@Override
	public Date getSlowEndTime(String key)
	{
		CrawlThread thread = this.interactor.getSlowFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getEndTime();
		}
		return Time.INIT_TIME;
	}

	@Override
	public String getSlowTask(String key)
	{
		CrawlThread thread = this.interactor.getSlowFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			return thread.getTaskInString();
		}
		return CrawlConfig.NO_HUB_URL;
	}

	@Override
	public int isFastEmpty(String key) throws InterruptedException
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			if (thread.isEmpty())
			{
				return CrawlConfig.EMPTY;
			}
			else
			{
				return CrawlConfig.NOT_EMPTY;
			}
		}
		return CrawlConfig.NOT_AVAILABLE;
	}

	@Override
	public int isIdle(String key)
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			if (thread.getIdleTime() != Time.INIT_TIME)
			{
				return CrawlConfig.IDLE;
			}
			else
			{
				return CrawlConfig.BUSY;
			}
		}
		return CrawlConfig.NOT_AVAILABLE;
	}

	@Override
	public Date getIdleTime(String key)
	{
		CrawlThread thread = this.interactor.getFastFunction(key);
		if (thread != CrawlConfig.NO_CRAWL_THREAD)
		{
			if (thread.getIdleTime() != Time.INIT_TIME)
			{
				return thread.getIdleTime();
			}
			else
			{
				return Time.INIT_TIME;
			}
		}
		return Time.INIT_TIME;
	}

	@Override
	public Set<String> getSlowThreadKeys()
	{
		return this.interactor.getSlowThreadKeys();
	}

	@Override
	public void alleviateSlow(String key) throws InterruptedException
	{
		this.interactor.alleviateSlow(key);
	}

	@Override
	public void restoreFast(String key) throws InterruptedException
	{
		this.interactor.restoreFast(key);
	}

	@Override
	public void appendSlow() throws InterruptedException
	{
		this.interactor.appendSlow();
	}

	@Override
	public void killSlow(String key) throws InterruptedException
	{
		this.interactor.kill(key);
	}

	@Override
	public void submit() throws InterruptedException
	{
		HubSource hub = HubRegistry.WWW().dequeue();
		if (hub != CrawlConfig.NO_HUB_SOURCE)
		{
			if (hub.getPassedTime() >= hub.getUpdatingPeriod())
			{
				if (!this.interactor.enqueue(hub))
				{
				}
			}
			else
			{
				HubRegistry.WWW().incrementTime(hub.getHubKey());
			}
		}
		else
		{
		}
	}

	public void submit(HubSource hub) throws InterruptedException
	{
		this.interactor.enqueue(hub);
	}

	@Override
	public List<HubSource> getLeftTasks(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void killAll() throws InterruptedException
	{
		// TODO Auto-generated method stub

	}
}
