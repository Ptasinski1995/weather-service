package pl.mptasinski.weather.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WeatherbitIntegrationException extends ResponseStatusException {

  private static final String WEATHERBIT_INTEGRATION_MESSAGE_PATTERN = "Integration with Weatherbit API has failed, error: %s";

  public WeatherbitIntegrationException(Throwable cause) {
    super(HttpStatus.INTERNAL_SERVER_ERROR, String.format(WEATHERBIT_INTEGRATION_MESSAGE_PATTERN, cause.getMessage()), cause);

  }
}
