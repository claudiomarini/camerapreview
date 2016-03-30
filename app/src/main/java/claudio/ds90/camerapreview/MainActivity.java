package claudio.ds90.camerapreview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements  SurfaceHolder.Callback{

    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing=false;
    LayoutInflater controlInflater=null;

    int currentid=Camera.CameraInfo.CAMERA_FACING_FRONT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView)findViewById(R.id.camerapreview);
        surfaceView.setDrawingCacheEnabled(true);
        surfaceView.setWillNotDraw(false);
        surfaceHolder=surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        controlInflater = LayoutInflater.from(getApplicationContext());

        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);


        ImageButton flip = (ImageButton)findViewById(R.id.flipcamera);
        assert flip != null;
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previewing){
                    Log.i("EEE","in previe e la stoppo");
                    camera.stopPreview();
                }

                camera.release();

                if(currentid==Camera.CameraInfo.CAMERA_FACING_FRONT){
                    Log.i("EEE","stava in front e la giro");

                    currentid=Camera.CameraInfo.CAMERA_FACING_BACK;
                    camera = Camera.open(currentid);
                    camera.setDisplayOrientation(90);
                }
                else{
                    Log.i("EEE","stava in back e la giro");

                    currentid=Camera.CameraInfo.CAMERA_FACING_FRONT;
                    camera = Camera.open(currentid);
                    camera.setDisplayOrientation(90);
                }
                Log.i("EEE","riparte");
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();

            }
        });


        ImageButton take= (ImageButton)findViewById(R.id.takepicture);
        assert take != null;
        take.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
                        Bitmap mutableBitmap = b.copy(Bitmap.Config.ARGB_8888, true);
                        Matrix x = new Matrix();
                        if(currentid== Camera.CameraInfo.CAMERA_FACING_FRONT){
                            x.postRotate(-90);
                        }
                        else{
                            x.postRotate(90);
                        }
                        Bitmap dausare= Bitmap.createBitmap(mutableBitmap,0,0,mutableBitmap.getWidth(),mutableBitmap.getHeight(),x,true);



                        LinearLayout lin = (LinearLayout)findViewById(R.id.alltext);

                        //TextView testo = (TextView)findViewById(R.id.tv_testo);
                        Bitmap testB;
                        //testB=Bitmap.createBitmap(testo.getWidth(), testo.getHeight(), Bitmap.Config.ARGB_8888);
                        testB=Bitmap.createBitmap(lin.getWidth(), lin.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(testB);
                        //testo.draw(c);
                        lin.draw(c);


                        ImageView iv2 = new ImageView(getApplicationContext());
                        iv2.setBackgroundColor(Color.GRAY);
                        iv2.setImageBitmap(testB);
                        iv2.setDrawingCacheEnabled(true);
                        iv2.buildDrawingCache();
                        BitmapDrawable drawable2 = (BitmapDrawable) iv2.getDrawable();

                        Rect r = drawable2.getBounds();
                        Bitmap bitmap2 = drawable2.getBitmap();

                        Bitmap res = overlay(dausare, bitmap2,lin.getX(),lin.getY() /*testo.getX(),testo.getY()*/);


                        try {
                            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString()+"/kakakakaka.png");
                            res.compress(Bitmap.CompressFormat.PNG, 100, fos);
                            fos.flush();
                            fos.close();

                            Intent i = new Intent(getApplicationContext(),ShareActivity.class);
                            i.putExtra("shareImg",Environment.getExternalStorageDirectory().toString()+"/kakakakaka.png");
                            startActivity(i);


                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                camera.takePicture(null,null,mPictureCallback);
                previewing=false;
            }
        });

    }



    private void takePicture(){



    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("EEE","SurfaceCreata");
        camera = Camera.open(currentid);
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        params.setFocusMode("fixed");//params.getSupportedFocusModes().get(0));

        camera.setParameters(params);


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("EEE","SurfaceCambiata");

        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }

        if (camera != null) {
            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("EEE","SurfaceSistrutta");

        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }
    protected void onResume(){
        super.onResume();
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2, float x, float y) {

        Bitmap bmOverlay = Bitmap.createBitmap(bmp1);
        Canvas canvas = new Canvas(bmOverlay);

        float pad = 72;
        float diffpx =TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,pad,getResources().getDisplayMetrics());
        canvas.drawBitmap(bmp2, x, y+diffpx, new Paint());
        return bmOverlay;
    }
}
