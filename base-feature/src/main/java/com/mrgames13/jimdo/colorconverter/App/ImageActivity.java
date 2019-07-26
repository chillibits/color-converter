package com.mrgames13.jimdo.colorconverter.App;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.provider.MediaStore;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;
import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;
import com.mrgames13.jimdo.colorconverter.Utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class ImageActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    //Konstanten
    private final int REQ_CAMERA_CHOOSER = 10002;
    private final int REQ_PERMISSION_READ_EXTERNAL_STORAGE = 10003;
    private final int REQ_PERMISSION_CAMERA = 10004;

    //Variablen als Objekte
    private Resources res;
    private ColorUtils clru;
    private NetworkUtils nwu;
    private ShowcaseView.Builder showcase_view;
    private Bitmap choosed_image = null;

    //Komponenten
    private ImageView image;
    private LinearLayout button_container;
    private RelativeLayout color_button_container;
    private ImageView vibrant_color;
    private ImageView light_vibrant_color;
    private ImageView dark_vibrant_color;
    private ImageView muted_color;
    private ImageView light_muted_color;
    private ImageView dark_muted_color;
    private ImageView selected_color;

    //Variablen
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

        //Toolbar initialisieren
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //NetworkUtils initialisieren
        nwu = new NetworkUtils(this);

        //Komponenten initialisieren
        image = findViewById(R.id.image);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (image.getDrawable() != null && (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE)) {
                    float eventX = event.getX();
                    float eventY = event.getY();
                    float[] eventXY = new float[]{eventX, eventY};

                    Matrix invertMatrix = new Matrix();
                    image.getImageMatrix().invert(invertMatrix);

                    invertMatrix.mapPoints(eventXY);
                    int x = (int) eventXY[0];
                    int y = (int) eventXY[1];

                    Drawable imgDrawable = image.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) imgDrawable).getBitmap();

                    //Limit x, y range within bitmap
                    if (x < 0) {
                        x = 0;
                    } else if (x > bitmap.getWidth() - 1) {
                        x = bitmap.getWidth() - 1;
                    }

                    if (y < 0) {
                        y = 0;
                    } else if (y > bitmap.getHeight() - 1) {
                        y = bitmap.getHeight() - 1;
                    }

                    int_choosed_color = bitmap.getPixel(x, y);
                    MainActivity.selected_color = new Color(0, "Selection", int_choosed_color, -1);
                    selected_color.setBackgroundColor(int_choosed_color);
                }
                return true;
            }
        });
        button_container = findViewById(R.id.button_container);
        ImageView choose_from_gallery = findViewById(R.id.choose_from_gallery);
        choose_from_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromGallery();
            }
        });
        ImageView choose_from_camera = findViewById(R.id.choose_from_camera);
        choose_from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImageFromCamera();
            }
        });
        ImageView choose_from_web = findViewById(R.id.choose_from_web);
        choose_from_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImageFromWeb();
            }
        });
        color_button_container = findViewById(R.id.color_button_container);
        vibrant_color = findViewById(R.id.vibrant_color);
        vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        light_vibrant_color = findViewById(R.id.light_vibrant_color);
        light_vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_light_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        dark_vibrant_color = findViewById(R.id.dark_vibrant_color);
        dark_vibrant_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_dark_vibrant_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        muted_color = findViewById(R.id.muted_color);
        muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        light_muted_color = findViewById(R.id.light_muted_color);
        light_muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_light_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        dark_muted_color = findViewById(R.id.dark_muted_color);
        dark_muted_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("Color", int_dark_muted_color);
                setResult(RESULT_OK, data);
                finish();
            }
        });
        selected_color = findViewById(R.id.selected_color);
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
                .withNewStyleShowcase()
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
                                .withNewStyleShowcase()
                                .setTarget(new ViewTarget(findViewById(R.id.choose_from_camera)))
                                .setContentTitle(R.string.capture_image)
                                .setContentText(R.string.instructions_photo_m)
                                .setStyle(R.style.showCaseView)
                                .blockAllTouches()
                                .singleShot(2)
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        showcase_view = new ShowcaseView.Builder(ImageActivity.this)
                                                .withNewStyleShowcase()
                                                .setTarget(new ViewTarget(findViewById(R.id.choose_from_web)))
                                                .setContentTitle(R.string.image_from_web)
                                                .setContentText(R.string.instructions_web_m)
                                                .setStyle(R.style.showCaseView)
                                                .blockAllTouches()
                                                .singleShot(3);
                                        showcase_view.build();
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                                    }

                                    @Override
                                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                                    }
                                });
                        showcase_view.build();
                    }

                    @Override
                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
                    }

                    @Override
                    public void onShowcaseViewShow(ShowcaseView showcaseView) {
                    }

                    @Override
                    public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {
                    }
                });
        showcase_view.build();

        if (MainActivity.selected_image != null) {
            try {
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
                selected_color.setBackgroundColor(android.graphics.Color.argb(255, MainActivity.selected_color.getRed(), MainActivity.selected_color.getGreen(), MainActivity.selected_color.getBlue()));

                button_container.setVisibility(View.GONE);
                color_button_container.setVisibility(View.VISIBLE);
            } catch (Exception e) {
            }
        }

        if (ActivityCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_PERMISSION_READ_EXTERNAL_STORAGE);
        }

        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
            //Bei ShareIntent von anderer App, Bild direkt anzeigen
            if (intent.getType().startsWith("image/")) {
                try {
                    Uri image_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                    choosed_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
                    MainActivity.selected_image = choosed_image;

                    setBitmapToImageView();
                } catch (Exception e) {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        } else {
            //Bei Shortcut-Aufruf auf Unterfunktion weiterleiten
            if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("photo")) {
                chooseImageFromCamera();
            } else if (getIntent().hasExtra("action") && getIntent().getStringExtra("action").equals("image")) {
                chooseImageFromGallery();
            }
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
        if (id == R.id.action_galery) {
            chooseImageFromGallery();
        } else if (id == R.id.action_capture) {
            chooseImageFromCamera();
        } else if (id == R.id.action_web) {
            chooseImageFromWeb();
        } else if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toolbar Text und Farbe setzen
        getSupportActionBar().setTitle(R.string.title_activity_image);
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().setStatusBarColor(clru.darkenColor(res.getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void chooseImageFromGallery() {
        BSImagePicker picker = new BSImagePicker.Builder("com.mrgames13.jimdo.colorconverter.fileprovider")
                .hideCameraTile()
                .build();
        picker.show(getSupportFragmentManager(), "picker");
    }

    private void chooseImageFromCamera() {
        if (ActivityCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
            takePictureIntent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoURI;
                        if (Build.VERSION.SDK_INT >= 24) {
                            photoURI = FileProvider.getUriForFile(this, "com.mrgames13.jimdo.colorconverter.fileprovider", photoFile);
                        } else {
                            photoURI = Uri.fromFile(photoFile);
                        }
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQ_CAMERA_CHOOSER);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            ActivityCompat.requestPermissions(ImageActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_PERMISSION_CAMERA);
        }
    }

    private void chooseImageFromWeb() {
        final EditText et_url = new EditText(this);
        et_url.setHint(R.string.enter_web_url);
        et_url.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        @SuppressLint("RestrictedApi")
        AlertDialog d = new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(R.string.download_img)
                .setView(et_url, 60, 0, 60, 0)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.download_img, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nwu.isInternetAvailable()) {
                            String url = et_url.getText().toString().trim();
                            if (nwu.isUrl(url)) {
                                image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                Glide.with(ImageActivity.this)
                                        .load(url)
                                        .addListener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                choosed_image = ((BitmapDrawable) resource).getBitmap();
                                                setBitmapToImageView();
                                                return false;
                                            }
                                        })
                                        .into(image);
                            } else {
                                Toast.makeText(ImageActivity.this, R.string.malformed_url, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ImageActivity.this, R.string.no_internet_available, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();
        d.show();
    }

    private File createImageFile() {
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
            MainActivity.selected_image = choosed_image = BitmapFactory.decodeFile(current_photo_path);
            setBitmapToImageView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQ_PERMISSION_CAMERA && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseImageFromCamera();
        } else if(requestCode == REQ_PERMISSION_READ_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            finish();
        }
    }

    private void setBitmapToImageView() {
        try {
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);

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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ImageActivity.this, R.string.image_broken, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSingleImageSelected(Uri uri, String tag) {
        try {
            MainActivity.selected_image = choosed_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            setBitmapToImageView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}