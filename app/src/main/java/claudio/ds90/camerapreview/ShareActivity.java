package claudio.ds90.camerapreview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.io.File;

public class ShareActivity extends AppCompatActivity {

    private ShareActionProvider mShareActionProvider;
    private Intent sharer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ImageView iv = (ImageView) findViewById(R.id.shareimg);
        Log.i("AAA", i.getStringExtra("shareImg"));
        File imgFile = new File(i.getStringExtra("shareImg"));
        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getPath());
        if (iv != null) {
            iv.setImageBitmap(myBitmap);
        } else {
            Log.i("AAA", "iv è nullo");
        }


        sharer = new Intent();
        sharer.setAction(Intent.ACTION_SEND);
        sharer.setType("image/*");
        sharer.putExtra(Intent.EXTRA_TEXT, "Ciao !!!! #SkyMeteo24");
        sharer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        sharer.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imgFile));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        mShareActionProvider.setShareIntent(sharer);
        return true;
    }
/*
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }*/
}
