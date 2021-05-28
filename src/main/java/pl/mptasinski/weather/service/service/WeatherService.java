package pl.mptasinski.weather.service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse;
import pl.mptasinski.weather.service.exception.NoSuitableWeatherFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

  private static final double FORMULA_WIND_MULTIPLIER = 3;

  private final WeatherbitService weatherbitService;

  public Mono<WeatherForecastResponse> findBestPlaceToSurf(LocalDate requestedDate) {
    return weatherbitService.fetchWeatherForecast(requestedDate)
        .doOnNext(next ->
            log.info("City: {}, country: {}, temperature: {}, wind: {}", next.city(), next.country(),
                next.temperature(), next.windSpeed()))
        .filter(WeatherForecastResponse::isSuitableForSurfing)
        .switchIfEmpty(Flux.error(new NoSuitableWeatherFoundException(requestedDate)))
        .sort(Comparator.comparingDouble(this::calculateSuitabilityCoefficient))
        .last();
  }

  private double calculateSuitabilityCoefficient(WeatherForecastResponse weatherForecastResponse) {
    return BigDecimal.valueOf(weatherForecastResponse.windSpeed())
        .multiply(BigDecimal.valueOf(FORMULA_WIND_MULTIPLIER))
        .add(BigDecimal.valueOf(weatherForecastResponse.temperature()))
        .doubleValue();
  }
}
