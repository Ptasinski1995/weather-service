package pl.mptasinski.weather.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidDateException extends ResponseStatusException {

  private static final String INVALID_FORMAT_MESSAGE_PATTERN = "Requested date: '%s', doesn't match the expected date pattern: yyyy-mm-dd";
  private static final String INVALID_RANGE_MESSAGE_PATTERN = "Only dates up to 15 days ahead and not before the current date are allowed, requestedDate: '%s'";

  public InvalidDateException(final String date, final Throwable cause) {
    super(HttpStatus.BAD_REQUEST, String.format(INVALID_FORMAT_MESSAGE_PATTERN, date), cause);
  }

  public InvalidDateException(final String date) {
    super(HttpStatus.BAD_REQUEST, String.format(INVALID_RANGE_MESSAGE_PATTERN, date));
  }

}
