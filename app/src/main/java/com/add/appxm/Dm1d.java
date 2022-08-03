package com.add.appxm;

public class Dm1d {
    public String sw;
    public String syd;
    public String sddl;
    public String rjy;
    public String ph;

    public Dm1d(String sw, String syd, String sddl, String rjy, String ph) {
        this.sw = sw;
        this.syd = syd;
        this.sddl = sddl;
        this.rjy = rjy;
        this.ph = ph;
    }

    @Override
    public String toString() {
        return "Dm1d{" +
                "sw='" + sw + '\'' +
                ", syd='" + syd + '\'' +
                ", sddl='" + sddl + '\'' +
                ", rjy='" + rjy + '\'' +
                ", ph='" + ph + '\'' +
                '}';
    }
}
