package com.dmall.rpcassist.retry.core;


import com.dmall.rpcassist.retry.component.RetryCallback;
import com.dmall.rpcassist.retry.component.RetryDataPersistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.DelayQueue;

/**
 * 重试主任务
 * 
 * @author rui.wang
 */
public class RetryTask {

	protected DelayQueue<RetryDelayed> delayQueue = new DelayQueue<RetryDelayed>();

	private static final Logger log = LoggerFactory.getLogger(RetryTask.class);

	private boolean start = false;

	private RetryDataPersistService<RetryDelayed> persistService;

	public RetryTask() {

	}

	public RetryTask(RetryDataPersistService<RetryDelayed> persistService) {
		this.persistService = persistService;
	}

	/**
	 * 如果存在未执行完毕的任务，应用启动以后调用一次
	 * 
	 **/

	@PostConstruct
	public void checkCache() {
		if (persistService == null) {
			return;
		}
		List<RetryDelayed> list = persistService.getAll();
		log.info("[RetryTask] checkCache size " + (null != list ? list.size() : 0) + " ...");
		if (null != list && list.size() > 0) { // 如果启动时，缓存中有待重试任务
			long now = new Date().getTime();
			for (RetryDelayed retryDelayed : list) {
				if (retryDelayed.getStartTime() < now) { // 重启恢复任务，如果延时时间大于了当前时间，直接开始
					retryDelayed.resume();
				}
				this.add(retryDelayed);
			}
			this.start();
		}
	}

	public void start() {
		if (start) {
			return;
		}
		log.info("[RetryTask] start at " + new SimpleDateFormat("YY-MM-dd HH:mm:ss.SSS").format(new Date()) + " ...");
		start = true;
		new Thread(new Runnable() {
			public void run() {
				while (start) {
					try {
						// DelayQueue的take方法，把优先队列拿出来（peek），如果没有达到延时阀值，则进行await处理
						final RetryDelayed task = delayQueue.take();
						if (task != null) {
							RetryPool.execute(new Runnable() {
								public void run() {
									retry(task);
								}
							});
							if (persistService != null) {
								persistService.delete(task);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void retry(RetryDelayed retryDelayed) {
		RetryEntity retryEntity = retryDelayed.getRetryEntity();
		RetryCallback callbackService = retryEntity.getCallback();
		boolean success = false;
		String taskId = null;
		// 执行重试调用
		try {
			taskId = retryDelayed.getTaskId();
			success = retryEntity.getExecutor().execute(retryEntity.getParam());
		} catch (Exception e) {
			log.error("excute retryTask[" + taskId + "] has Exception.", e);
		}
		if (!success) {
			log.error("[RetryTask] " + retryDelayed.getRetryEntity().getTaskId() + " retry "
					+ retryDelayed.getRetryEntity().getRetryTimes() + " failed");
			RetryDelayed next = this.getNextTask(retryEntity);
			if (next != null) {
				add(next);
			} else {
				callbackService.retryFailed(retryEntity);
				remove(retryDelayed);
			}
		} else {
			log.info("[RetryTask] " + retryDelayed.getRetryEntity().getTaskId() + " retry "
					+ retryDelayed.getRetryEntity().getRetryTimes() + " succeeded");
			callbackService.retrySucceeded(retryEntity);
			remove(retryDelayed);
		}
	}

	public RetryDelayed getNextTask(RetryEntity retryEntity) {
		int[] interval = retryEntity.getIntervals();
		int retrytimes = retryEntity.getRetryTimes();
		if (retrytimes < interval.length) {
			retryEntity.setRetryTimes(retrytimes + 1);
			return new RetryDelayed(retryEntity, interval[retrytimes]);
		}
		return null;
	}

	/**
	 * 添加任务入口
	 * @param retryEntity
	 */
	public void add(RetryEntity retryEntity) {
		if (null == retryEntity) {
			return;
		}
		this.add(new RetryDelayed(retryEntity, retryEntity.getIntervals()[0]));
	}

	private void add(final RetryDelayed retryDelayed) {
		this.start();
		RetryPool.execute(new Runnable() {
			public void run() {
				delayQueue.put(retryDelayed);
				log.info("[RetryTask] add retry task: " + retryDelayed.getRetryEntity().getTaskId() + " [interval "
						+ retryDelayed.getInterval() + "], current retry queue size: " + size());
				if (persistService != null) {
					persistService.save(retryDelayed);
				}
			}
		});
	}

	public void remove(final RetryDelayed target) {
		RetryPool.execute(new Runnable() {
			public void run() {
				if (target == null) {
					return;
				}
				delayQueue.remove(target);
				log.info("[RetryTask] remove retry: " + target.getRetryEntity().getTaskId() + " [interval "
						+ target.getInterval() + "], current retry queue size: " + size());
			}
		});
	}

	public int size() {
		return delayQueue.size();
	}

	public boolean isStart() {
		return start;
	}
}
