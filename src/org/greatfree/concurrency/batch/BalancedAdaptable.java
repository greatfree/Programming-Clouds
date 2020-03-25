package org.greatfree.concurrency.batch;

import java.util.Date;
import java.util.List;
import java.util.Set;

// Created: 04/23/2018, Bing Li
public interface BalancedAdaptable<Task>
{
	public void dispose() throws InterruptedException;
	public void init();
	public boolean isShutdown();
	public void setPause() throws InterruptedException;
	public boolean isPaused();
	public void holdOn(long time) throws InterruptedException;
	public void keepOn();
	public Set<String> getFastThreadKeys();
	public Date getFastStartTime(String key);
	public Date getFastEndTime(String key);
	public String getFastTask(String key);
	public Date getSlowStartTime(String key);
	public Date getSlowEndTime(String key);
	public String getSlowTask(String key);
	public int isFastEmpty(String key) throws InterruptedException;
	public int isIdle(String key);
	public Date getIdleTime(String key);
	public Set<String> getSlowThreadKeys();
	public void alleviateSlow(String key) throws InterruptedException;
	public void restoreFast(String key) throws InterruptedException;
	public void appendSlow() throws InterruptedException;
	public void killSlow(String key) throws InterruptedException;
	public void killAll() throws InterruptedException;
	public List<Task> getLeftTasks(String taskKey);
	public void submit() throws InterruptedException;
}
