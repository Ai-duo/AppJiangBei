package com.add.appxm;

import android.text.TextUtils;
import android.util.Log;

/*{"town_site_item":"K2159","town_name":"海曙","wea_logo":"宁波海曙12月01日15时发布的七天预报","DATE":"20211201","TIME":"1500",
"wea_txt1":"1日 :晴到少云;2～11℃","wea_txt2":"2日 :晴到少云;1～14℃","wea_txt3":"3日 :晴到少云;3～14℃","wea_txt4":"4日 :多云;5～16℃","wea_txt5":"5日 :多云到晴;7～18℃","wea_txt6":"6日 :多云;7～14℃","wea_txt7":"7日 :晴到多云;4～12℃"}*/
public class SevenWea {
    public String town_site_item;
    public String town_name;
    public String wea_logo;
    public String DATE;
    public String TIME;
    public String txt1_date;
    public String txt2_date;
    public String txt3_date;
    public String txt4_date;
    public String txt5_date;
    public String txt6_date;
    public String txt7_date;


    public float txt1_max;
    public float txt1_min;
    public String txt1;
    public float txt2_max;
    public float txt2_min;
    public String txt2;
    public float txt3_max;
    public float txt3_min;
    public String txt3;
    public float txt4_max;
    public float txt4_min;
    public String txt4;
    public float txt5_max;
    public float txt5_min;
    public String txt5;
    public float txt6_max;
    public float txt6_min;
    public String txt6;
    public float txt7_max;
    public float txt7_min;
    public String txt7;

    public String txt1_week;
    public String txt2_week;
    public String txt3_week;
    public String txt4_week;
    public String txt5_week;
    public String txt6_week;
    public String txt7_week;


    public String wea_txt1;
    public String wea_txt2;
    public String wea_txt3;
    public String wea_txt4;
    public String wea_txt5;
    public String wea_txt6;
    public String wea_txt7;
    public float maxNum;
    public float minNum;
    public void build() {
        String[] ss1 = toArray(wea_txt1);
        txt1_date = ss1[0];
        txt1 = ss1[1];
        //txt1_week = ss1[1];
        txt1_min = Float.parseFloat(ss1[2]);
        txt1_max = Float.parseFloat(ss1[3]);
       if (maxNum<txt1_max){
           maxNum = txt1_max;
       }
       if (minNum>txt1_min){
           minNum = txt1_min;
       }
        String[] ss2 = toArray(wea_txt2);
        txt2_date = ss2[0];
        txt2 = ss2[1];
       // txt2_week = ss2[1];
        txt2_min =Float.parseFloat(ss2[2]);
        txt2_max = Float.parseFloat(ss2[3]);
        if (maxNum<txt2_max){
            maxNum = txt2_max;
        }
        if (minNum>txt2_min){
            minNum = txt2_min;
        }
        String[] ss3 = toArray(wea_txt3);
        txt3_date = ss3[0];
        txt3 = ss3[1];
        //txt3_week = ss3[1];
        txt3_min = Float.parseFloat(ss3[2]);
        txt3_max = Float.parseFloat(ss3[3]);
        if (maxNum<txt3_max){
            maxNum = txt3_max;
        }
        if (minNum>txt3_min){
            minNum = txt3_min;
        }
        String[] ss4 = toArray(wea_txt4);
        txt4_date = ss4[0];
        txt4 = ss4[1];
      //  txt4_week = ss4[1];
        txt4_min = Float.parseFloat(ss4[2]);
        txt4_max = Float.parseFloat(ss4[3]);
        if (maxNum<txt4_max){
            maxNum = txt4_max;
        }
        if (minNum>txt4_min){
            minNum = txt4_min;
        }
        String[] ss5 = toArray(wea_txt5);
        txt5_date = ss5[0];
        txt5 = ss5[1];
        //txt5_week = ss5[1];
        txt5_min = Float.parseFloat(ss5[2]);
        txt5_max = Float.parseFloat(ss5[3]);
        if (maxNum<txt5_max){
            maxNum = txt5_max;
        }
        if (minNum>txt5_min){
            minNum = txt5_min;
        }
        String[] ss6 = toArray(wea_txt6);
        txt6_date = ss6[0];
        txt6 = ss6[1];
       // txt6_week = ss6[1];
        txt6_min = Float.parseFloat(ss6[2]);
        txt6_max =Float.parseFloat(ss6[3]);
        if (maxNum<txt6_max){
            maxNum = txt6_max;
        }
        if (minNum>txt6_min){
            minNum = txt6_min;
        }
        String[] ss7 = toArray(wea_txt7);
        txt7_date = ss7[0];
        txt7 = ss7[1];
      //  txt7_week = ss7[1];
        txt7_min = Float.parseFloat(ss7[2]);
        txt7_max = Float.parseFloat(ss7[3]);

        if (maxNum<txt7_max){
            maxNum = txt7_max;
        }
        if (minNum>txt7_min){
            minNum = txt7_min;
        }
        Log.i("TAG","max:"+maxNum+";mix:"+minNum);
    }

    public String chang(String s) {
        if (TextUtils.isEmpty(s)) return "";
        return s.replace(" :", ";").replace("℃", "").replace("～",";");
    }

    public String[] toArray(String s) {
        String s1 = chang(s);
        String[] ss = s1.split(";");
        return ss;
    }

    @Override
    public String toString() {
        return "SevenWea{" +
                "town_site_item='" + town_site_item + '\'' +
                ", town_name='" + town_name + '\'' +
                ", wea_logo='" + wea_logo + '\'' +
                ", DATE='" + DATE + '\'' +
                ", TIME='" + TIME + '\'' +
                ", txt1_date='" + txt1_date + '\'' +
                ", txt2_date='" + txt2_date + '\'' +
                ", txt3_date='" + txt3_date + '\'' +
                ", txt4_date='" + txt4_date + '\'' +
                ", txt5_date='" + txt5_date + '\'' +
                ", txt6_date='" + txt6_date + '\'' +
                ", txt7_date='" + txt7_date + '\'' +
                ", txt1_max=" + txt1_max +
                ", txt1_min=" + txt1_min +
                ", txt1='" + txt1 + '\'' +
                ", txt2_max=" + txt2_max +
                ", txt2_min=" + txt2_min +
                ", txt2='" + txt2 + '\'' +
                ", txt3_max=" + txt3_max +
                ", txt3_min=" + txt3_min +
                ", txt3='" + txt3 + '\'' +
                ", txt4_max=" + txt4_max +
                ", txt4_min=" + txt4_min +
                ", txt4='" + txt4 + '\'' +
                ", txt5_max=" + txt5_max +
                ", txt5_min=" + txt5_min +
                ", txt5='" + txt5 + '\'' +
                ", txt6_max=" + txt6_max +
                ", txt6_min=" + txt6_min +
                ", txt6='" + txt6 + '\'' +
                ", txt7_max=" + txt7_max +
                ", txt7_min=" + txt7_min +
                ", txt7='" + txt7 + '\'' +
                ", txt1_week='" + txt1_week + '\'' +
                ", txt2_week='" + txt2_week + '\'' +
                ", txt3_week='" + txt3_week + '\'' +
                ", txt4_week='" + txt4_week + '\'' +
                ", txt5_week='" + txt5_week + '\'' +
                ", txt6_week='" + txt6_week + '\'' +
                ", txt7_week='" + txt7_week + '\'' +
                ", wea_txt1='" + wea_txt1 + '\'' +
                ", wea_txt2='" + wea_txt2 + '\'' +
                ", wea_txt3='" + wea_txt3 + '\'' +
                ", wea_txt4='" + wea_txt4 + '\'' +
                ", wea_txt5='" + wea_txt5 + '\'' +
                ", wea_txt6='" + wea_txt6 + '\'' +
                ", wea_txt7='" + wea_txt7 + '\'' +
                '}';
    }
}
