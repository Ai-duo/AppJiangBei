package com.add.appxm;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.add.appxm.databinding.ActivityAlarmBinding;
import com.add.appxm.databinding.ActivityWeaBinding;

import org.greenrobot.eventbus.EventBus;

public class FifthFragment extends Fragment {
    public String text = "";
    ActivityAlarmBinding binding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().post(new UpdateTime("预警信息"));
        if(binding==null){
            binding = DataBindingUtil.inflate(inflater,R.layout.activity_alarm,container,false);
        }
        if (!TextUtils.isEmpty(text))
        binding.setTxt(text);
        return binding.getRoot();
    }
    public boolean isShow;
    public void updateText(Alarm text){
         if(text.state.equals("1")){
             if(!this.text.equals(text.des)){
                 this.text = text.des;
                 Log.i("TAG",text.des);
                 if(binding!=null)
                 binding.setTxt(text.des);
             }
            isShow = true;
         }else{
             isShow = false;
             this.text = null;
         }

    }
}
