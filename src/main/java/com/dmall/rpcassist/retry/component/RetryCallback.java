package com.dmall.rpcassist.retry.component;

import com.dmall.rpcassist.retry.core.RetryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 重试完成后的回调逻辑，可由调用方自定义实现
 * 
 * @author rui
 *
 */
public class RetryCallback {

	private final static Logger logger = LoggerFactory.getLogger(RetryCallback.class);
	/**
	 * 最终失败了的时候会回调，只一次
	 */
	public void retryFailed(RetryEntity retryEntity) {
		String desc = null;
		if (retryEntity != null) {
			desc = retryEntity.toString();
		}
		logger.info("[retryFailed execute] {}", desc);
	}

	/**
	 * 最终成功了的时候会回调，只一次
	 */
	public void retrySucceeded(RetryEntity retryEntity) {
		String desc = null;
		if (retryEntity != null) {
			desc = retryEntity.toString();
		}
		logger.info("[retrySucceeded execute] {}", desc);
	}

}
