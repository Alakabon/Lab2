package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.polymtl.inf8405.lab2.R;

public class StatusManager extends Fragment {
    View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        GlobalDataManager _gdm = (GlobalDataManager) getActivity().getApplicationContext();
        _view = inflater.inflate(R.layout.tab5_status, container, false);
        ((TextView) _view.findViewById(R.id.txt1)).setText(_gdm.getOnlineStatusString());
        ((TextView) _view.findViewById(R.id.txt2)).setText(_gdm.getBatteryStatusString());
        ((TextView) _view.findViewById(R.id.txt3)).setText(_gdm.getGPSLatitudeString());
        ((TextView) _view.findViewById(R.id.txt4)).setText(_gdm.getGPSLongitudeString());
        return _view;
    }
}
