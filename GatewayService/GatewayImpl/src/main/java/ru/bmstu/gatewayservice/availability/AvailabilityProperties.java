package ru.bmstu.gatewayservice.availability;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "manage")
@Getter @Setter
public class AvailabilityProperties {
    Map<String, String> urls;
}
