package com.add.appxm;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.github.mikephil.charting.charts.BarChart;

public  class DataBindingSets {

    @BindingAdapter("setBackImage")
    public static void setBackImage(LinearLayout view,Integer index){
        if (index==null)return;
        if(index==0){
            view.setBackgroundResource(R.drawable.db1);
        }else if(index==1){
            view.setBackgroundResource(R.drawable.db2);
        }else if(index==2){
            view.setBackgroundResource(R.drawable.db3);
        }
    }
    @BindingAdapter("setTextColor")
    public static void setTextColor(TextView view, Integer index){
        if (index==null)return;
        if(index==0){
            view.setTextColor(Color.WHITE);
        }else if(index==1){
            view.setTextColor(Color.YELLOW);
        }else if(index==2){
            view.setTextColor(Color.argb(255,255,255,0));
        }
    }
    @BindingAdapter("setText")
    public static void setText(MarqueeView view, String text){
        if (TextUtils.isEmpty(text))return;
        view.setContent(text);
    }
    @BindingAdapter("setTemp")
    public static void setTemp(TempView view, String text){
        if (TextUtils.isEmpty(text))return;
        text = text.replace("℃","");
        if(text.equals("--"))text = "0";
        view.setMinc(Float.parseFloat(text));
    }
    @BindingAdapter("setBar")
    public static void setBar(BarChart view, String text){

    }
    @BindingAdapter("setImage")
    public static void setText(ImageView view, String text){

        if (TextUtils.isEmpty(text))return;
        switch (text){
            case"雨":
            case"小雨":
                view.setImageResource(R.drawable.xiaoyu);
                break;
            case"中雨":
                view.setImageResource(R.drawable.zhongyu);
                break;
            case"大雨":
                view.setImageResource(R.drawable.dayu);
                break;
            case"暴雨":
                view.setImageResource(R.drawable.baoyu);
                break;
            case"特大暴雨":
                view.setImageResource(R.drawable.tedabaoyu);
                break;
            case"雷阵雨":
                view.setImageResource(R.drawable.leizhenyu);
                break;
            case"冰雨":
                view.setImageResource(R.drawable.bingyu);
                break;
            case"小雪":
            case"雪":
                view.setImageResource(R.drawable.xiaoxue);
                break;
            case"中雪":
                view.setImageResource(R.drawable.zhongxue);
                break;
            case"大雪":
                view.setImageResource(R.drawable.daxue);
                break;
            case"雨夹雪":
                view.setImageResource(R.drawable.yujiaxue);

            case"阴有阵雨":
                view.setImageResource(R.drawable.yinyouzhenyu);
            case"阵雨":
                view.setImageResource(R.drawable.zhenyu);
            case"阵雪":
                view.setImageResource(R.drawable.zhenxue);
                break;
            case"雾":
                view.setImageResource(R.drawable.wu);
                break;
            case"沙尘暴":
                view.setImageResource(R.drawable.shachenbao);
                break;
            case"扬沙":
                view.setImageResource(R.drawable.yangsha);
                break;
            case"阴天":
            case"阴":
                view.setImageResource(R.drawable.yintian);
                break;
            case"月亮":
                view.setImageResource(R.drawable.yueliang);
                break;
            case"强沙尘暴":
                view.setImageResource(R.drawable.qiangshachenbao);
                break;
            case"晴":
            case"晴天":
                view.setImageResource(R.drawable.qing);
                break;
            case"多云":
            case"少云":
                view.setImageResource(R.drawable.duoyun);
                break;
            default:
                if(text.contains("到")){
                    String[] s = text.split("到");
                    setText(view,s[1]);
                }else  if(text.contains("转")){
                    String[] s = text.split("转");
                    setText(view,s[1]);
                }


        }
    }

}
