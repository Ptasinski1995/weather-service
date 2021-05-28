package pl.mptasinski.weather.service.util

import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse
import pl.mptasinski.weather.service.config.CityConfig
import pl.mptasinski.weather.service.integration.weatherbit.dto.DailyWeatherData
import pl.mptasinski.weather.service.integration.weatherbit.dto.WeatherForecast

import java.time.LocalDate

trait TestData {

    def prepareCityConfig(def country, def city) {
        def config = new CityConfig()
        config.city = city
        config.country = country

        config as CityConfig
    }

    def listOfDatedWeatherForecast() {
        def now = LocalDate.now()
        List.of(mappedWeatherbitResponse(now, "PL", "Jastarnia", 6.5, 16),
                mappedWeatherbitResponse(now, "BB", "Bridgetown", 7, 15),
                mappedWeatherbitResponse(now, "BR", "Fortaleza", 9.61, 6))
    }

    def listOfNotSuitableDatedWeatherForecast() {
        def now = LocalDate.now()
        List.of(mappedWeatherbitResponse(now, "PL", "Jastarnia", 1.5, 16),
                mappedWeatherbitResponse(now, "BB", "Bridgetown", 7, -1),
                mappedWeatherbitResponse(now, "BR", "Fortaleza", 20.61, 38))
    }

    def mappedWeatherbitResponse(LocalDate date, def country, def cityName, double windSpeed, double temperature) {
        new WeatherForecastResponse(cityName, country, date, windSpeed, temperature)
    }

    def weatherbitResponse(LocalDate date, def country, def cityName, double windSpeed, double temperature) {
        new WeatherForecast(
                dailyWeatherData(date, windSpeed, temperature),
                cityName,
                country
        )
    }

    def dailyWeatherData(def date, def windSpeed, def temperature) {
        Arrays.asList(
                new DailyWeatherData(date, windSpeed, temperature),
                new DailyWeatherData(date.plusDays(1), windSpeed + 1, temperature - 1),
                new DailyWeatherData(date.plusDays(2), windSpeed + 1.5, temperature - 1),
                new DailyWeatherData(date.plusDays(3), windSpeed + 2, temperature - 2),
                new DailyWeatherData(date.plusDays(4), windSpeed + 3, temperature - 3)
        ) as ArrayList
    }

}