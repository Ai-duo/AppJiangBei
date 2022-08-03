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

import com.add.appxm.databinding.ActivityFirstBinding;

import org.greenrobot.eventbus.EventBus;

public class FirstFragment extends Fragment {
    ActivityFirstBinding inflate;
    public Dmgd dmgd;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(inflate==null) {
            inflate = DataBindingUtil.inflate(inflater, R.layout.activity_first, container, false);
        }
        if(live!=null){
            EventBus.getDefault().post(new UpdateTime("更新时间:"+live.updatetime));
            inflate.setLive(live);
        }
        return inflate.getRoot();
    }
    public void updateInfo(Dmgd dmgd){
        this.dmgd =dmgd;
        if(inflate!=null) {
            if(dmgd!=null){
                inflate.setDmgd(dmgd);
            }
        }
    }
    Live live;
    public void updateInfo(Live live){
        this.live =live;
        if(inflate!=null) {
            if(live!=null){
                EventBus.getDefault().post(new UpdateTime(live.updatetime));
                inflate.setLive(live);
            }
        }
    }
}
