package pl.mptasinski.weather.service.config;

import lombok.Data;

@Data
public final class CityConfig {

  private String city;
  private String country;
  private double latitude;
  private double longitude;


}
