package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Info {
    private int place;
    private int time;
    private int day;
    private int date;
    private String dateFormat;
    private List<String> dayFormat;

    public Info(int place, int time, int day, String dateFormat, List<String> dayFormat) {
        this.place = place;
        this.time = time;
        this.day = day;
        this.dateFormat = dateFormat;
        this.dayFormat = dayFormat;
    }

    public Info(int place, int time, int date, String dateFormat) {
        this.place = place;
        this.time = time;
        this.date = date;
        this.dateFormat = dateFormat;
    }

    public Info(String dateFormat, List<String> dayFormat) {
        this.dateFormat = dateFormat;
        this.dayFormat = dayFormat;
    }
}
