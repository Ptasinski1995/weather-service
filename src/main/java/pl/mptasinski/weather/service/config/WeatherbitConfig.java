package pl.mptasinski.weather.service.config;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "weatherbit")
public class WeatherbitConfig {

  private String key;
  private String url;
  private String forecastPath;
  private List<CityConfig> cities;
}
