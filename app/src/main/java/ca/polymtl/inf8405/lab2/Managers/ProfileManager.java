package ca.polymtl.inf8405.lab2.Managers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

import ca.polymtl.inf8405.lab2.R;

public class ProfileManager extends Fragment {
    private static final int REQUEST_IMAGE_CAPTURE = 8405;  //Constant value which will be used to identify specific camera results
    private View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab1_profile, container, false);
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

        return _view;
    }

    //The result of the action we are launching will be returned automatically to this callback method
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                //Retrieve the image and display it on the ImageView
                Bitmap _photo = (Bitmap) data.getExtras().get("data");
                ((ImageView) _view.findViewById(R.id.img_profile)).setImageBitmap(_photo);

                //Turn the photo data into an array of individual bytes, and specify the type of encoding = Base64
                //Base64 is a format of binary-to-text encoding and let us to store binary as string in Firebase
                ByteArrayOutputStream _stream = new ByteArrayOutputStream();
                _photo.compress(Bitmap.CompressFormat.PNG, 100, _stream);
                ((GlobalDataManager) getActivity().getApplicationContext()).setPhoto_URL(Base64.encodeToString(_stream.toByteArray(), Base64.DEFAULT));
            }
        } catch (Exception ex) {
            ((ImageView) _view.findViewById(R.id.img_profile)).setImageResource(R.drawable.profile_error);
        }
    }
}