package com.dmall.rpcassist.rpc;

import java.io.Serializable;

/**
 * Created by zhaoyong on 2017/5/12.
 * 使用优惠券的入参
 */
public class CouponUseParam implements Serializable {

	private String couponId;

	private boolean expectRes; // 本次使用优惠券期望值，用于单元测试方便

	public String getCouponId() {
		return couponId;
	}

	public void setCouponId(String couponId) {
		this.couponId = couponId;
	}

	public boolean isExpectRes() {
		return expectRes;
	}

	public void setExpectRes(boolean expectRes) {
		this.expectRes = expectRes;
	}
}
