package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ca.polymtl.inf8405.lab2.Entities.User;
import ca.polymtl.inf8405.lab2.R;

public class PlaceManager extends Fragment {
    View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab3_places, container, false);
        return _view;
    }
}