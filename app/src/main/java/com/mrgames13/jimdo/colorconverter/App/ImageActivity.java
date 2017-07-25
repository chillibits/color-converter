package com.mrgames13.jimdo.colorconverter.App;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ImageActivity extends AppCompatActivity {

    //Konstanten
    private final int REQ_IMAGE_CHOOSER = 10001;
    private final int REQ_CAMERA_CHOOSER = 10002;
    private final int REQ_PERMISSION_WRITE_EXTERNAL_STORAGE = 10003;

    //Variablen als Objekte
    private Resources res;
    private Toolbar toolbar;
    private ColorUtils clru;
    private ShowcaseView.Builder showcase_view;
    private Bitmap choosed_image = null;
    private Handler h;

    //Komponenten
    private ImageView image;
    private LinearLayout button_container;
    private ImageView choose_from_gallery;
    private ImageView choose_from_camera;
    private RelativeLayout color_button_container;
    private ImageView vibrant_color;
    private ImageView light_vibrant_color;
    private ImageView dark_vibrant_color;
    private ImageView muted_color;
    private ImageView light_muted_color;
    private ImageView dark_muted_color;
    private ImageView selected_color;

    //Variablen
    private Uri image_uri;
    private int int_vibrant_color;
    private int int_light_vibrant_color;
    private int int_dark_vibrant_color;
    private int int_muted_color;
    private int int_light_muted_color;
    private int int_dark_muted_color;
    private int int_choosed_color;
    private String current_photo_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        //Resourcen initialisieren
        res = getResources();

        //Handler initialisieren
        h = new Handler();

        //Toolbar initialisieren
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //Komponenten initialisieren
        image = (ImageView) findViewById(R.id.image);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (image.getDrawable() != null && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)) {
                    float eventX = event.getX();
                    float eventY = event.getY();
                    float[] eventXY = new float[] {eventX, eventY};

                    Matrix invertMatrix = new Matrix();
                    image.getImageMatrix().invert(invertMatrix);

                    invertMatrix.mapPoints(eventXY);
                    int x = Integer.valueOf((int)eventXY[0]);
                    int y = Integer.valueOf((int)eventXY[1]);

                    Drawable imgDrawable = image.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

                    //Limit x, y range within bitmap
                    if(x < 0) {
                        x = 0;
                    } else if (x > bitmap.getWidth()-1){
                        x = bitmap.getWidth()-1;
                    }

                    if(y < 0) {
                        y = 0;
                    } else if(y > bitmap.getHeight()-1){
                        y = bitmap.getHeight()-1;
                    }

                    int_choosed_color = bitmap.getPixel(x, y);
                    MainActivity.selected_image_color = int_choosed_color;
                    selected_color.setBackgroundColor(int_choosed_color);
                }
                return true;
            }
        });
        button_container = (LinearLayout) findViewById(R.id.button_container);
        choose_from_gallery = (ImageView) findViewById(R.id.choose_from_gallery);
        choose_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });
        choose_from_camera = (ImageView) findViewById(R.id.choose_from_camera);
        choose_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromCamera();
            }
        });
        color_button_container = (RelativeLayout) findViewById(R.id.color_button_container);
        vibrant_color = (ImageView) findViewById(R.id.vibrant_color);
        vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        light_vibrant_color = (ImageView) findViewById(R.id.light_vibrant_color);
        light_vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_light_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        dark_vibrant_color = (ImageView) findViewById(R.id.dark_vibrant_color);
        dark_vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_dark_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        muted_color = (ImageView) findViewById(R.id.muted_color);
        muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        light_muted_color = (ImageView) findViewById(R.id.light_muted_color);
        light_muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_light_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        dark_muted_color = (ImageView) findViewById(R.id.dark_muted_color);
        dark_muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_dark_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        selected_color = (ImageView) findViewById(R.id.selected_color);
        selected_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_choosed_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });

        //ShowCaseView initialisieren
        showcase_view = new ShowcaseView.Builder(ImageActivity.this)
                .withHoloShowcase()
                .setTarget(new ViewTarget(findViewById(R.id.choose_from_gallery)))
                .setContentTitle(R.string.instructions_gallery_t)
                .setContentText(R.string.instructions_gallery_m)
                .setStyle(R.style.showCaseView)
                .blockAllTouches()
                .singleShot(1)
                .setShowcaseEventListener(new OnShowcaseEventListener() {
                    @Override
                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                        showcase_view = new ShowcaseView.Builder(ImageActivity.this)
                                .withHoloShowcase()
                                .setTarget(new ViewTarget(findViewById(R.id.choose_from_camera)))
                                .setContentTitle(R.string.capture_image)
                                .setContentText(R.string.instructions_photo_m)
                                .setStyle(R.style.showCaseView)
                                .blockAllTouches()
                                .singleShot(2);
                        showcase_view.build();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {}

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {}
                });
        showcase_view.build();

        if(MainActivity.selected_image != null) {
            choosed_image = MainActivity.selected_image;
            image.setImageBitmap(choosed_image);

            int_vibrant_color = clru.getVibrantColor(choosed_image);
            vibrant_color.setBackgroundColor(int_vibrant_color);
            int_light_vibrant_color = clru.getLightVibrantColor(choosed_image);
            light_vibrant_color.setBackgroundColor(int_light_vibrant_color);
            int_dark_vibrant_color = clru.getDarkVibrantColor(choosed_image);
            dark_vibrant_color.setBackgroundColor(int_dark_vibrant_color);
            int_muted_color = clru.getMutedColor(choosed_image);
            muted_color.setBackgroundColor(int_muted_color);
            int_light_muted_color = clru.getLightMutedColor(choosed_image);
            light_muted_color.setBackgroundColor(int_light_muted_color);
            int_dark_muted_color = clru.getDarkMutedColor(choosed_image);
            dark_muted_color.setBackgroundColor(int_dark_muted_color);
            selected_color.setBackgroundColor(MainActivity.selected_image_color);

            button_container.setVisibility(View.GONE);
            color_button_container.setVisibility(View.VISIBLE);
        }

        if(ActivityCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_galery) {
            chooseImageFromGallery();
        } else if(id == R.id.action_capture) {
            chooseImageFromCamera();
        } else if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toolbar Text und Farbe setzen
        getSupportActionBar().setTitle(res.getString(R.string.title_activity_image));
        if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(clru.darkenColor(res.getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void chooseImageFromGallery() {
        FilePickerBuilder.getInstance()
                .enableCameraSupport(false)
                .setMaxCount(1)
                .pickPhoto(this);
    }

    private void chooseImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
        takePictureIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI;
                    if(Build.VERSION.SDK_INT >= 24) {
                        photoURI = FileProvider.getUriForFile(this, "com.mrgames13.jimdo.colorconverter.fileprovider", photoFile);
                    } else {
                        photoURI = Uri.fromFile(photoFile);
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQ_CAMERA_CHOOSER);
                }
            } catch (Exception e) {}
        }
    }

    private File createImageFile() throws IOException {
        //Dateinamen für das Bild erstellen
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp;
        File storageDir = getExternalCacheDir();
        File image = new File(storageDir, imageFileName + ".jpg");
        //Pfad festlegen
        current_photo_path = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_CAMERA_CHOOSER && resultCode == RESULT_OK && current_photo_path != null) {
            choosed_image = BitmapFactory.decodeFile(current_photo_path);
            MainActivity.selected_image = choosed_image;

            button_container.setVisibility(View.GONE);
            image.setImageBitmap(choosed_image);

            int_vibrant_color = clru.getVibrantColor(choosed_image);
            vibrant_color.setBackgroundColor(int_vibrant_color);
            int_light_vibrant_color = clru.getLightVibrantColor(choosed_image);
            light_vibrant_color.setBackgroundColor(int_light_vibrant_color);
            int_dark_vibrant_color = clru.getDarkVibrantColor(choosed_image);
            dark_vibrant_color.setBackgroundColor(int_dark_vibrant_color);
            int_muted_color = clru.getMutedColor(choosed_image);
            muted_color.setBackgroundColor(int_muted_color);
            int_light_muted_color = clru.getLightMutedColor(choosed_image);
            light_muted_color.setBackgroundColor(int_light_muted_color);
            int_dark_muted_color = clru.getDarkMutedColor(choosed_image);
            dark_muted_color.setBackgroundColor(int_dark_muted_color);

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.animation_scale_up);
            color_button_container.setVisibility(View.VISIBLE);
            color_button_container.startAnimation(anim);
        } else if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            button_container.setVisibility(View.GONE);
            String path = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA).get(0);
            choosed_image = BitmapFactory.decodeFile(path);
            image.setImageBitmap(choosed_image);
            MainActivity.selected_image = choosed_image;

            int_vibrant_color = clru.getVibrantColor(choosed_image);
            vibrant_color.setBackgroundColor(int_vibrant_color);
            int_light_vibrant_color = clru.getLightVibrantColor(choosed_image);
            light_vibrant_color.setBackgroundColor(int_light_vibrant_color);
            int_dark_vibrant_color = clru.getDarkVibrantColor(choosed_image);
            dark_vibrant_color.setBackgroundColor(int_dark_vibrant_color);
            int_muted_color = clru.getMutedColor(choosed_image);
            muted_color.setBackgroundColor(int_muted_color);
            int_light_muted_color = clru.getLightMutedColor(choosed_image);
            light_muted_color.setBackgroundColor(int_light_muted_color);
            int_dark_muted_color = clru.getDarkMutedColor(choosed_image);
            dark_muted_color.setBackgroundColor(int_dark_muted_color);

            Animation anim = AnimationUtils.loadAnimation(this, R.anim.animation_scale_up);
            color_button_container.setVisibility(View.VISIBLE);
            color_button_container.startAnimation(anim);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_PERMISSION_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }
    }
}