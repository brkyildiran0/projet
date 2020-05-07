package com.cs102.projet.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cs102.projet.R;

import org.w3c.dom.Text;

public class FragmentCreatePageMembers extends Fragment {
    private String memberName;
    private String imageName;
    private TextView textMemberName;
    private TextView textImageName;
    private ImageView memberImage;

    public FragmentCreatePageMembers() {
    }

    public FragmentCreatePageMembers(String memberName) {
        this.memberName = memberName;
        this.imageName = null;
    }

    public FragmentCreatePageMembers(String memberName, String imageName) {
        this.memberName = memberName;
        this.imageName = imageName;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_create_project_members, container, false);
        textMemberName = rootview.findViewById(R.id.textMemberName);
        memberImage = rootview.findViewById(R.id.memberImage);

        textMemberName.setText(this.memberName);
        if (this.imageName != null)
        {

            memberImage.setImageResource(getResources().getIdentifier(imageName, "drawable", getActivity().getPackageName()));
        }
        return rootview;
    }
}