//package thread.seopftware.mychef.HomeChef;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.database.Observable;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Switch;
//import android.widget.TextView;
//
//import com.esafirm.imagepicker.features.ImagePicker;
//import com.esafirm.imagepicker.features.camera.CameraModule;
//import com.esafirm.imagepicker.features.camera.ImmediateCameraModule;
//import com.esafirm.imagepicker.features.camera.OnImageReadyListener;
//import com.esafirm.imagepicker.model.Image;
//import com.esafirm.rximagepicker.RxImagePicker;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import thread.seopftware.mychef.R;
//
///**
// * Created by MSI on 2017-07-04.
// */
//
//public class MainActivity extends AppCompatActivity {
//
//    private static final int RC_CODE_PICKER = 2000;
//    private static final int RC_CAMERA = 3000;
//
//    private TextView textView;
//    private ArrayList<Image> images = new ArrayList<>();
//    private CameraModule cameraModule;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.picexam);
//
//        textView = (TextView) findViewById(R.id.text_view);
//
//        findViewById(R.id.button_pick_image).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                start();
//            }
//        });
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == RC_CAMERA) {
//            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                captureImage();
//            }
//        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//
//    private void captureImage() {
//        startActivityForResult(
//                getCameraModule().getCameraIntent(MainActivity.this), RC_CAMERA);
//    }
//
//    private ImmediateCameraModule getCameraModule() {
//        if (cameraModule == null) {
//            cameraModule = new ImmediateCameraModule();
//        }
//        return (ImmediateCameraModule) cameraModule;
//    }
//
//
//    public void start() {
//
//        ImagePicker imagePicker = ImagePicker.create(this)
//                .theme(R.style.ImagePickerTheme)
//                .returnAfterFirst(returnAfterCapture) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
//                .folderMode(true) // set folder mode (false by default)
//                .folderTitle("Folder") // folder selection title
//                .imageTitle("Tap to select"); // image selection title
//
//
//        imagePicker.limit(3)
//                .multi()// max images can be selected (99 by default)
//                .showCamera(true) // show camera or not (true by default)
//                .imageDirectory("Camera")   // captured image directory name ("Camera" folder by default)
//                .origin(images) // original selected images, used in multi mode
//                .start(RC_CODE_PICKER); // start image picker activity with request code
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
//        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
//            images = (ArrayList<Image>) ImagePicker.getImages(data);
//            printImages(images);
//            return;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
//
//    private void printImages(List<Image> images) {
//        if (images == null) return;
//
//        StringBuilder stringBuffer = new StringBuilder();
//        for (int i = 0, l = images.size(); i < l; i++) {
//            stringBuffer.append(images.get(i).getPath()).append("\n");
//        }
//        textView.setText(stringBuffer.toString());
//    }
//}
