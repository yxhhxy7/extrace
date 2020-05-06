package extrace.misc.model;

import java.util.Date;

public class RouteItem {
    private Date time;
    private String from;
    private String to;

    public RouteItem() {
    }

    public RouteItem(Date time, String from, String to) {
        this.time = time;
        this.from = from;
        this.to = to;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "RouteItem{" +
                "time=" + time +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
