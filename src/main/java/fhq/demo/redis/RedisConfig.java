package fhq.demo.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author fhq
 * @date 2019/12/20 14:15
 */
@PropertySource("classpath:customer.properties")
@ConfigurationProperties(prefix = "fhq.demo.redis")
@Component
@Getter
@Setter
public class RedisConfig {
    private String host;
    private int port;
    private String password;
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
    private int timeout;
}
