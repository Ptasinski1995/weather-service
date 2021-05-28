package pl.mptasinski.weather.service.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse;
import pl.mptasinski.weather.service.config.WeatherbitConfig;
import pl.mptasinski.weather.service.integration.weatherbit.WeatherbitClient;
import pl.mptasinski.weather.service.integration.weatherbit.dto.WeatherForecast;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class WeatherbitService {

  private final WeatherbitClient weatherbitClient;
  private final WeatherbitConfig weatherbitConfig;

  public Flux<WeatherForecastResponse> fetchWeatherForecast(final LocalDate requestedDate) {
    return Flux.fromIterable(weatherbitConfig.getCities())
        .flatMap(weatherbitClient::fetchWeatherForecast)
        .map(weatherForecast -> filterNotNeededData(weatherForecast, requestedDate))
        .map(WeatherForecastResponse::prepareDatedWeatherForecast);
  }

  private WeatherForecast filterNotNeededData(final WeatherForecast weatherForecast,
      final LocalDate requestedDate) {
    weatherForecast.data()
        .removeIf(dailyWeatherData -> !dailyWeatherData.date().equals(requestedDate));

    return weatherForecast;
  }


}
