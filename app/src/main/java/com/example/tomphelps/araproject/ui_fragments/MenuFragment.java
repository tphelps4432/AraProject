package com.example.tomphelps.araproject.ui_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomphelps.araproject.R;

/**
 * Created by Tom Phelps on 9/6/2015.
 */
public class MenuFragment extends android.support.v4.app.Fragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
    }
}