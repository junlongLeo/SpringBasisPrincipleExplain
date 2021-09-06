package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.myself.service.AppConfig;

public class SelfDefinationApplicationContext {

	private Class configClass;

	private Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

	private Map<String, Object> singletonBeanMap = new HashMap<>();
	
	private List<BeanPostProcessor> beanPostProcessList = new ArrayList<>();

	public SelfDefinationApplicationContext(Class configClazz) {
		this.configClass = configClazz;

		// 扫描
		scan(configClazz);

		// 创建
		for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
			String beanName = entry.getKey();
			BeanDefinition beanDefinition = entry.getValue();
			if (beanDefinition.getScope().equals("singleton")) {
				Object bean = createBean(beanName, beanDefinition);
				singletonBeanMap.put(beanName, bean);
			}
		}
	}

	public Object getBean(String beanName) {
		if (!beanDefinitionMap.containsKey(beanName)) {
			throw new NullPointerException();
		}

		BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);

		if (beanDefinition.getScope().equals("singleton")) {

			Object singletonBean = singletonBeanMap.get(beanName);
			// 去除类的实例顺序导致的null
			if (singletonBean == null) {
				singletonBean = createBean(beanName, beanDefinition);
				singletonBeanMap.put(beanName, singletonBean);
			}
			return singletonBean;
		} else {
			// prototype
			Object prototypeBean = createBean(beanName, beanDefinition);
			return prototypeBean;
		}

	}

	private Object createBean(String beanName, BeanDefinition beanDefinition) {
		// TODO Auto-generated method stub
		Class clazz = beanDefinition.getType();
		Object instance = null;
		try {
			// 根据无参构造方法 创建类实例
			instance = clazz.getConstructor().newInstance();

			// 完成类里面的依赖注入
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Autowried.class)) {
					field.setAccessible(true);
					// using beanName 有循环调用的问题
					field.set(instance, getBean(field.getName()));
				}
			}
			
			//回调
			if(instance instanceof BeanNameAware) {
				((BeanNameAware)instance).setBeanName(beanName);
			}
			
			//初始化
			if (instance instanceof InitializingBean) {
				//instance = (InitializingBean) instance;
				((InitializingBean) instance).afterPropertiesSet();
			}
			
			//初始化后
			for (BeanPostProcessor beanPostProcessor : beanPostProcessList) {
			     instance = beanPostProcessor.postProcessAfterInitialization(instance, beanName);
			}
			
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return instance;
	}

	private void scan(Class configClazz) {
		if (configClazz.isAnnotationPresent(ComponentScan.class)) {
			ComponentScan componentScanAnnotation = (ComponentScan) configClazz.getAnnotation(ComponentScan.class);
			String path = componentScanAnnotation.value();
			path = path.replace(".", "/");
			ClassLoader classLoader = SelfDefinationApplicationContext.class.getClassLoader();
			URL resource = classLoader.getResource(path);
			File file = new File(resource.getFile());
			if (file.isDirectory()) {
				for (File perFile : file.listFiles()) {
					String absolutePath = perFile.getAbsolutePath();
					absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
					absolutePath = absolutePath.replace("/", ".");
					//System.out.println(absolutePath);
					try {
						Class<?> clazz = classLoader.loadClass(absolutePath);
						BeanDefinition beanDefinition = new BeanDefinition();
						beanDefinition.setType(clazz);
						if (clazz.isAnnotationPresent(Component.class)) {
							
							if(BeanPostProcessor.class.isAssignableFrom(clazz)) {
								BeanPostProcessor newInstance = (BeanPostProcessor) clazz.getConstructor().newInstance();
								beanPostProcessList.add(newInstance);
							}

							Component componentAnnotation = clazz.getAnnotation(Component.class);
							String beanName = componentAnnotation.value();

							if ("".equals(beanName)) {
								beanName = Introspector.decapitalize(clazz.getSimpleName());
							}

							if (clazz.isAnnotationPresent(Scope.class)) {

								Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
								beanDefinition.setScope(scopeAnnotation.value());
							} else {
								beanDefinition.setScope("singleton");
							}

							beanDefinitionMap.put(beanName, beanDefinition);

						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}
	}

}
