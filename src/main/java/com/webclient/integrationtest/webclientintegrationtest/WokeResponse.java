package com.webclient.integrationtest.webclientintegrationtest;


public class WokeResponse {
    public WokeResponse(String alarm1, String alarm2, String alarm3) {
        this.alarm1 = alarm1;
        this.alarm2 = alarm2;
        this.alarm3 = alarm3;
    }

    public String getAlarm1() {
        return alarm1;
    }

    public String getAlarm2() {
        return alarm2;
    }

    public String getAlarm3() {
        return alarm3;
    }

    public void setAlarm1(String alarm1) {
        this.alarm1 = alarm1;
    }

    public void setAlarm2(String alarm2) {
        this.alarm2 = alarm2;
    }

    public void setAlarm3(String alarm3) {
        this.alarm3 = alarm3;
    }

    private String alarm1;
    private String alarm2;
    private String alarm3;
}
