package com.add.appxm;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.add.appxm.databinding.ActivityThirdBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThirdFragment extends Fragment {
    ActivityThirdBinding inflate;
    public SevenWea wea;
    public BarChart bc1,bc2,bc3,bc4,bc5,bc6,bc7;
    public String img1 = "",img2 = "",img3 = "",img4 = "",img5 = "",img6 = "",img7 = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().post(new UpdateTime("七天预报"));
        if (inflate == null) {
            inflate = DataBindingUtil.inflate(inflater, R.layout.activity_third, container, false);
            bc1 = inflate.t1;
            bc2 = inflate.t2;
            bc3 = inflate.t3;
            bc4 = inflate.t4;
            bc5 = inflate.t5;
            bc6 = inflate.t6;
            bc7 = inflate.t7;
            initChart(bc1);
            initChart(bc2);
            initChart(bc3);
            initChart(bc4);
            initChart(bc5);
            initChart(bc6);
            initChart(bc7);

        }
        if (wea != null) {
            inflate.setSeven(wea);
            max = wea.maxNum+15;
            min = wea.minNum-10;
            updateBardata(bc1, wea.txt1_min, wea.txt1_max);
            updateBardata(bc2, wea.txt2_min, wea.txt2_max);
            updateBardata(bc3, wea.txt3_min, wea.txt3_max);
            updateBardata(bc4, wea.txt4_min, wea.txt4_max);
            updateBardata(bc5, wea.txt5_min, wea.txt5_max);
            updateBardata(bc6, wea.txt6_min, wea.txt6_max);
            updateBardata(bc7, wea.txt7_min, wea.txt7_max);

            if (wea.txt1 != img1) {
                img1 = wea.txt1;
                inflate.setImag1(img1);
            }
            if (wea.txt2 != img2) {
                img2 = wea.txt2;
                inflate.setImag2(img2);
            }
            if (wea.txt3 != img3) {
                img3 = wea.txt3;
                inflate.setImag3(img3);
            }
            if (wea.txt4 != img4) {
                img4 = wea.txt4;
                inflate.setImag4(img4);
            }
            if (wea.txt5 != img5) {
                img5 = wea.txt5;
                inflate.setImag5(img5);
            }
            if (wea.txt6 != img6) {
                img6 = wea.txt6;
                inflate.setImag6(img6);
            }
            if (wea.txt7 != img7) {
                img7 = wea.txt7;
                inflate.setImag7(img7);
            }
        }
        return inflate.getRoot();
    }

    public void updateInfo(SevenWea wea) {
        this.wea = wea;
        if (inflate != null) {

            if (wea != null){
                inflate.setSeven(wea);
                max = wea.maxNum+10;
                min = wea.minNum-5;

                updateBardata(bc1,wea.txt1_max,wea.txt1_max);
                updateBardata(bc2,wea.txt2_max,wea.txt2_max);
                updateBardata(bc3,wea.txt3_max,wea.txt3_max);
                updateBardata(bc4,wea.txt4_max,wea.txt4_max);
                updateBardata(bc5,wea.txt5_max,wea.txt5_max);
                updateBardata(bc6,wea.txt6_max,wea.txt6_max);
                updateBardata(bc7,wea.txt7_max,wea.txt7_max);

                if (wea.txt1 != img1) {
                    img1 = wea.txt1;
                    inflate.setImag1(img1);
                }
                if (wea.txt2 != img2) {
                    img2 = wea.txt2;
                    inflate.setImag2(img2);
                }
                if (wea.txt3 != img3) {
                    img3 = wea.txt3;
                    inflate.setImag3(img3);
                }
                if (wea.txt4 != img4) {
                    img4 = wea.txt4;
                    inflate.setImag4(img4);
                }
                if (wea.txt5 != img5) {
                    img5 = wea.txt5;
                    inflate.setImag5(img5);
                }
                if (wea.txt6 != img6) {
                    img6 = wea.txt6;
                    inflate.setImag6(img6);
                }
                if (wea.txt7 != img7) {
                    img7 = wea.txt7;
                    inflate.setImag7(img7);
                }
            }

        }
    }
    public void initChart(BarChart mBarChart){
//        mBarChart.groupBars(1980f, 20, 0);  // 设置group组间隔
        mBarChart.setFitBars(false);    // 在bar开头结尾两边添加一般bar宽的留白
        mBarChart.setViewPortOffsets(0,0,0,0);
        mBarChart.setLeft(0);
        mBarChart.setRight(0);
        mBarChart.setExtraRightOffset(0);
        mBarChart.setExtraLeftOffset(0);
        mBarChart.setDrawValueAboveBar(true);    // 所有值都绘制在柱形外顶部，而不是柱形内顶部。默认true
        mBarChart.setDrawBarShadow(false);   // 柱形阴影，一般有值被绘制，但是值到顶部的位置为空，这个方法设置也画这部分，但是性能下降约40%，默认false
        // setDrawValuesForWholeStack(boolean enabled);  // 没有该方法。。。是否绘制堆积的每个值，还是只是画堆积的总值，
        // setDrawHighlightArrow(true);  // 没有该方法。。。是否绘制高亮箭头
        XAxis xAxis= mBarChart.getXAxis();
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);
        xAxis.setSpaceMax(0.1f);
        xAxis.setSpaceMin(0.1f);
       // xAxis.se
        xAxis.setLabelCount(1,false);
        xAxis.setAxisLineWidth(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中

        YAxis yAxis = mBarChart.getAxisLeft();
        yAxis.setDrawAxisLine(false);
        yAxis.setDrawLabels(false);
        yAxis.setDrawGridLines(false);
        yAxis.setZeroLineWidth(0);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.setDescription(null);
    }
    float max, min;
    public void setMaxAndMix(BarChart mBarChart){
        mBarChart.getAxisLeft().setAxisMaximum(max);
        mBarChart.getAxisLeft().setAxisMinimum(min);
    }
    public void updateBardata(BarChart mBarChart,int min,int max){
        setMaxAndMix(mBarChart);
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(0, max));

        BarDataSet iBarDataSet = new BarDataSet(barEntries, "bar label");
         iBarDataSet.setColors(Color.rgb(250,150,0));
         iBarDataSet.setValueTextSize(8.0f);

         iBarDataSet.setValueTextColors(Collections.singletonList(Color.rgb(250, 150, 0)));
        iBarDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int i = (int)value;
                return i+"℃";
            }
        });

        List<BarEntry> barEntries1 = new ArrayList<>();
        barEntries1.add(new BarEntry(1, min));

        BarDataSet iBarDataSet1 = new BarDataSet(barEntries1, "bar label");
          iBarDataSet1.setColors(Color.rgb(30,144,255));
        iBarDataSet1.setValueTextSize(8.0f);

         iBarDataSet1.setValueTextColors(Collections.singletonList(Color.rgb(30, 144, 255)));
        iBarDataSet1.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int i = (int)value;
                return i+"℃";
            }
        });
        BarData barData = new BarData();
        barData.addDataSet(iBarDataSet);
        barData.addDataSet(iBarDataSet1);// 可以添加多个set，即可化成group组
        barData.setBarWidth(0.2f);
        mBarChart.setFitBars(false);
        barData.groupBars(0, 0.0f, 0.2f);
        mBarChart.setData(barData);

    }
}
