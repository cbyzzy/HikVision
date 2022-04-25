package com.github.cbyzzy;

import com.github.cbyzzy.common.CommonKit;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ServletComponentScan
@EnableAsync
@ComponentScan(nameGenerator = CameraApplication.SpringBeanNameGenerator.class)
@MapperScan("com.**.mapper")
public class CameraApplication {

	public static class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
		@Override
		protected String buildDefaultBeanName(BeanDefinition definition) {
			return definition.getBeanClassName();
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(CameraApplication.class, args);
	}


}
