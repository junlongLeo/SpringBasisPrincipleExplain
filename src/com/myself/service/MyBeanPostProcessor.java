package com.myself.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.spring.BeanPostProcessor;
import com.spring.Component;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		// TODO Auto-generated method stub
		//System.out.println(beanName);
		if(beanName.equals("userService")){
			
			Object newProxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(), bean.getClass().getInterfaces(),new InvocationHandler() {
				
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					// TODO Auto-generated method stub
					System.out.println("生成代理类，切面逻辑");
					return method.invoke(bean, args);
				}
			});
			
			return newProxyInstance;
		}
		
		return bean;
		
	}

}
