package claudio.ds90.camerapreview;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    static Camera camera=null;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    boolean previewing = false;
    LayoutInflater controlInflater = null;

    int currentid = Camera.CameraInfo.CAMERA_FACING_FRONT;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFormat(PixelFormat.UNKNOWN);
        surfaceView = (SurfaceView) findViewById(R.id.camerapreview);
        surfaceView.setDrawingCacheEnabled(true);
        surfaceView.setWillNotDraw(false);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        controlInflater = LayoutInflater.from(getApplicationContext());

        View viewControl = controlInflater.inflate(R.layout.control, null);
        LayoutParams layoutParamsControl = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
        this.addContentView(viewControl, layoutParamsControl);


        /*ImageButton flip = (ImageButton) findViewById(R.id.flipcamera);
        assert flip != null;
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previewing) {
                    Log.i("EEE", "in previe e la stoppo");
                    camera.stopPreview();
                }

                camera.release();

                if (currentid == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Log.i("EEE", "stava in front e la giro");

                    currentid = Camera.CameraInfo.CAMERA_FACING_BACK;
                    camera = Camera.open(currentid);
                    camera.setDisplayOrientation(90);
                } else {
                    Log.i("EEE", "stava in back e la giro");

                    currentid = Camera.CameraInfo.CAMERA_FACING_FRONT;
                    camera = Camera.open(currentid);
                    camera.setDisplayOrientation(90);
                }
                Log.i("EEE", "riparte");
                try {
                    camera.setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();

            }
        });*/


        ImageButton take = (ImageButton) findViewById(R.id.takepicture);
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
                        if (currentid == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            x.postRotate(-90);
                            x.preScale(1, -1);
                        } else {
                            x.postRotate(90);
                        }
                        Bitmap dausare = Bitmap.createBitmap(mutableBitmap, 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), x, true);

                       // Bitmap dausare=getResizedBitmap(dausare1,400,400);

                        LinearLayout lin = (LinearLayout) findViewById(R.id.alltext);

                        DisplayMetrics metrics = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(metrics);

                        //TextView testo = (TextView)findViewById(R.id.tv_testo);
                        Bitmap testB;
                        //testB=Bitmap.createBitmap(testo.getWidth(), testo.getHeight(), Bitmap.Config.ARGB_8888);
                        testB = Bitmap.createBitmap(lin.getWidth(), lin.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas c = new Canvas(testB);
                        //testo.draw(c);
                        lin.draw(c);


                        ImageView iv2 = new ImageView(getApplicationContext());
                        iv2.setMinimumHeight((int) (iv2.getHeight()*metrics.density));
                        iv2.setMinimumWidth((int) (iv2.getWidth() * metrics.density));
                        iv2.setBackgroundColor(Color.GRAY);
                        iv2.setImageBitmap(testB);
                        iv2.setDrawingCacheEnabled(true);
                        iv2.buildDrawingCache();
                        BitmapDrawable drawable2 = (BitmapDrawable) iv2.getDrawable();

                        Rect r = drawable2.getBounds();
                        Bitmap bitmap2 = drawable2.getBitmap();//getResizedBitmap(drawable2.getBitmap(), 400,400);

                        Bitmap res = overlay(dausare, bitmap2, lin.getX(), lin.getY() /*testo.getX(),testo.getY()*/);


                        try {
                            FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/kakakakaka.png");
                            res.compress(Bitmap.CompressFormat.PNG, 60, fos);
                            fos.flush();
                            fos.close();

                            Intent i = new Intent(getApplicationContext(), ShareActivity.class);
                            i.putExtra("shareImg", Environment.getExternalStorageDirectory().toString() + "/kakakakaka.png");
                            startActivity(i);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };

                camera.takePicture(null, null, mPictureCallback);
                previewing = false;
            }
        });

    }

    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menumain, menu);

        MenuItem item = menu.findItem(R.id.flipper);
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                flippa(item.getActionView());return true;
            }
        });
        return true;
    }


    public void flippa(View v){
        if (previewing) {
            Log.i("EEE", "in previe e la stoppo");
            camera.stopPreview();
        }

        camera.release();

        if (currentid == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            Log.i("EEE", "stava in front e la giro");

            currentid = Camera.CameraInfo.CAMERA_FACING_BACK;
            camera = Camera.open(currentid);
            camera.setDisplayOrientation(90);
        } else {
            Log.i("EEE", "stava in back e la giro");

            currentid = Camera.CameraInfo.CAMERA_FACING_FRONT;
            camera = Camera.open(currentid);
            camera.setDisplayOrientation(90);
        }
        Log.i("EEE", "riparte");
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();
    }

    private void takePicture() {


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("EEE", "SurfaceCreata");
        camera = Camera.open(currentid);
        camera.setDisplayOrientation(90);
        Camera.Parameters params = camera.getParameters();
        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //params.setFocusMode("fixed");//params.getSupportedFocusModes().get(0));

        camera.setParameters(params);


    }

    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }

    public Bitmap getResizedBitmap1(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("EEE", "SurfaceCambiata");

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
        Log.i("EEE", "SurfaceSistrutta");

        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    protected void onResume() {
        super.onResume();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public Bitmap overlay(Bitmap bmp1, Bitmap bmp2, float x, float y) {



        float xx1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bmp1.getWidth(), getResources().getDisplayMetrics());
        float yy1 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bmp1.getHeight(), getResources().getDisplayMetrics());

        Bitmap bmOverlay = Bitmap.createScaledBitmap(bmp1, (int) xx1, (int) yy1, true);

        Canvas canvas = new Canvas(bmOverlay);

        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        float pad = getStatusBarHeight()+actionBarHeight+100;


        float xx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bmp2.getWidth(), getResources().getDisplayMetrics());
        float yy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bmp2.getHeight(), getResources().getDisplayMetrics());
        float xpad = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pad, getResources().getDisplayMetrics());

        LinearLayout ll = (LinearLayout)findViewById(R.id.alltext);
        Bitmap m = Bitmap.createScaledBitmap(bmp2, (int) xx, (int) yy,true);
        canvas.drawBitmap(m, x, y+(int)xpad , new Paint());


        return getResizedBitmap1(bmOverlay,1000);
    }
}
