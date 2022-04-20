package apies.weatherApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Weather {
    public int id;
    public String main;
    public String description;
    public String icon;
}
