package com.dmall.rpcassist.retry.core;

import com.dmall.rpcassist.retry.component.RetryCallback;
import com.dmall.rpcassist.retry.component.RetryExecutor;
import com.dmall.rpcassist.util.AssertUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * 重试对象基本信息
 * @author rui
 */
public class RetryEntity {

	private static final int[] DEFAULT_INTERVALS = new int[] { // 默认重试间隔
			5,
			10,
			30,
			1 * 60,
			5 * 60
//			10 * 60,
//			30 * 60,
//			60 * 60,
//			2 * 3600,
//			5 * 3600,
//			10 * 600,
//			15 * 3600,
//			24 * 3600
	};

	private String remark; // 备注，如优惠券、余额等文本

	private RetryExecutor executor; // 重试执行的逻辑

	private Object param; // 重试入参

	private RetryCallback callback; // 重试后的回调服务

	/********** 以下字段无需强制调用方指定，有默认值 **********/

	private String taskId; // 时间戳唯一标识，格式：六位随机字母字符串-时间戳

	private int[] intervals; // 重试间隔

	int retryTimes = 1; // 当前重试次数


	public RetryEntity(RetryExecutor executor, Object param, RetryCallback callback) {
		AssertUtils.notNull(executor, param, callback);
		this.taskId = getRandomString(6) + "-" + new Date().getTime();
		this.executor = executor;
		this.param = param;
		this.callback = callback;
		this.intervals = DEFAULT_INTERVALS;
	}

	public RetryEntity(String taskId, RetryExecutor executor, Object param, RetryCallback callback) {
		AssertUtils.notNull(taskId, executor, param, callback);
		this.taskId = taskId;
		this.executor = executor;
		this.param = param;
		this.callback = callback;
		this.intervals = DEFAULT_INTERVALS;
	}

	public RetryEntity(String taskId, RetryExecutor executor, Object param, RetryCallback callback, int[] intervals) {
		AssertUtils.notNull(taskId, executor, param, callback, intervals);
		this.taskId = taskId;
		this.executor = executor;
		this.param = param;
		this.callback = callback;
		if (null != intervals && intervals.length > 0) {
			Arrays.sort(intervals);
			this.intervals = intervals;
		} else {
			this.intervals = DEFAULT_INTERVALS;
		}
	}

	public RetryEntity(String taskId, RetryExecutor executor, Object param, RetryCallback callback, String remark) {
		AssertUtils.notNull(taskId, executor, param, callback, remark);
		this.taskId = taskId;
		this.executor = executor;
		this.param = param;
		this.callback = callback;
		this.remark = remark;
		this.intervals = DEFAULT_INTERVALS;
	}

	public RetryEntity(String taskId, RetryExecutor executor, Object param, RetryCallback callback, int[] intervals, String remark) {
		AssertUtils.notNull(taskId, executor, param, callback, intervals);
		this.taskId = taskId;
		this.executor = executor;
		this.param = param;
		this.callback = callback;
		this.remark = remark;
		if (null != intervals && intervals.length > 0) {
			Arrays.sort(intervals);
			this.intervals = intervals;
		} else {
			this.intervals = DEFAULT_INTERVALS;
		}
	}

	private static String getRandomString(int count) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < count; i++) {
			sb.append("abcdefghijklmnopqrstuvwxyz".charAt((int) Math.round(Math.random() * (25))));
		}
		return sb.toString();
	}

	public int getNextTime() {
		if (this.retryTimes >= this.intervals.length) {
			return -1;
		}
		return intervals[this.retryTimes];
	}

	public String getTaskId() {
		return taskId;
	}

	public int[] getIntervals() {
		return intervals;
	}

	public int getRetryTimes() {
		return retryTimes;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public void setRetryTimes(int retryTimes) {
		this.retryTimes = retryTimes;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public void setIntervals(int[] intervals) {
		this.intervals = intervals;
	}

	public RetryExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(RetryExecutor executor) {
		this.executor = executor;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public RetryCallback getCallback() {
		return callback;
	}

	public void setCallback(RetryCallback callback) {
		this.callback = callback;
	}
}
