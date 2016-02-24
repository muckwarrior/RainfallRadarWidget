package com.muckwarrior.rainfallradarwidget.api;

import com.muckwarrior.rainfallradarwidget.models.Radar;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by aaronsmith on 24/02/2016.
 */
public interface MetClient {

    @GET("/weathermaps/radar2/radar4_6hr.xml")
    Call<Radar> getRadar();

}
