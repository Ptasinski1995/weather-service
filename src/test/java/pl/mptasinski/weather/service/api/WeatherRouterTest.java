package pl.mptasinski.weather.service.api;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import pl.mptasinski.weather.service.api.dto.WeatherForecastResponse;
import pl.mptasinski.weather.service.integration.weatherbit.dto.DailyWeatherData;
import pl.mptasinski.weather.service.integration.weatherbit.dto.WeatherForecast;

@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(properties = {"weatherbit.url=localhost:8081", "weatherbit.key=12345"})
@AutoConfigureWebTestClient
class WeatherRouterTest {

  @Autowired
  private WebTestClient webTestClient;

  private ObjectMapper objectMapper;
  private WireMockServer wireMockServer;

  @BeforeAll
  public void setup() {
    wireMockServer = new WireMockServer(8081);
    wireMockServer.start();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @AfterAll
  public void cleanUp() {
    wireMockServer.stop();
  }

  @Test
  void shouldReturnBestPlaceToSurf() throws JsonProcessingException {
    final var date = LocalDate.now().plusDays(4);
    mockResponse("Jastarnia", "PL", 20, 7.12);
    mockResponse("Bridgetown", "BB", 30, 12.62);
    mockResponse("Pissouri", "CY", 22, 3.6);
    mockResponse("Fortaleza", "BR", 18, 18.5);
    mockResponse("Le Morne", "MU", 30, 10.34);

    webTestClient.get()
        .uri("/bestWeather/{date}", date)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .json(expectedResponse(date));
  }

  @Test
  void shouldReturn500StatusCodeWhenWeatherbitCallFails() {
    final var date = LocalDate.now().plusDays(4);
    mockExceptionResponse();

    webTestClient.get()
        .uri("/bestWeather/{date}", date)
        .exchange()
        .expectStatus()
        .is5xxServerError();
  }

  @Test
  void shouldReturnInvalidDateExcpetionWhenFormatIsBroken() {
    var invalidRequestedDate = "2021-05-1";
    webTestClient.get()
        .uri("/bestWeather/{date}", invalidRequestedDate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void shouldReturnInvalidDateExcpetionWhenDateIsNotInValidRange() {
    var invalidRequestedDate = LocalDate.now().plusDays(16);
    webTestClient.get()
        .uri("/bestWeather/{date}", invalidRequestedDate)
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  void mockResponse(final String city, final String country, final double temp, final double wind)
      throws JsonProcessingException {
    wireMockServer.stubFor(get(urlMatching("^.*/forecast/daily.*$"))
        .withQueryParams(getCityCountryParams(city, country))
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(weatherBitResponse(city, country, temp, wind))));

    wireMockServer.stubFor(get(urlMatching("^.*/forecast/daily.*$"))
        .withQueryParams(getLatLongQueryParams())
        .willReturn(aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(weatherBitResponse("Flic en Flac", country, temp, wind))));
  }

  void mockExceptionResponse() {
    wireMockServer.stubFor(get(urlMatching("^.*/forecast/daily.*$"))
        .willReturn(serverError().withStatus(500)
            .withHeader("Content-Type", "application/json")));

  }

  private Map<String, StringValuePattern> getCityCountryParams(String city, String country) {
    return Map.of("city", equalTo(city),
        "country", equalTo(country));
  }

  private Map<String, StringValuePattern> getLatLongQueryParams() {
    return Map.of("lat", equalTo("20.27"),
        "lon", equalTo("57.18"));
  }

  private String expectedResponse(final LocalDate date) throws JsonProcessingException {
    final var expectedResponse = new WeatherForecastResponse("Fortaleza", "BR", date, 17.0, 25.2);

    return objectMapper.writeValueAsString(expectedResponse);
  }

  private byte[] weatherBitResponse(final String city, final String country, final double temp,
      final double wind)
      throws JsonProcessingException {
    final var weatherForecast = new WeatherForecast(
        dailyWeatherData(temp, wind),
        city,
        country
    );

    return objectMapper.writeValueAsBytes(weatherForecast);
  }

  private List<DailyWeatherData> dailyWeatherData(double temp, double wind) {
    final var date = LocalDate.now();
    return Arrays.asList(
        new DailyWeatherData(date, temp, wind),
        new DailyWeatherData(date.plusDays(1), temp + 0.5, wind + 2),
        new DailyWeatherData(date.plusDays(2), temp - 0.5, wind + 1.5),
        new DailyWeatherData(date.plusDays(3), temp + 1, wind - 2.2),
        new DailyWeatherData(date.plusDays(4), temp - 1, wind + 6.7),
        new DailyWeatherData(date.plusDays(5), temp + 1.5, wind + 20),
        new DailyWeatherData(date.plusDays(6), temp + 2, wind + 18),
        new DailyWeatherData(date.plusDays(7), temp + 4, wind + 21)
    );
  }
}
