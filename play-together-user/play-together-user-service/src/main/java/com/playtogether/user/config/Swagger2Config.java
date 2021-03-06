package com.playtogether.user.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 接口文档的配置。生成的文档通过 http://127.0.0.1:端口号/swagger-ui.html 访问
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Swagger2Config {

	@Bean
	public Docket weAbpiConfig(){

		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("userApi")
				.apiInfo(webApiInfo())
				.select()
				.paths(Predicates.not(PathSelectors.regex("/error.*")))
				.build();

	}

	private ApiInfo webApiInfo(){

		return new ApiInfoBuilder()
				.title("PlayTogether-用户中心接口文档")
				.description("本文档描述了用户中心微服务接口定义")
				.version("1.0")
				.contact(new Contact("关力斌", "https://github.com/mbcwdl/Defender", "programmerguan@163.com"))
				.build();
	}

}