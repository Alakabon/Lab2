package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.polymtl.inf8405.lab2.R;

public class EventsManager extends Fragment {
    View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab4_events, container, false);
        return _view;
    }
}
