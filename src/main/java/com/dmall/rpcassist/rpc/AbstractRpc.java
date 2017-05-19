package com.dmall.rpcassist.rpc;

import com.dmall.rpcassist.retry.component.RetryCallback;
import com.dmall.rpcassist.retry.component.RetryExecutor;
import com.dmall.rpcassist.retry.core.RetryEntity;
import com.dmall.rpcassist.retry.core.RetryTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * rpc调用操作，自动实现失败后回滚重试
 * Created by zhaoyong on 2017/5/11.
 */
public abstract class AbstractRpc<REQ, RES> {

	private Logger logger = LoggerFactory.getLogger(AbstractRpc.class);

	private final static RetryTask retryTask = new RetryTask(); // 重试队列，用于回滚失败后的重试

	private String name; // 名称

	public AbstractRpc(String name) {
		this.name = name;
	}

	/**
	 * 执行业务请求，由调用方实现
	 */
	protected abstract RES commit(REQ request);

	/**
	 * 根据响应结果判断是否需要回滚
	 * @param response
	 * @return
	 */
	protected abstract boolean needRollback(REQ request, RES response);

	/**
	 * 回滚请求逻辑，由调用方实现
	 */
	protected abstract boolean rollback(REQ request, RES response);

	/**
	 * 最终回滚成功时需要做的操作
	 */
	protected void rollbackSucceeded(String taskId, REQ request, RES response) {
		logger.info("{} rollbackSucceeded!", taskId);
	}

	/**
	 * 最终回滚失败时需要做的操作，失败后建议做报警操作
	 */
	protected void rollbackFailed(String taskId, REQ request, RES response) {
		logger.info("{} rollbackFailed!", taskId);
	}

	/**
	 * 执行rpc请求调用
	 * @param request 请求参数
	 * @param rollbackIfCommitException 如果发生异常，是否进行回滚
	 */
	public final RES execute(REQ request, boolean rollbackIfCommitException) {
		RES response = null;
		boolean needRb = false; // 记录是否回滚
		try {
			response = commit(request); // 执行请求
			needRb = true;
		} catch (Exception e) {
			needRb = rollbackIfCommitException;
		}
		if (needRb) {
			addRollbackTask(request, response);
		}
		return response;
	}

	/**
	 * 添加回滚任务
	 */
	private void addRollbackTask(REQ request, RES response) {
		final String taskId = name + "失败的回滚任务" + "-" + System.currentTimeMillis(); // 使用微秒作为id后缀
		final REQ req = request;
		final RES res = response;
		// 回滚操作执行逻辑
		RetryExecutor<RES> retryExecutor = new RetryExecutor<RES>() {
			@Override
			public boolean execute(RES param) {
				return rollback(req, res);
			}
		};
		// 回滚完成后的执行逻辑
		RetryCallback retryCallback = new RetryCallback() {
			@Override
			public void retryFailed(RetryEntity retryEntity) {
				rollbackFailed(taskId, req, res);
			}
			@Override
			public void retrySucceeded(RetryEntity retryEntity) {
				rollbackSucceeded(taskId, req, res);
			}
		};
		RetryEntity retryEntity = new RetryEntity(taskId, retryExecutor, response, retryCallback, name);
		retryTask.add(retryEntity); // 将回滚任务假如重试队列
	}

}
