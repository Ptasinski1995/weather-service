package pl.mptasinski.weather.service.exception;

import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoSuitableWeatherFoundException extends ResponseStatusException {

  private static final String NO_SUITABLE_WEATHER_FOUND_MESSAGE = "There is no suitable weather for windsurfing on requested date: '%s'";

  public NoSuitableWeatherFoundException(final LocalDate requestedDate) {
    super(HttpStatus.NOT_FOUND, String.format(NO_SUITABLE_WEATHER_FOUND_MESSAGE, requestedDate));
  }

}
