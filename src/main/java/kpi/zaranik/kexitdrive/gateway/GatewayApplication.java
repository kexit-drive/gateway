package kpi.zaranik.kexitdrive.gateway;

import kpi.zaranik.kexitdrive.gateway.config.properties.ServiceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder, ServiceProperties serviceProperties) {
		return builder.routes()
			.route("kexit-core", r -> r
				.path("/api/**")
				.filters(filters -> filters.rewritePath("/api/(?<segment>.*)", "/${segment}"))
				.uri(serviceProperties.url().get("kexit-core"))
			)
			.route("frontend", r -> r.order(100)
				.path("/**")
				.uri(serviceProperties.url().get("frontend"))
			)
			.build();
	}

}
