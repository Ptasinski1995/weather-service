package pl.mptasinski.weather.service.integration.weatherbit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import pl.mptasinski.weather.service.config.CityConfig;
import pl.mptasinski.weather.service.config.WeatherbitConfig;
import pl.mptasinski.weather.service.exception.WeatherbitIntegrationException;
import pl.mptasinski.weather.service.integration.weatherbit.dto.WeatherForecast;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherbitClient {

  private static final String KEY_PARAM = "key";
  private static final String CITY_PARAM = "city";
  private static final String COUTNRY_PARAM = "country";
  private static final String LAT_PARAM = "lat";
  private static final String LON_PARAM = "lon";


  private final WeatherbitConfig weatherbitConfig;
  private final WebClient webClient;

  public Mono<WeatherForecast> fetchWeatherForecast(final CityConfig cityConfig) {
    return webClient.get().uri(uriBuilder -> uriBuilder.path(weatherbitConfig.getUrl())
            .path(weatherbitConfig.getForecastPath())
            .queryParam(KEY_PARAM, weatherbitConfig.getKey())
            .queryParam(CITY_PARAM, cityConfig.getCity())
            .queryParam(COUTNRY_PARAM, cityConfig.getCountry())
            .build())
        .retrieve()
        .bodyToMono(WeatherForecast.class)
        .switchIfEmpty(makeFallbackCall(cityConfig))
        .onErrorResume(error -> Mono.error(new WeatherbitIntegrationException(error)));
  }

  //This part was a bit tricky for me, it looks like Weatherbit doesn't return anything for Le Morne in Mauritius.
  //Normally, I would double check the requirements with PO, since I don't have one here, I decided to make a business decision
  //and look for the nearest place based on latitude and longitude
  private Mono<WeatherForecast> makeFallbackCall(CityConfig cityConfig) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder.path(weatherbitConfig.getUrl())
            .path(weatherbitConfig.getForecastPath())
            .queryParam(KEY_PARAM, weatherbitConfig.getKey())
            .queryParam(LAT_PARAM, cityConfig.getLatitude())
            .queryParam(LON_PARAM, cityConfig.getLongitude())
            .build())
        .retrieve()
        .bodyToMono(WeatherForecast.class);
  }

}
