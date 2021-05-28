# Windsurfer's Weather App

The idea behind this application is to find the best place to surf on a given date based on 16 days
weather forecast. Currently, there are 5 cities configured: Jastarnia (PL), Bridgetown(BB),
Fortaleza(BR), Pissouri(CY), Le Morne(Mauritius). Algorithm to choose the best place is based on the
average temperature and average windSpeed -> temperature must be within range the of 5-35 and wind
speed within the range of 5-18. Coefficient is calculated with following formula: wind speed * 3 +
temperature.

Feel free to extend the list of supported cities (or maybe consume them on the API level) or change
the algorithm to better suits your need.

## Firstly

In order to work with this application clone the source using *git clone* or simply download and
unzip the sources

## Building application

To build the application use:
> ./gradlew build -x test

## Running tests

To run the tests use:
> ./gradlew test

Once the tests are executed you should have access to the test report with:
> open build/reports/tests/test/index.html

## Running application

To run the application you can either configure your IDE or use gradle:
> ./gradlew bootRun

Once the application has started you can test the behaviour using:
> curl --location --request GET 'localhost:8080/bestWeather/{date}'

Where {date} is an actual date of your choice, but up to the 15 days forwards (16 days forecast) and in the format: *yyyy-mm-dd*