package com.add.appxm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.add.appxm.databinding.ActivitySecondBinding;

import org.greenrobot.eventbus.EventBus;

public class SecondFragment extends Fragment {
    ActivitySecondBinding inflate;
    public Dmrd dmrd;
    public Dm5d dm5d;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(inflate==null) {
            inflate = DataBindingUtil.inflate(inflater, R.layout.activity_second, container, false);
        }
        if(zhishu!=null){
            EventBus.getDefault().post(new UpdateTime("更新时间:"+zhishu.observtime));
        inflate.setZhishu(zhishu);}
        return inflate.getRoot();
    }
    public void updateInfo(Dmrd dmrd,Dm5d dm5d){
        this.dmrd =dmrd;
        this.dm5d =dm5d;
        if(inflate!=null) {
            if(dm5d!=null)
                inflate.setDm5d(dm5d);
            if(dmrd!=null)
                inflate.setDmrd(dmrd);
        }
    }
    Zhishu zhishu;
    public void updateInfo(Zhishu zhishu){
        this.zhishu =zhishu;

        if(inflate!=null) {
            if(zhishu!=null)
                EventBus.getDefault().post(new UpdateTime(zhishu.observtime));
                inflate.setZhishu(zhishu);

        }
    }
}
