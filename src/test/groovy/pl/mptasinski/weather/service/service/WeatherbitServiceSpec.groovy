package pl.mptasinski.weather.service.service

import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse
import pl.mptasinski.weather.service.config.CityConfig
import pl.mptasinski.weather.service.config.WeatherbitConfig
import pl.mptasinski.weather.service.exception.WeatherbitIntegrationException
import pl.mptasinski.weather.service.integration.weatherbit.WeatherbitClient
import pl.mptasinski.weather.service.util.TestData
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.LocalDate

class WeatherbitServiceSpec extends Specification implements TestData{

    WeatherbitConfig weatherbitConfig
    WeatherbitClient weatherbitClient
    WeatherbitService weatherbitService

    def setup() {
        weatherbitConfig = Mock(WeatherbitConfig)
        weatherbitClient = Mock(WeatherbitClient)

        weatherbitService = new WeatherbitService(weatherbitClient, weatherbitConfig)
    }

    def "should return forecast only for requested date"() {
        given:
        def requestedDate = LocalDate.now()
        weatherbitConfig.getCities() >> List.of(prepareCityConfig("Jastarnia", "PL"), prepareCityConfig("Bridgetown", "BB"), prepareCityConfig("Fortaleza", "BR"))
        weatherbitClient.fetchWeatherForecast(prepareCityConfig("Jastarnia", "PL")) >> Mono.just(weatherbitResponse(requestedDate, "PL", "Jastarnia", 1.5, 16))
        weatherbitClient.fetchWeatherForecast(prepareCityConfig("Bridgetown", "BB")) >> Mono.just(weatherbitResponse(requestedDate, "BB", "Bridgetown", 2, 15))
        weatherbitClient.fetchWeatherForecast(prepareCityConfig("Fortaleza", "BR")) >> Mono.just(weatherbitResponse(requestedDate, "BR", "Fortaleza", 4.61, 13))

        when:
        def result = weatherbitService.fetchWeatherForecast(requestedDate)

        then:
        StepVerifier.create(result)
                .expectNext(
                        new WeatherForecastResponse("Jastarnia", "PL", requestedDate, 1.5, 16),
                        new WeatherForecastResponse("Bridgetown", "BB", requestedDate, 2, 15),
                        new WeatherForecastResponse("Fortaleza", "BR", requestedDate, 4.61, 13)
                )
                .expectComplete()
                .verify()
    }

    def "should throw WeatherbitIntegrationException when integration with Weatherbit fails"() {
        given:
        def requestedDate = LocalDate.now()
        weatherbitConfig.getCities() >> List.of(prepareCityConfig("Jastarnia", "PL"))
        weatherbitClient.fetchWeatherForecast(_ as CityConfig) >> Mono.error(new WeatherbitIntegrationException(new RuntimeException("Error occurred while calling Weatherbit")))

        when:
        def result = weatherbitService.fetchWeatherForecast(requestedDate)

        then:
        StepVerifier.create(result)
                .expectError(WeatherbitIntegrationException)
                .verify()

    }
}
