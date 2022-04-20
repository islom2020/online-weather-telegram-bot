package weatherBot.service;

import apies.countriesApi.Country;
import apies.countriesApi.CountryApi;
import apies.weatherApi.WeatherApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.meta.api.objects.Location;
import weatherBot.WeatherInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ApiService implements WeatherInterface {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static String getWeatherInfoByWeatherApi(WeatherApi weatherApi) {
        if (weatherApi == null) {
            return "==CITY_NAME_IS_INCORRECT==";
        }
        return "" + new Date() + " \n" +
                weatherApi.name + ", " + weatherApi.sys.country +
                "\n" +
                toCelsius(weatherApi.main.temp) + "°C" +
                " Feels like " + toCelsius(weatherApi.main.feels_like) + "°C" +
                "\n" +
                weatherApi.weather.get(0).main + ", " + weatherApi.weather.get(0).description;
    }

    public static String getWeatherInfoFromCityName(String cityName) {
        cityName = cityName.split(" ", 2)[0].toLowerCase();
        WeatherApi weatherApi = getWeatherObjectByCityName(cityName);
        return getWeatherInfoByWeatherApi(weatherApi);
    }

    public static String getWeatherInfoFromLocation(Location location) {
        return getWeatherInfoByWeatherApi(getWeatherObjectByLocation(location));
    }

    private static WeatherApi getWeatherObjectByCityName(String cityName) {
        String text = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=b6d13f47169f7d28c514be4f194b4bde";
        InputStream inputStream = getInputStreamFromUrl(text);
        try {
            return objectMapper.readValue(inputStream, WeatherApi.class);
        } catch (IOException e) {
            System.out.println("objectmapper WeatherApi classni read qilolmadi");
            return null;
        }
    }

    private static WeatherApi getWeatherObjectByLocation(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        String urlText = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=b6d13f47169f7d28c514be4f194b4bde";
        InputStream inputStream = getInputStreamFromUrl(urlText);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(inputStream, WeatherApi.class);
        } catch (IOException e) {
            System.out.println("weather location inputStreamni objectmapper orqali readvalue qilishda  error");
            return null;
        }
    }

    private static String toCelsius(double fahrenheit) {
        return String.format("%.2f", fahrenheit - 273);
    }

    public static List<String> getCitiesByCountryName(String countryName) {
        return Objects.requireNonNull(getCountryByName(countryName)).cities;
    }

    public static List<String> getCountryListInString() {
        List<String> list = new ArrayList<>();
        for (Country country : Objects.requireNonNull(getCountryList())) {
            list.add(country.country);
        }
        return list;
    }

    private static Country getCountryByName(String countryName) {
        for (Country country : Objects.requireNonNull(getCountryList())) {
            if (country.country.equals(countryName)) {
                return country;
            }
        }
        return null;
    }

    private static List<Country> getCountryList() {
        String text = "https://countriesnow.space/api/v0.1/countries";
        InputStream inputStream = getInputStreamFromUrl(text);
        try {
            return objectMapper.readValue(inputStream, CountryApi.class).data;
        } catch (IOException e) {
            System.out.println("cities inputStream read value error");
            return null;
        }
    }

    private static InputStream getInputStreamFromUrl(String urlText) {
        try {
            return new URL(urlText).openStream();
        } catch (IOException e) {
            System.out.println("get urlText ERROR");
            return null;
        }
    }
}


