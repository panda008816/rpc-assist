package com.dmall.rpcassist.retry.core;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 回滚服务延时类
 * 
 * Delayed扩展了Comparable接口，比较的基准为延时的时间值
 * 
 * Delayed接口的实现类getDelay的返回值应为固定值（final）
 * 
 * DelayQueue内部是使用PriorityQueue实现的
 * 
 * @author rui
 *
 */
public class RetryDelayed implements Delayed {

	private RetryEntity retryEntity; // 重试对象
	private int interval;// 当前延时
	private long startTime;// 执行时间
	private String taskId;// 冗余retryEntity
	private int resume;// 重启恢复次数

	public RetryDelayed() {
	}

	public RetryDelayed(RetryEntity retryEntity, int interval) {
		this.retryEntity = retryEntity;
		this.taskId = retryEntity.getTaskId();
		this.interval = interval;
		this.startTime = System.currentTimeMillis() + interval * 1000L;
	}

	public RetryEntity getRetryEntity() {
		return retryEntity;
	}

	public void resume() {
		this.interval = 0;
		this.startTime = 0;
		this.resume++;
	}

	public int getResume() {
		return resume;
	}

	public void setResume(int resume) {
		this.resume = resume;
	}

	public int getInterval() {
		return interval;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setRetryEntity(RetryEntity retryEntity) {
		this.retryEntity = retryEntity;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public int compareTo(Delayed other) {
		if (other == this) {
			return 0;
		}
		if (other instanceof RetryDelayed) {
			RetryDelayed otherTask = (RetryDelayed) other;
			long otherStartTime = otherTask.getStartTime();
			return (int) (this.startTime - otherStartTime);
		}
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + interval;
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RetryDelayed other = (RetryDelayed) obj;
		if (interval != other.interval)
			return false;
		if (startTime != other.startTime)
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}
}
