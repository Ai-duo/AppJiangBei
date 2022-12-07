package com.add.appxm;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.add.appxm.databinding.ActivityOxyBinding;

import org.greenrobot.eventbus.EventBus;

public class OxyFragment extends Fragment {
    ActivityOxyBinding binding;
    int oxindex = 1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().post(new UpdateTime("负氧离子"));
        if(binding==null)
            binding = DataBindingUtil.inflate(inflater, R.layout.activity_oxy,container,false);
        if(rain!=null){
            binding.setIndex(oxindex);
            binding.setOxy(rain);
            binding.setInfo(getInfo(rain));
        }
        return binding.getRoot();
    }

    Oxy rain;
    public void updateInfo(Oxy live) {
        this.rain = live;
        if (binding!=null){
            binding.setOxy(rain);
            binding.setIndex(oxindex);
            binding.setInfo(getInfo(rain));
        }
    }
    private String getInfo( Oxy rain){
        int index = 500;
        try {
             index = Integer.parseInt(rain.ndu.replace("(个/cm³)","").replace("(个/cm3)",""));

        }catch (Exception e){
           e.printStackTrace();
        }
         if(index>1000){
             oxindex = 3;
           return "浓度很高，空气很清新。";
        }else if(index>500){
             oxindex = 2;
            return "浓度高，空气清新。";
        }else {
             oxindex = 1;
            return "浓度中，空气一般。";
        }
    }
}
