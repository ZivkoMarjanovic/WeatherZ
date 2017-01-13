package net;



import java.util.Map;


import model_daily.Daily;
import model_forecast.Forecast;
import model_weather.ExampleNew;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;



public interface MyApiEndpointInterface {

    @GET("data/2.5/weather")
    Call<ExampleNew> getExample(@QueryMap Map<String, Double> latLon,@QueryMap Map<String, String> city_id);

    @GET("data/2.5/forecast/daily")
    Call<Daily> getDailyForecast(@QueryMap Map<String, Double> latLon, @QueryMap Map<String, String> city_id);

    @GET("data/2.5/forecast")
    Call<Forecast> getForecast(@QueryMap Map<String, Double> latLon,@QueryMap Map<String, String> city_id);


}
