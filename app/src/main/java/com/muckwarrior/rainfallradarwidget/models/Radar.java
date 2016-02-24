package com.muckwarrior.rainfallradarwidget.models;

import java.util.List;

/**
 * Created by aaronsmith on 24/02/2016.
 */
public class Radar {

    List<Image> mImages;

    public Radar(List<Image> images) {
        mImages = images;
    }

    public List<Image> getImages() {
        return mImages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Radar radar = (Radar) o;

        return mImages != null ? mImages.equals(radar.mImages) : radar.mImages == null;

    }

    @Override
    public int hashCode() {
        return mImages != null ? mImages.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Radar{" +
                "mImages=" + mImages +
                '}';
    }
}
