package com.example.travelcompass;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Travel Compass 애플리케이션의 최상위 진입 클래스.
 * {@code @SpringBootApplication}이 붙은 클래스가 있는 패키지(com.example.travelcompass)를 기준으로
 * 하위 패키지(controller, service, mapper 등)를 자동으로 스캔하여 빈(Bean)으로 등록한다.
 * 즉, 이 클래스가 스프링부트 프로젝트 전체를 실행시키는 시작점(부트스트랩) 역할을 한다.
 */
// 애플리케이션 시작점 (Spring Boot 부트스트랩)
// @SpringBootApplication은 아래 3개 어노테이션을 합쳐 놓은 것과 같다.
// 1) @Configuration : 이 클래스도 스프링 설정 클래스로 사용될 수 있음
// 2) @EnableAutoConfiguration : 클래스패스에 있는 라이브러리를 보고 필요한 설정을 자동으로 구성
// 3) @ComponentScan : @Controller, @Service, @Repository 등이 붙은 클래스를 찾아 빈으로 등록
@SpringBootApplication
public class TravelcompassApplication {

	/**
	 * JVM이 프로그램을 실행할 때 가장 먼저 호출하는 메서드.
	 * 스프링 부트 애플리케이션을 실제로 구동(내장 톰캣 서버 기동, 빈 초기화 등)시킨다.
	 *
	 * @param args 커맨드라인 실행 인자 (예: java -jar app.jar --server.port=8081)
	 */
	public static void main(String[] args) {
		// SpringApplication.run()이 내부적으로 스프링 컨테이너(ApplicationContext)를 생성하고
		// 내장 웹서버(기본 Tomcat)를 띄운 뒤, 등록된 빈들을 초기화한다.
		SpringApplication.run(TravelcompassApplication.class, args);
	}

}
