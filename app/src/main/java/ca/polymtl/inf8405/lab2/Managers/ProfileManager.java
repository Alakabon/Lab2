package ca.polymtl.inf8405.lab2.Managers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import ca.polymtl.inf8405.lab2.R;

public class ProfileManager extends Fragment {
    private static final String TAG = "ProfileManager";
    private static final int REQUEST_IMAGE_CAPTURE = 8405;  //Constant value which will be used to identify specific camera results
    private View _view;
    private GlobalDataManager _gdm;

    //___________________________________________________________________________________________________________________________________//
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab1_profile, container, false);
        _gdm = (GlobalDataManager) getActivity().getApplicationContext();
        ((Button) _view.findViewById(R.id.btn_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //Defines the method that will call when the user click on the button
                //Instruct Android to automatically access the device's camera.
                //MediaStore is a built-in Android class that handles all things media,
                //and ACTION_IMAGE_CAPTURE is the standard intent that accesses the device's camera application
                Intent _camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                //It's ensuring a camera app is available and accessible. It's important to perform this check,
                //Because if we launch our intent and there is no camera application present to handle it, our app will crash
                if (_camera.resolveActivity(getActivity().getPackageManager()) != null)
                    startActivityForResult(_camera, REQUEST_IMAGE_CAPTURE);

                //The above line, launch the camera, and retrieve the resulting image
                //It will automatically trigger the callback method onActivityResult()
                //when the result of the activity is available
            }
        });

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
    //The result of the action we are launching will be returned automatically to this callback method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //Retrieve the image and display it on the ImageView
                Bitmap _photo = (Bitmap) data.getExtras().get("data");
                ((ImageView) _view.findViewById(R.id.img_profile)).setImageBitmap(_photo);
                setImageViewTag(_photo);
            }
        } catch (Exception ex) {
            ((ImageView) _view.findViewById(R.id.img_profile)).setImageResource(R.drawable.profile_error);
            setImageViewTagAsDefault();
            Log.e(TAG, ex.getMessage());
        }
    }

    //___________________________________________________________________________________________________________________________________//
    // Load data to Views of the Fragment based on latest values in GlobalDataManager
    public void setDataInEachViews() {
        try {
            if (_view != null && _gdm != null) {
                ((EditText) _view.findViewById(R.id.txt_alias)).setText(_gdm.getUserData().getName());
                ((EditText) _view.findViewById(R.id.txt_group)).setText(_gdm.getUserData().getGroup());
                byte[] _bytes = Base64.decode(_gdm.getUserData().getPhoto_url(), Base64.DEFAULT);
                if (_bytes.length == 0) {
                    ((ImageView) _view.findViewById(R.id.img_profile)).setImageResource(R.drawable.profile);
                    setImageViewTagAsDefault();
                } else {
                    Bitmap _photo = BitmapFactory.decodeByteArray(_bytes, 0, _bytes.length);
                    ((ImageView) _view.findViewById(R.id.img_profile)).setImageBitmap(_photo);
                    setImageViewTag(_photo);
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }

    //Turn the photo data into an array of individual bytes, and specify the type of encoding = Base64
    //Base64 is a format of binary-to-text encoding and let us to store binary as string in Firebase
    //___________________________________________________________________________________________________________________________________//
    private void setImageViewTagAsDefault() {
        ByteArrayOutputStream _stream = new ByteArrayOutputStream();
        Bitmap _photo = BitmapFactory.decodeResource(getResources(), R.drawable.profile);
        _photo.compress(Bitmap.CompressFormat.PNG, 100, _stream);
        ((ImageView) _view.findViewById(R.id.img_profile)).setTag(Base64.encodeToString(_stream.toByteArray(), Base64.DEFAULT));
    }

    private void setImageViewTag(Bitmap photo) {
        ByteArrayOutputStream _stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, _stream);
        ((ImageView) _view.findViewById(R.id.img_profile)).setTag(Base64.encodeToString(_stream.toByteArray(), Base64.DEFAULT));
    }
}