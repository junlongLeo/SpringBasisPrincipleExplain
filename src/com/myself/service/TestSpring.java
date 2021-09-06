package com.myself.service;

import com.spring.SelfDefinationApplicationContext;

public class TestSpring {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 扫描 -- 创建单例bean
		SelfDefinationApplicationContext applicationContext = new SelfDefinationApplicationContext(AppConfig.class);
		UserInterface userservice = (UserInterface)applicationContext.getBean("userService");
		//UserService userservice1 = (UserService)applicationContext.getBean("userService");
		//OrderService orderService = (OrderService)applicationContext.getBean("orderService");
		//OrderService orderService1 = (OrderService)applicationContext.getBean("orderService");
		//System.out.println(orderService);
		//System.out.println(orderService1);
		//System.out.println(userservice);
		userservice.test();

	}

}
