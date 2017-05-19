package com.dmall.rpcassist.retry.component;

import com.dmall.rpcassist.retry.core.RetryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @desc 由调用方定义重试的执行逻辑
 *
 * @author zhaoyong
 *
 * @date 2017/5/12 9:17
 */
public interface RetryExecutor<T> {

	/**
	 * 重试的执行逻辑
	 */
	boolean execute(T param);

}
