package pl.mptasinski.weather.service.integration.weatherbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record WeatherForecast(
    List<DailyWeatherData> data,
    @JsonProperty("city_name") String cityName,
    @JsonProperty("country_code") String country
) {}
