package com.cs102.projet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.cs102.projet.R;
import com.cs102.projet.adapters.SlideViewPagerAdapter;

public class SlideActivity extends AppCompatActivity
{
    //Global Variables
    public static ViewPager viewPager;
    SlideViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        //Adding the slides to the activity by using the adapter
        viewPager = findViewById(R.id.viewpager);
        adapter = new SlideViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
    }
}
    