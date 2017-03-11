package ca.polymtl.inf8405.lab2.Managers;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.polymtl.inf8405.lab2.R;

public class StatusManager extends Fragment {
    private static final String TAG = "StatusManager";
    private View _view;
    private GlobalDataManager _gdm;

    //___________________________________________________________________________________________________________________________________//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _gdm = (GlobalDataManager) getActivity().getApplicationContext();
        _view = inflater.inflate(R.layout.tab5_status, container, false);
        setDataInEachViews();
        return _view;
    }

    //___________________________________________________________________________________________________________________________________//
    // Capture the focus state of fragment and load data to views
    @Override
    public void setMenuVisibility(final boolean visible) {
        super.setMenuVisibility(visible);
        if (visible) setDataInEachViews();
    }

    //___________________________________________________________________________________________________________________________________//
    // Load data to Views of the Fragment based on latest values in GlobalDataManager
    private void setDataInEachViews() {
        try {
            if (_view != null && _gdm != null) {
                ((TextView) _view.findViewById(R.id.txt1)).setText(_gdm.getOnlineStatusString());
                ((TextView) _view.findViewById(R.id.txt2)).setText(_gdm.getBatteryLevelString());
                ((TextView) _view.findViewById(R.id.txt3)).setText(_gdm.getGPSLatitudeString());
                ((TextView) _view.findViewById(R.id.txt4)).setText(_gdm.getGPSLongitudeString());
                ((TextView) _view.findViewById(R.id.txt5)).setText(_gdm.getGPSProviderStatusString());
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}
