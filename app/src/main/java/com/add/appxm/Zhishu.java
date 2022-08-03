package com.add.appxm;
/*{"stationnum":"K2159","stationname":"海曙 ","observtime":"2021/11/25 6:00:00","jyzslevel":"三级","jyzs":"容易感冒","cyzslevel":"二级
","cyzs":"适宜穿着棉衣、皮衣、厚毛衣等","slhxlevel":"四级        ","slhx":"容易引起森林火灾，林区控制野外用火","zwxlevel":"三级，中等     ","zwx":"外出可适当采取一些防护措施"}*/
public class Zhishu {
    public String stationnum,stationname,observtime,jyzslevel,jyzs,cyzslevel,cyzs,slhxlevel,slhx,zwxlevel,zwx,clzslevel,clzs,ydzslevel,ydzs;

    /*"clzslevel": "三级,        ",
    "clzs": " 较适宜晨练",
    "ydzslevel": "四级,        ",
    "ydzs": "不太适宜运动。"*/
    @Override
    public String toString() {
        return "Zhishu{" +
                "stationnum='" + stationnum + '\'' +
                ", stationname='" + stationname + '\'' +
                ", observtime='" + observtime + '\'' +
                ", jyzslevel='" + jyzslevel + '\'' +
                ", jyzs='" + jyzs + '\'' +
                ", cyzslevel='" + cyzslevel + '\'' +
                ", cyzs='" + cyzs + '\'' +
                ", slhxlevel='" + slhxlevel + '\'' +
                ", slhx='" + slhx + '\'' +
                ", zwxlevel='" + zwxlevel + '\'' +
                ", zwx='" + zwx + '\'' +
                '}';
    }
}
