package apies.weatherApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Sys {
    public int type;
    public int id;
    public String country;
    public int sunrise;
    public int sunset;
}
