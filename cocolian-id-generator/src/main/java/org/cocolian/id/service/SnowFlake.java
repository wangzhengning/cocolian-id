package org.cocolian.id.service;

import org.cocolian.rpc.server.PidRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SnowFlake
{
    /**
     * 初始时间戳 (已经开始生成id之后 不能随意修改 可能导致生成的id重复)
     */
    private static final long twepoch = 1288834974657L;

    /**
     * 机器id占位数
     */
    private static final long workerIdBits = 5L;

    /**
     * 数据中心占位数
     */
    private static final long datacenterIdBits = 5L;

    /**
     * 最大机器id
     */
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 最大数据中心id
     */
    private static final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /**
     * 序列占位长度
     */
    private static final long sequenceBits = 12L;

    /**
     * 机器id左移位数
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 数据中心id左移位数
     */
    private static final long datacenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间戳左移位数
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /**
     * 序列左移位数
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 上一次时间戳
     */
    private long lastTimestamp = -1L;

    /**
     * 序列id
     */
    private long sequence = 0L;

    /**
     * 机器id
     */
    private long workerId = 0L;

    /**
     * 数据中心id
     */
    private long datacenterId = 0L;

    /**
     * 获取日志对象
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(PidRecorder.class);

    /**
     * 批量获取id
     *
     * @param workerId
     * @param datacenterId
     * @param batchSize
     * @return
     */
    public List<Long> nextIdBatch(long workerId, long datacenterId, int batchSize) {
        List<Long> idList = new ArrayList<Long>(batchSize);
        for (int i = 0; i < batchSize; i++)
        {
            long id = nextId(workerId, datacenterId);
            idList.add(id);
        }
        return idList;
    }

    /**
     * id生成 单个
     * snowflake
     *
     * @return
     */
    public synchronized long nextId(long workerId, long datacenterId) {
        //初始化workerId及datacenterId
        initParam(workerId, datacenterId);
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp)
        {
            LOG.error("clock is moving backwards.  Rejecting requests until %d.",
                    lastTimestamp);
            throw new RuntimeException(("Clock moved backwards.  Refusing to generate id for " +
                    "%d milliseconds")
                    .format(
                            Long.toString(lastTimestamp - timestamp)));
        }
        if (lastTimestamp == timestamp)
        {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0)
            {
                timestamp = tilNextMillis(lastTimestamp);
                LOG.debug("前后俩次的时间戳相同 lastTimestamp:" + lastTimestamp + ",timestamp:" + timestamp
                        + "");
            }
        }
        else
        {
            sequence = 0L;
        }
        //对上一次时间进行赋值
        lastTimestamp = timestamp;
        //位或运算
        return ((timestamp - twepoch) << timestampLeftShift) |
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    /**
     * 初始化workerId datacenterId
     * @param workerId
     * @param datacenterId
     */
    private void initParam(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0)
        {
            throw new IllegalArgumentException(("worker Id can't be greater than %d or less than " +
                    "0").format(Long.toString(maxWorkerId)));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0)
        {
            throw new IllegalArgumentException(("datacenter Id can't be greater than %d or less " +
                    "than 0").format(Long.toString(maxDatacenterId)));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        LOG.debug("worker starting. timestamp left shift %d, datacenter id bits %d, worker id " +
                        "bits " +
                        "%d, sequence bits %d, workerid %d",
                timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId);
    }

    /**
     * 获取下一个时间戳
     *
     * @return
     */
    private long tilNextMillis(long lastTimestamp)

    {
        long timestamp = System.currentTimeMillis();
        //如果时间回退的情况或者时间相同则再次获取一次时间戳
        while (timestamp <= lastTimestamp)
        {
            timestamp = System.currentTimeMillis();
            LOG.debug("时间戳和上一次相同或者小于上一次 timestamp:" + timestamp + "  lastTimestamp:" +
                    lastTimestamp + "");
        }
        return timestamp;
    }
}