package pl.mptasinski.weather.service.api;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import pl.mptasinski.weather.service.handler.WeatherHandler;

@Configuration
public class WeatherRouter {

  @Bean
  public RouterFunction<ServerResponse> routes(final WeatherHandler weatherHandler) {
    return route(GET("/bestWeather/{date}"),
        weatherHandler::handleFindBestWeather);
  }

}
