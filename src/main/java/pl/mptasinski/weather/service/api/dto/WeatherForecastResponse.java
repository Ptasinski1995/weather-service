package pl.mptasinski.weather.service.api.dto;

import java.time.LocalDate;
import pl.mptasinski.weather.service.integration.weatherbit.dto.DailyWeatherData;
import pl.mptasinski.weather.service.integration.weatherbit.dto.WeatherForecast;

public record WeatherForecastResponse(
    String city,
    String country,
    LocalDate date,
    Double windSpeed,
    Double temperature
) {

  private static final double MIN_SUITABLE_TEMPERATURE = 5;
  private static final double MAX_SUITABLE_TEMPERATURE = 35;
  private static final double MIN_SUITABLE_WIND_SPEED = 5;
  private static final double MAX_SUITABLE_WIND_SPEED = 18;

  public static WeatherForecastResponse prepareDatedWeatherForecast(WeatherForecast weatherForecast) {
    final var weatherForecastData = weatherForecast.data().stream().findFirst();
    final var windSpeed = weatherForecastData
        .map(DailyWeatherData::windSpeed)
        .orElse(null);
    final var temperature = weatherForecastData
        .map(DailyWeatherData::temperature)
        .orElse(null);

    final var date = weatherForecastData
        .map(DailyWeatherData::date)
        .orElse(null);

    return new WeatherForecastResponse(weatherForecast.cityName(), weatherForecast.country(), date,
        windSpeed, temperature);
  }

  public static boolean isSuitableForSurfing(WeatherForecastResponse weatherForecastResponse) {
    return !(weatherForecastResponse.temperature() > MAX_SUITABLE_TEMPERATURE)
        && !(weatherForecastResponse.temperature() < MIN_SUITABLE_TEMPERATURE)
        && !(weatherForecastResponse.windSpeed() > MAX_SUITABLE_WIND_SPEED)
        && !(weatherForecastResponse.windSpeed() < MIN_SUITABLE_WIND_SPEED);
  }
}


