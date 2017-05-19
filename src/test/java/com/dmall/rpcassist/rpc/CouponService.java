package com.dmall.rpcassist.rpc;

/**
 * 使用优惠券接口
 * Created by zhaoyong on 2017/5/12.
 */
public interface CouponService {

	/**
	 * 使用优惠券
	 * @return
	 */
	public boolean consume(CouponUseParam param);

}
