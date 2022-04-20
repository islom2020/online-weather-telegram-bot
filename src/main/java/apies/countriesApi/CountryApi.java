package apies.countriesApi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryApi {
    public boolean error;
    public String msg;
    public List<Country> data;
}

