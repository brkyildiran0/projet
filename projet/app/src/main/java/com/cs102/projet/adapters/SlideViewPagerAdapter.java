package com.cs102.projet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cs102.projet.R;
import com.cs102.projet.activities.ProjetMainPageActivity;
import com.cs102.projet.activities.SlideActivity;

public class SlideViewPagerAdapter extends PagerAdapter
{
    //Global Variables
    Context context;

    public SlideViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object)
    {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;

        View view = layoutInflater.inflate(R.layout.slide_screen,container,false);

        //Setting the images from the resources\drawables directory
        ImageView image = view.findViewById(R.id.imageView2);
        ImageView next = view.findViewById(R.id.nextImage);
        ImageView back = view.findViewById(R.id.backImage);

        final Button finish = view.findViewById(R.id.finishButton);

        switch (position)
        {
            case 0:
                image.setImageResource(R.drawable.currentprojetmainpaint);
                break;
            case 1:
                image.setImageResource(R.drawable.projeanapaint);
                break;
            case 2:
                image.setImageResource(R.drawable.addtaskspaint);
                break;
            case 3:
                image.setImageResource(R.drawable.addmemberspaint);
                break;
            case 4:
                image.setImageResource(R.drawable.settingspaint);
                break;
        }

        //Making next image clickable
        next.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SlideActivity.viewPager.setCurrentItem(position + 1);
            }
        });

        //Making back image clickable
        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SlideActivity.viewPager.setCurrentItem(position - 1);
            }
        });

        //Making get started button clickable
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(context, ProjetMainPageActivity.class);
                context.startActivity(go);
            }
        });

        //Setting next and back images and get started button visible or invisible according to pages
        if ( position == 0)
        {
            back.setVisibility(View.INVISIBLE);
        }
        else if (position == 4)
        {
            next.setVisibility(View.INVISIBLE);
            finish.setVisibility(View.VISIBLE);
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
    {
        container.removeView((View) object);
    }
}
