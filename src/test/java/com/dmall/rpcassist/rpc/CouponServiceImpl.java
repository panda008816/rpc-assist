package com.dmall.rpcassist.rpc;

import com.dmall.rpcassist.rpc.AbstractRpc;
import com.dmall.rpcassist.rpc.CouponService;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by zhaoyong on 2017/5/12.
 */
@Service
public class CouponServiceImpl extends AbstractRpc<CouponUseParam, Boolean> implements CouponService {

	public CouponServiceImpl() {
		super("优惠券使用");
	}

	@Override
	public boolean consume(CouponUseParam param) {
		return execute(param, true);
	}

	/**
	 * 执行优惠券的扣减
	 * @return
	 */
	@Override
	protected Boolean commit(CouponUseParam param) {
		return param.isExpectRes();
	}

	/**
	 * 根据返回结果判断是否需要回滚
	 * @param request
	 * @param response
	 * @return
	 */
	@Override
	protected boolean needRollback(CouponUseParam request, Boolean response) {
		if (response != null) {
			return !response;
		}
		return false;
	}

	/**
	 * 执行优惠券的回滚
	 * @param request
	 * @return
	 */
	@Override
	protected boolean rollback(CouponUseParam request, Boolean response) {
		// 通过随机数模拟是否成功
		Random random = new Random();
		if (random.nextInt(100) % 3 == 0) {
			return true;
		}
		return false;
	}

}
