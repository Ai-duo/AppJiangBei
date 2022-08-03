package com.add.appxm;

public class Dmgd {
    public String wd;
    public String sd;
    public String fs;
    public String fx;
    public String qy;
    public String js;
    public String njd;
    public String time;

    public Dmgd(String wd, String sd, String fs, String fx, String qy, String js, String njd, String time) {
        this.wd = wd;
        this.sd = sd;
        this.fs = fs;
        this.fx = fx;
        this.qy = qy;
        this.js = js;
        this.njd = njd;
        this.time = time;
    }

    public Dmgd(String wd, String sd, String fs, String fx, String qy, String js, String njd) {
        this.wd = wd;
        this.sd = sd;
        this.fs = fs;
        this.fx = fx;
        this.qy = qy;
        this.js = js;
        this.njd = njd;
    }

    @Override
    public String toString() {
        return "Dmgd{" +
                "wd='" + wd + '\'' +
                ", sd='" + sd + '\'' +
                ", fs='" + fs + '\'' +
                ", fx='" + fx + '\'' +
                ", qy='" + qy + '\'' +
                ", js='" + js + '\'' +
                ", njd='" + njd + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
