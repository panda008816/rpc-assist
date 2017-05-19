package com.dmall.rpcassist;

import com.dmall.rpcassist.rpc.CouponService;
import com.dmall.rpcassist.rpc.CouponUseParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * 优惠券使用的测试，验证如果使用发生异常，是否正常回滚
 * Created by zhaoyong on 2017/5/12.
 */
@ContextConfiguration(locations = "classpath:com/dmall/rpcassist/rpc/spring-rpc-test.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class CouponServiceTest {

	private Logger logger = LoggerFactory.getLogger(Logger.class);

	@Resource
	CouponService couponService;

	@Test
	public void test1() throws InterruptedException {
		CouponUseParam param = new CouponUseParam();
		param.setCouponId("DMMJ0000008");
		param.setExpectRes(false); // 测试优惠券使用失败的场景
		boolean res = couponService.consume(param);
		logger.info("优惠券使用结果：" + res);
		if (!res) { // 休眠60s，保证观察到回滚的效果
			Thread.sleep(60 * 1000L);
		}
	}

}
