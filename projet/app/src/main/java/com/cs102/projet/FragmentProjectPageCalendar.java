package com.cs102.projet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentProjectPageCalendar extends Fragment {

    private CalendarView calendar_pp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View calendarView = inflater.inflate(R.layout.fragment_project_page_calendar, container, false);
        //calendar_pp =
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
