package pl.mptasinski.weather.service.service

import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse
import pl.mptasinski.weather.service.exception.NoSuitableWeatherFoundException
import pl.mptasinski.weather.service.util.TestData
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import spock.lang.Specification

import java.time.LocalDate

class WeatherServiceSpec extends Specification implements TestData {

    WeatherbitService weatherbitService
    WeatherService weatherService

    def setup() {
        weatherbitService = Mock(WeatherbitService)
        weatherService = new WeatherService(weatherbitService)
    }

    def "should return best place to surf based on coefficient"() {
        given:
        def requestedDate = LocalDate.now()
        weatherbitService.fetchWeatherForecast(requestedDate) >> Flux.fromIterable(listOfDatedWeatherForecast())

        when:
        def result = weatherService.findBestPlaceToSurf(requestedDate)

        then:
        StepVerifier.create(result)
                .expectNext(new WeatherForecastResponse("Bridgetown", "BB", requestedDate, 7, 15))
                .verifyComplete()
    }

    def "should return NoSuitableWeatherFoundException when there is no suitable weather"() {
        given:
        def requestedDate = LocalDate.now()
        weatherbitService.fetchWeatherForecast(requestedDate) >> Flux.fromIterable(listOfNotSuitableDatedWeatherForecast())

        when:
        def result = weatherService.findBestPlaceToSurf(requestedDate)

        then:
        StepVerifier.create(result)
                .verifyError(NoSuitableWeatherFoundException)
    }
}
