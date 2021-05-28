package pl.mptasinski.weather.service.handler;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.mptasinski.weather.service.exception.InvalidDateException;
import pl.mptasinski.weather.service.service.WeatherService;
import reactor.core.publisher.Mono;
import reactor.util.annotation.NonNull;

@Component
@Slf4j
@AllArgsConstructor
public class WeatherHandler {

  private static final String DATE_PATH_VARIABLE = "date";
  private static final int TOP_ALLOWED_DATE_RANGE = 15;

  private final WeatherService weatherService;

  @NonNull
  public Mono<ServerResponse> handleFindBestWeather(final ServerRequest serverRequest) {
    final var requestedDate = serverRequest.pathVariable(DATE_PATH_VARIABLE);

    return Mono.just(requestedDate)
        .map(LocalDate::parse)
        .doOnError(error -> log
            .error("Error occurred while parsing requested date: {}, expectedFormat: yyyy-mm-dd",
                requestedDate))
        .onErrorResume(error -> Mono.error(new InvalidDateException(requestedDate, error)))
        .doOnNext(this::checkIfDateIsInValidRange)
        .flatMap(weatherService::findBestPlaceToSurf)
        .flatMap(bestPlaceToSurf ->
            ServerResponse.ok().bodyValue(bestPlaceToSurf));
  }

  private void checkIfDateIsInValidRange(final LocalDate requestedDate) {
    final var now = LocalDate.now();
    if (requestedDate.isAfter(now.plusDays(TOP_ALLOWED_DATE_RANGE)) || requestedDate
        .isBefore(now)) {
      log.error("Requested date is not within an allowed range, requested date: {}", requestedDate);
      throw new InvalidDateException(requestedDate.toString());
    }
  }

}
