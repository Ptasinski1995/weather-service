package pl.mptasinski.weather.service.integration.weatherbit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record DailyWeatherData(
    @JsonProperty("valid_date") LocalDate date,
    @JsonProperty("wind_spd") Double windSpeed,
    @JsonProperty("temp") Double temperature
) {}
