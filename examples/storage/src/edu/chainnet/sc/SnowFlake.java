package edu.chainnet.sc;

// Created: 10/13/2020, Bing Li
class SnowFlake
{
	private long workerId;
	private long datacenterId;
	private long sequence;

	public SnowFlake()
	{
		this(1, 1, 1);
	}

	public SnowFlake(long workerId)
	{
		this.workerId = workerId;
	}

	public SnowFlake(long workerId, long datacenterId, long sequence)
	{
		// sanity check for workerId
		if (workerId > maxWorkerId || workerId < 0)
		{
			throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0)
		{
			throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
		}

		this.workerId = workerId;
		this.datacenterId = datacenterId;
		this.sequence = sequence;
	}

	private long twepoch = 1288834974657L;

	private long workerIdBits = 5L;
	private long datacenterIdBits = 5L;
	private long maxWorkerId = -1L ^ (-1L << workerIdBits);
	private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
	private long sequenceBits = 12L;

	private long workerIdShift = sequenceBits;
	private long datacenterIdShift = sequenceBits + workerIdBits;
	private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
	private long sequenceMask = -1L ^ (-1L << sequenceBits);

	private long lastTimestamp = -1L;

	public long getWorkerId()
	{
		return workerId;
	}

	public long getDatacenterId()
	{
		return datacenterId;
	}

	public long getTimestamp()
	{
		return System.currentTimeMillis();
	}

	public String id()
	{
		return String.valueOf(nextId());
	}

	public synchronized long nextId()
	{
		long timestamp = timeGen();

		if (timestamp < lastTimestamp)
		{
			System.err.printf("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp);
			throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
		}

		if (lastTimestamp == timestamp)
		{
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0)
			{
				timestamp = tilNextMillis(lastTimestamp);
			}
		}
		else
		{
			sequence = 0;
		}

		lastTimestamp = timestamp;
		return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
	}

	private long tilNextMillis(long lastTimestamp)
	{
		long timestamp = timeGen();
		while (timestamp <= lastTimestamp)
		{
			timestamp = timeGen();
		}
		return timestamp;
	}

	private long timeGen()
	{
		return System.currentTimeMillis();
	}

}
