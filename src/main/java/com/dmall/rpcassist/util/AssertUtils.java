package com.dmall.rpcassist.util;

/**
 * @desc 断言相关工具类
 *
 * @author zhaoyong
 *
 * @date 2017/5/12 9:45
 */
public class AssertUtils {

	/**
	 * 判断非空
	 * @param message
	 */
	public static void notNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void notNull(Object... objects) {
		if (objects == null || objects.length <= 0) {
			return;
		}
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				throw new IllegalArgumentException("第" + (i + 1) + "个对象不能为空");
			}
		}
	}

}
