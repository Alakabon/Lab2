package ca.polymtl.inf8405.lab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class ProfileManager extends Fragment {
    View _view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inflate the Fragment's view and call findViewById() on the View to set event handler
        _view = inflater.inflate(R.layout.tab1_profile, container, false);
        ((Button) _view.findViewById(R.id.btn_camera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Launch the camera app
                Intent _camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(_camera, 8405);
            }
        });

        return _view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 8405) {
            //Retrieve the image onActivityResult and display it on the ImageView
            Bitmap _photo = (Bitmap) data.getExtras().get("data");
            ((ImageView) _view.findViewById(R.id.img_profile)).setImageBitmap(_photo);
        }
    }
}
