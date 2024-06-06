package kpi.zaranik.kexitdrive.gateway;

import java.net.URL;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("kexit-core", r -> r
				.path("/api/**")
				.filters(filters -> filters
					.filter(this::setXForwardHeadersFilter)
					.rewritePath("/api/(?<segment>.*)", "/${segment}")
				)
				.uri("http://localhost:8090") // lb://kexit-core
			)
			.route("frontend", r -> r.order(100)
				.path("/**")
				.uri("http://localhost:5173")
			)
			.build();
	}

	@SneakyThrows
	private Mono<Void> setXForwardHeadersFilter(ServerWebExchange exchange, GatewayFilterChain chain) {
		URL url = exchange.getRequest().getURI().toURL();

		String gatewayHost = url.getHost();
		int gatewayPort = url.getPort();
		String gatewayProtocol = url.getProtocol();

		ServerHttpRequest request = exchange.getRequest()
			.mutate()
			.header("X-Forwarded-Host", gatewayHost)
			.header("X-Forwarded-Proto", gatewayProtocol)
			.header("X-Forwarded-Port", String.valueOf(gatewayPort))
			.build();

		ServerWebExchange exchangeMutated = exchange.mutate().request(request).build();
		return chain.filter(exchangeMutated);
	}

}
