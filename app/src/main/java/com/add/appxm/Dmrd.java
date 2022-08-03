package com.add.appxm;

public class Dmrd {

    public String zfs;
    public String zwx;


    public Dmrd(String zfs, String zwx) {

        this.zfs = zfs;
        this.zwx = zwx;
    }

    @Override
    public String toString() {
        return "Dmrd{" +
                "zfs='" + zfs + '\'' +
                ", zwx='" + zwx + '\'' +
                '}';
    }
}
