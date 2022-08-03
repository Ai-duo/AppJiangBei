package com.add.appxm;
/*{"town_site_item":"K2159","town_name":"海曙",
"wea_logo":"宁波海曙","DATE":"20211125","TIME":"0600",
"wea_txt1":"今天晴到少云，明天晴到多云。今天西北到北风，明天北到东北风，风力都是2～3级。今天最高气温17℃，明天最低气温5℃。","ext1":""}*/
public class Wea {
    public String town_site_item,town_name,wea_logo,DATE,TIME,wea_txt1,ext1;

    @Override
    public String toString() {
        return "Wea{" +
                "town_site_item='" + town_site_item + '\'' +
                ", town_name='" + town_name + '\'' +
                ", wea_logo='" + wea_logo + '\'' +
                ", DATE='" + DATE + '\'' +
                ", TIME='" + TIME + '\'' +
                ", wea_txt1='" + wea_txt1 + '\'' +
                ", ext1='" + ext1 + '\'' +
                '}';
    }
}
