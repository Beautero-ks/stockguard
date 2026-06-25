package dev.bops.stockguard.shared.infrastructure;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret = "!Fv=_q$oJ!vQFRvlUUo^$f:5<34wCGYARX<nW=4ofNz";
    private long expirationMs = 86_400_000; // 24 heures
}