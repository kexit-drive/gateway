package kpi.zaranik.kexitdrive.gateway.config.properties;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("service")
public record ServiceProperties(
    Map<String, String> url
) {

}
