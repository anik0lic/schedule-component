package raf.sk.projekat1.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Info {
    protected int place;
    protected int time;
    protected int day;
    protected int startDate;
    protected int endDate;
    protected String dateFormat;
    protected List<String> dayFormat;
    protected List<String> headers;

    //1
    public Info(int place, int time, int day, int startDate, String dateFormat, List<String> dayFormat) {
        this.place = place;
        this.time = time;
        this.day = day;
        this.startDate = startDate;
        this.dateFormat = dateFormat;
        this.dayFormat = dayFormat;
        this.endDate = -1;
    }

    //2
    public Info(int place, int time, int day, int startDate, int endDate, String dateFormat, List<String> dayFormat) {
        this.place = place;
        this.time = time;
        this.day = day;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateFormat = dateFormat;
        this.dayFormat = dayFormat;
    }

    public Info(String dateFormat, List<String> dayFormat) {
        this.dateFormat = dateFormat;
        this.dayFormat = dayFormat;
    }
}
