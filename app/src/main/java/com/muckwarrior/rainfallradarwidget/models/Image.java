package com.muckwarrior.rainfallradarwidget.models;

/**
 * Created by aaronsmith on 24/02/2016.
 */
public class Image {

    private final int day;
    private final int hour;
    private final int min;
    private final String src;

    public Image(int day, int hour, int min, String src) {
        this.day = day;
        this.hour = hour;
        this.min = min;
        this.src = src;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public String getSrc() {
        return src;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Image image = (Image) o;

        if (day != image.day) return false;
        if (hour != image.hour) return false;
        if (min != image.min) return false;
        return src != null ? src.equals(image.src) : image.src == null;

    }

    @Override
    public int hashCode() {
        int result = day;
        result = 31 * result + hour;
        result = 31 * result + min;
        result = 31 * result + (src != null ? src.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Image{" +
                "day=" + day +
                ", hour=" + hour +
                ", min=" + min +
                ", src='" + src + '\'' +
                '}';
    }
}
