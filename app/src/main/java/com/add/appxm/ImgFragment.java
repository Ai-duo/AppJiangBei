package com.add.appxm;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

public class ImgFragment extends Fragment {
    @Nullable
    ImageView view ;
    AnimationDrawable drawables;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null)
        view = (ImageView) inflater.inflate(R.layout.activity_img,container,false);
        if(drawables==null) {
            drawables = new AnimationDrawable();
            drawables.addFrame(getImage("p1.jpg"),5000);
            drawables.addFrame(getImage("p2.jpg"),5000);
            drawables.addFrame(getImage("p3.jpg"),5000);
            drawables.addFrame(getImage("p4.jpg"),5000);
            drawables.addFrame(getImage("p5.jpg"),5000);
            drawables.addFrame(getImage("p6.jpg"),5000);
            drawables.addFrame(getImage("p7.jpg"),5000);

        }
        view.setImageDrawable(drawables);
        drawables.start();
        return view ;
    }
    private BitmapDrawable getImage(String fileName)
    {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return new BitmapDrawable(image);
    }
    @Override
    public void onDetach() {
        if(drawables!=null){
            drawables.stop();
        }
        super.onDetach();
    }
}
