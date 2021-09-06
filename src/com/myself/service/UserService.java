package com.myself.service;

import com.spring.Autowried;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.InitializingBean;
import com.spring.Scope;

@Component("userService")
@Scope("singleton")
public class UserService implements InitializingBean,UserInterface,BeanNameAware{
	
	@Autowried
	private OrderService orderService;
	
	public void test() {
		System.out.print("hello spring "+orderService);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("初始化");
	}

	@Override
	public void setBeanName(String name) {
		System.out.println(name);
		// TODO Auto-generated method stub
		
	}

}
