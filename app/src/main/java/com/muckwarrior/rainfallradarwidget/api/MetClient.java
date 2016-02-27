package com.muckwarrior.rainfallradarwidget.api;

import com.muckwarrior.rainfallradarwidget.models.Radar;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

/**
 * Created by aaronsmith on 24/02/2016.
 */
public interface MetClient {

    @Headers({
            "Referer:http://www.met.ie/latest/rainfall_radar.asp"
    })
    @GET("/weathermaps/radar2/radar4_6hr.xml")
    Call<Radar> getRadar();

}
