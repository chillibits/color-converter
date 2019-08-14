package com.mrgames13.jimdo.colorconverter.App;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.instantapps.InstantApps;
import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleSeekBarChangedListener;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleTextWatcherUtils;
import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.Utils.ColorNameUtils;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;
import com.mrgames13.jimdo.colorconverter.Utils.StorageUtils;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Konstanten
    private final int REQ_PICK_COLOR_FROM_IMAGE = 10001;
    private final int REQ_LOAD_COLOR = 10002;

    //Variablen als Objekte
    private Resources res;
    private ColorUtils clru;
    private ColorNameUtils cnu;
    private StorageUtils su;

    //Komponenten
    private SeekBar sb_red;
    private SeekBar sb_green;
    private SeekBar sb_blue;
    private TextView tv_r;
    private TextView tv_g;
    private TextView tv_b;
    private TextView tv_name;
    private TextView tv_rgb;
    private TextView tv_hex;
    private TextView tv_hsv;
    private RelativeLayout color_container;
    private EditText et_red;
    private EditText et_green;
    private EditText et_blue;
    private EditText et_hex;
    private EditText et_h;
    private EditText et_s;
    private EditText et_v;
    private ImageView btn_name_copy;
    private ImageView btn_rgb_copy;
    private ImageView btn_hex_copy;
    private ImageView btn_hsv_copy;
    private ImageView btn_load_color;
    private ImageView btn_save_color;
    private TextView tv_error;
    private AlertDialog d;
    private RelativeLayout container;
    public static Bitmap selected_image = null;
    public static Color selected_color = new Color(0, "Selection", android.graphics.Color.BLACK, -1);

    //Variablen
    private boolean already_converted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Resourcen initialisieren
        res = getResources();

        //Toolbar initialisieren
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //ColorNameUtils initialisieren
        cnu = new ColorNameUtils();

        //StorageUtils initialisieren
        su = new StorageUtils(this);

        //Komponenten initialisieren
        sb_red = findViewById(R.id.color_red);
        sb_green = findViewById(R.id.color_green);
        sb_blue = findViewById(R.id.color_blue);

        sb_red.getProgressDrawable().setColorFilter(res.getColor(R.color.red), PorterDuff.Mode.MULTIPLY);
        sb_green.getProgressDrawable().setColorFilter(res.getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
        sb_blue.getProgressDrawable().setColorFilter(res.getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);

        sb_red.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String value = String.valueOf(progress);
                    tv_r.setText(value);
                    Color tmp = selected_color;
                    tmp.setRed(progress);
                    updateDisplays(tmp);
                }
            }
        });
        sb_green.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String value = String.valueOf(progress);
                    tv_g.setText(value);
                    Color tmp = selected_color;
                    tmp.setGreen(progress);
                    updateDisplays(tmp);
                }
            }
        });
        sb_blue.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    String value = String.valueOf(progress);
                    tv_b.setText(value);
                    Color tmp = selected_color;
                    tmp.setBlue(progress);
                    updateDisplays(tmp);
                }
            }
        });

        tv_r = findViewById(R.id.tv_r);
        tv_g = findViewById(R.id.tv_g);
        tv_b = findViewById(R.id.tv_b);

        tv_name = findViewById(R.id.name_display);
        tv_rgb = findViewById(R.id.rgb_display);
        tv_hex = findViewById(R.id.hex_display);
        tv_hsv = findViewById(R.id.hsv_display);

        color_container = findViewById(R.id.color_container);
        color_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseColor();
            }
        });

        et_red = findViewById(R.id.et_red);
        et_green = findViewById(R.id.et_green);
        et_blue = findViewById(R.id.et_blue);
        et_hex = findViewById(R.id.et_hex);

        Button btn_pick = findViewById(R.id.pick);
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseColor();
            }
        });

        Button btn_pick_random = findViewById(R.id.pick_random_color);
        btn_pick_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomizeColor();
            }
        });

        Button btn_pick_from_image = findViewById(R.id.pick_from_image);
        btn_pick_from_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InstantApps.isInstantApp(MainActivity.this)) {
                    AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                            .setCancelable(true)
                            .setTitle(R.string.install_app)
                            .setMessage(R.string.instant_install_m)
                            .setPositiveButton(R.string.install_app, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    i.putExtra("InstantInstalled", true);
                                    InstantApps.showInstallPrompt(MainActivity.this, i, 10001, "");
                                }
                            })
                            .setNegativeButton(R.string.close, null)
                            .create();
                    d.show();
                } else {
                    startActivityForResult(new Intent(MainActivity.this, ImageActivity.class), REQ_PICK_COLOR_FROM_IMAGE);
                }
            }
        });

        Button btn_convert_rbg_to_hex = findViewById(R.id.convert_rgb_to_hex);
        btn_convert_rbg_to_hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HEX();
            }
        });

        Button btn_convert_hex_to_rgb = findViewById(R.id.convert_hex_to_rgb);
        btn_convert_hex_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2RGB();
            }
        });

        Button btn_convert_rgb_to_hsv = findViewById(R.id.convert_rgb_to_hsv);
        btn_convert_rgb_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HSV();
            }
        });

        Button btn_convert_hex_to_hsv = findViewById(R.id.convert_hex_to_hsv);
        btn_convert_hex_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2HSV();
            }
        });

        Button btn_convert_hsv_to_rgb = findViewById(R.id.convert_hsv_to_rgb);
        btn_convert_hsv_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHSV2RGB();
            }
        });

        Button btn_convert_hsv_to_hex = findViewById(R.id.convert_hsv_to_hex);
        btn_convert_hsv_to_hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHSV2HEX();
            }
        });

        //Farbe Speichern / Laden
        btn_load_color = findViewById(R.id.load_color);
        btn_load_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Farbe laden
                startActivityForResult(new Intent(MainActivity.this, ColorSelectionActivity.class), REQ_LOAD_COLOR);
            }
        });

        btn_save_color = findViewById(R.id.save_color);
        btn_save_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveColor();
            }
        });

        //Codes in Zwischenablage kopieren
        btn_name_copy = findViewById(R.id.name_copy);
        btn_name_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy_string = tv_name.getText().toString();
                copy_string = copy_string.substring(copy_string.indexOf(":") + 2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Color name", copy_string);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        btn_rgb_copy = findViewById(R.id.rgb_copy);
        btn_rgb_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy_string = tv_rgb.getText().toString();
                copy_string = copy_string.substring(copy_string.indexOf(":") + 2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("RGB-Code", copy_string);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        btn_hex_copy = findViewById(R.id.hex_copy);
        btn_hex_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy_string = tv_hex.getText().toString();
                copy_string = copy_string.substring(copy_string.indexOf(":") + 2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HEX-Code", copy_string);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        btn_hsv_copy = findViewById(R.id.hsv_copy);
        btn_hsv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy_string = tv_hsv.getText().toString();
                copy_string = copy_string.substring(copy_string.indexOf(":") + 2);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HSV-Code", copy_string);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
            }
        });

        //Bei Bedarf auf ImageActivity umleiten
        if (getIntent().hasExtra("action") && (getIntent().getStringExtra("action").equals("photo") || getIntent().getStringExtra("action").equals("image"))) {
            Intent i = new Intent(this, ImageActivity.class);
            i.putExtras(getIntent().getExtras());
            startActivityForResult(i, REQ_PICK_COLOR_FROM_IMAGE);
            getIntent().removeExtra("action");
        }

        Intent intent = getIntent();
        if (intent.getBooleanExtra("InstantInstalled", false)) {
            AlertDialog d = new AlertDialog.Builder(this)
                    .setTitle(R.string.instant_installed_t)
                    .setMessage(R.string.instant_installed_m)
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, null)
                    .create();
            d.show();
        } else if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            try {
                Uri image_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                selected_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            } catch (Exception e) {
                Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            }
            startActivityForResult(new Intent(MainActivity.this, ImageActivity.class), REQ_PICK_COLOR_FROM_IMAGE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toolbar Text und Farbe setzen
        getSupportActionBar().setTitle(R.string.title_activity_main);
        if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(clru.darkenColor(res.getColor(R.color.colorPrimary)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        if(InstantApps.isInstantApp(this)) menu.getItem(0).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_rate) {
            rateApp();
        } else if(id == R.id.action_share) {
            recommendApp();
        } else if(id == R.id.action_install) {
            AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                    .setCancelable(true)
                    .setTitle(R.string.install_app)
                    .setMessage(R.string.install_app_download)
                    .setPositiveButton(R.string.install_app, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            i.putExtra("InstantInstalled", true);
                            InstantApps.showInstallPrompt(MainActivity.this, i, 10001, "");
                        }
                    })
                    .setNegativeButton(R.string.close, null)
                    .create();
            d.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_PICK_COLOR_FROM_IMAGE && resultCode == RESULT_OK) {
            updateDisplays(new Color(0, "Selection", data.getIntExtra("Color", 0), -1));
        } else if(requestCode == REQ_LOAD_COLOR && resultCode == RESULT_OK) {
            updateDisplays(new Color(0, "Selection", data.getIntExtra("Color", 0), -1));
        }
    }

    private void saveColor() {
        //Farbe speichern
        final EditText et_name = new EditText(MainActivity.this);
        et_name.setHint(R.string.choose_name);
        et_name.setText(cnu.getColorNameFromColor(selected_color));
        et_name.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        @SuppressLint("RestrictedApi")
        AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(true)
                .setTitle(R.string.save_color)
                .setView(et_name, 60, 0, 60, 0)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        String name = et_name.getText().toString().trim();
                        if(!name.equals("")) {
                            selected_color.setName(name);
                            su.saveColor(selected_color);
                        } else {
                            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();
        d.show();
    }

    private void randomizeColor() {
        Random random = new Random(System.currentTimeMillis());
        updateDisplays(new Color(0, "Selection", random.nextInt(256), random.nextInt(256), random.nextInt(256), -1));
    }

    private void chooseColor() {
        ColorPickerDialog color_picker = new ColorPickerDialog(MainActivity.this, android.graphics.Color.parseColor(tv_hex.getText().toString().substring(5)));
        color_picker.setAlphaSliderVisible(false);
        color_picker.setHexValueEnabled(true);
        color_picker.setTitle(R.string.pick_color);
        color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                updateDisplays(new Color(0, "Selection", android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color), 0));
            }
        });
        color_picker.show();
    }

    private void updateDisplays(final Color selected_color) {
        //Update RGB TextViews
        tv_r.setText(String.valueOf(selected_color.getRed()));
        tv_g.setText(String.valueOf(selected_color.getGreen()));
        tv_b.setText(String.valueOf(selected_color.getBlue()));
        //Update Name TextView
        tv_name.setText(getString(R.string.color_name).concat(": ").concat(cnu.getColorNameFromColor(selected_color)));
        //Update RGB TextView
        tv_rgb.setText("RGB: " + selected_color.getRed() + ", " + selected_color.getGreen() + ", " + selected_color.getBlue());
        //Update HEX TextView
        String hex_red = Integer.toHexString(selected_color.getRed()).toUpperCase();
        if(hex_red.length() < 2) hex_red = "0" + hex_red;
        String hex_green = Integer.toHexString(selected_color.getGreen()).toUpperCase();
        if(hex_green.length() < 2) hex_green = "0" + hex_green;
        String hex_blue = Integer.toHexString(selected_color.getBlue()).toUpperCase();
        if(hex_blue.length() < 2) hex_blue = "0" + hex_blue;
        tv_hex.setText("HEX: #" + hex_red + hex_green + hex_blue);
        //Update HSV TextView
        float[] hsv = new float[3];
        android.graphics.Color.RGBToHSV(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue(), hsv);
        tv_hsv.setText("HSV: " + String.format("%.02f", hsv[0]) + ", " + String.format("%.02f", hsv[1]) + ", " + String.format("%.02f", hsv[2]));
        //Update TextColors
        tv_name.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        tv_rgb.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        tv_hex.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        tv_hsv.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_name_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_rgb_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_hex_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_hsv_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_save_color.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_load_color.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        //Update Container Color

        ValueAnimator red_anim = ValueAnimator.ofInt(sb_red.getProgress(), selected_color.getRed());
        int COLOR_ANIMATION_DURATION = 500;
        red_anim.setDuration(COLOR_ANIMATION_DURATION);
        red_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sb_red.setProgress((int) valueAnimator.getAnimatedValue());
                color_container.setBackgroundColor(android.graphics.Color.argb(255, sb_red.getProgress(), sb_green.getProgress(), sb_blue.getProgress()));
            }
        });
        red_anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {}

            @Override
            public void onAnimationEnd(Animator animator) {
                MainActivity.selected_color = selected_color;
            }

            @Override
            public void onAnimationCancel(Animator animator) {}

            @Override
            public void onAnimationRepeat(Animator animator) {}
        });
        red_anim.start();

        ValueAnimator green_anim = ValueAnimator.ofInt(sb_green.getProgress(), selected_color.getGreen());
        green_anim.setDuration(COLOR_ANIMATION_DURATION);
        green_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sb_green.setProgress((int) valueAnimator.getAnimatedValue());
            }
        });
        green_anim.start();

        ValueAnimator blue_anim = ValueAnimator.ofInt(sb_blue.getProgress(), selected_color.getBlue());
        blue_anim.setDuration(COLOR_ANIMATION_DURATION);
        blue_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sb_blue.setProgress((int) valueAnimator.getAnimatedValue());
            }
        });
        blue_anim.start();
    }

    //----------------------------------------Konvertierung-----------------------------------------

    private void convertRGB2HEX() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_rgb_to_hex, null);
        et_red = v.findViewById(R.id.et_red);
        et_red.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_red.getText().toString().length() >= 3) et_green.requestFocus();
            }
        });
        et_green = v.findViewById(R.id.et_green);
        et_green.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_green.getText().toString().length() >= 3) et_blue.requestFocus();
            }
        });
        et_blue = v.findViewById(R.id.et_blue);
        et_blue.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_blue.getText().toString().length() >= 3) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                }
            }
        });
        et_hex = v.findViewById(R.id.et_hex);
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(this)
                .setTitle(R.string.rgb_to_hex)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert,null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", Integer.parseInt(et_red.getText().toString()), Integer.parseInt(et_green.getText().toString()), Integer.parseInt(et_blue.getText().toString()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try {
                        tv_error.setVisibility(View.GONE);
                        selected_color.setRed(Integer.parseInt(et_red.getText().toString()));
                        selected_color.setGreen(Integer.parseInt(et_green.getText().toString()));
                        selected_color.setBlue(Integer.parseInt(et_blue.getText().toString()));
                        if(selected_color.getRed() > 255 || selected_color.getRed() < 0) {
                            et_red.requestFocus();
                            et_red.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else if(selected_color.getGreen() > 255 || selected_color.getGreen() < 0) {
                            et_green.requestFocus();
                            et_green.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else if(selected_color.getBlue() > 255 || selected_color.getBlue() < 0) {
                            et_blue.requestFocus();
                            et_blue.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else {
                            et_hex.setText(Integer.toHexString(selected_color.getRed()) + Integer.toHexString(selected_color.getGreen()) + Integer.toHexString(selected_color.getBlue()));
                            container.setBackgroundColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue()));
                            d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                            already_converted = true;
                        }
                    } catch (Exception e) {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error);
                    }
                }
            }
        });
    }

    private void convertHEX2RGB() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hex_to_rgb, null);
        et_red = v.findViewById(R.id.et_red);
        et_green = v.findViewById(R.id.et_green);
        et_blue = v.findViewById(R.id.et_blue);
        et_hex = v.findViewById(R.id.et_hex);
        et_hex.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_hex.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                }
            }
        });
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(this)
                .setTitle(R.string.hex_to_rgb)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert, null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", Integer.parseInt(et_red.getText().toString()), Integer.parseInt(et_green.getText().toString()), Integer.parseInt(et_blue.getText().toString()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try{
                        tv_error.setVisibility(View.GONE);
                        String hex = et_hex.getText().toString().trim();
                        if(hex.length() == 6) {
                            selected_color.setRed(Integer.valueOf(hex.substring(0, 2), 16));
                            selected_color.setGreen(Integer.valueOf(hex.substring(2, 4), 16));
                            selected_color.setBlue(Integer.valueOf(hex.substring(4, 6), 16));
                            et_red.setText(String.valueOf(selected_color.getRed()));
                            et_green.setText(String.valueOf(selected_color.getGreen()));
                            et_blue.setText(String.valueOf(selected_color.getBlue()));
                            container.setBackgroundColor(android.graphics.Color.parseColor("#" + hex));
                            d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                            already_converted = true;
                        } else {
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_6);
                        }
                    } catch (Exception e) {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error_hex_chars);
                    }
                }
            }
        });
    }

    private void convertRGB2HSV() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_rgb_to_hsv, null);
        et_red = v.findViewById(R.id.et_red);
        et_red.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_red.getText().toString().length() >= 3) et_green.requestFocus();
            }
        });
        et_green = v.findViewById(R.id.et_green);
        et_green.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_green.getText().toString().length() >= 3) et_blue.requestFocus();
            }
        });
        et_blue = v.findViewById(R.id.et_blue);
        et_blue.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_blue.getText().toString().length() >= 3) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                }
            }
        });
        et_h = v.findViewById(R.id.et_h);
        et_s = v.findViewById(R.id.et_s);
        et_v = v.findViewById(R.id.et_v);
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(this)
                .setTitle(R.string.rgb_to_hsv)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert, null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", Integer.parseInt(et_red.getText().toString()), Integer.parseInt(et_green.getText().toString()), Integer.parseInt(et_blue.getText().toString()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try {
                        tv_error.setVisibility(View.GONE);
                        selected_color.setRed(Integer.parseInt(et_red.getText().toString()));
                        selected_color.setGreen(Integer.parseInt(et_green.getText().toString()));
                        selected_color.setBlue(Integer.parseInt(et_blue.getText().toString()));
                        if(selected_color.getRed() > 255 || selected_color.getRed() < 0) {
                            et_red.requestFocus();
                            et_red.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else if(selected_color.getGreen() > 255 || selected_color.getGreen() < 0) {
                            et_green.requestFocus();
                            et_green.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else if(selected_color.getBlue() > 255 || selected_color.getBlue() < 0) {
                            et_blue.requestFocus();
                            et_blue.setText(null);
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_between);
                        } else {
                            String hex_red = Integer.toHexString(selected_color.getRed());
                            if(hex_red.length() < 2) hex_red = "0" + hex_red;
                            String hex_green = Integer.toHexString(selected_color.getGreen());
                            if(hex_green.length() < 2) hex_green = "0" + hex_green;
                            String hex_blue = Integer.toHexString(selected_color.getBlue());
                            if(hex_blue.length() < 2) hex_blue = "0" + hex_blue;
                            float[] hsv = new float[3];
                            android.graphics.Color.RGBToHSV(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue(), hsv);
                            et_h.setText(String.format("%.02f", hsv[0]));
                            et_s.setText(String.format("%.02f", hsv[1]));
                            et_v.setText(String.format("%.02f", hsv[2]));
                            container.setBackgroundColor(android.graphics.Color.parseColor("#" + hex_red + hex_green + hex_blue));
                            d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                            already_converted = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error);
                    }
                }
            }
        });
    }

    private void convertHEX2HSV() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hex_to_hsv, null);
        et_hex = v.findViewById(R.id.et_hex);
        et_hex.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_hex.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_hex.getWindowToken(), 0);
                }
            }
        });
        et_h = v.findViewById(R.id.et_h);
        et_s = v.findViewById(R.id.et_s);
        et_v = v.findViewById(R.id.et_v);
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.hex_to_hsv)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert, null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", android.graphics.Color.parseColor("#" + et_hex.getText().toString().trim()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try {
                        tv_error.setVisibility(View.GONE);
                        String hex = et_hex.getText().toString().trim();
                        if(hex.length() == 6) {
                            selected_color.setRed(Integer.valueOf(hex.substring(0, 2), 16));
                            selected_color.setGreen(Integer.valueOf(hex.substring(2, 4), 16));
                            selected_color.setBlue(Integer.valueOf(hex.substring(4, 6), 16));
                            float[] hsv = new float[3];
                            android.graphics.Color.RGBToHSV(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue(), hsv);
                            et_h.setText(String.format("%.02f", hsv[0]));
                            et_s.setText(String.format("%.02f", hsv[1]));
                            et_v.setText(String.format("%.02f", hsv[2]));
                            container.setBackgroundColor(android.graphics.Color.parseColor("#" + hex));
                            d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                            already_converted = true;
                        } else {
                            tv_error.setVisibility(View.VISIBLE);
                            tv_error.setText(R.string.error_number_6);
                        }
                    } catch (Exception e) {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error_hex_chars);
                    }
                }
            }
        });
    }

    private void convertHSV2RGB() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hsv_to_rgb, null);
        et_h = v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) et_s.requestFocus();
            }
        });
        et_s = v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) et_v.requestFocus();
            }
        });
        et_v = v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                }
            }
        });
        et_red = v.findViewById(R.id.et_red);
        et_green = v.findViewById(R.id.et_green);
        et_blue = v.findViewById(R.id.et_blue);
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(this)
                .setTitle(R.string.hsv_to_rgb)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert, null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", Integer.parseInt(et_red.getText().toString()), Integer.parseInt(et_green.getText().toString()), Integer.parseInt(et_blue.getText().toString()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try {
                        tv_error.setVisibility(View.GONE);
                        float[] hsv = new float[3];
                        hsv[0] = Float.parseFloat(et_h.getText().toString().trim());
                        hsv[1] = Float.parseFloat(et_s.getText().toString().trim());
                        hsv[2] = Float.parseFloat(et_v.getText().toString().trim());
                        int c = android.graphics.Color.HSVToColor(hsv);
                        selected_color.setRed(android.graphics.Color.red(c));
                        et_red.setText(String.valueOf(selected_color.getRed()));
                        selected_color.setGreen(android.graphics.Color.green(c));
                        et_green.setText(String.valueOf(selected_color.getGreen()));
                        selected_color.setBlue(android.graphics.Color.blue(c));
                        et_blue.setText(String.valueOf(selected_color.getBlue()));
                        container.setBackgroundColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue()));
                        d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                        already_converted = true;
                    } catch (Exception e) {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error);
                    }
                }
            }
        });
    }

    private void convertHSV2HEX() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hsv_to_hex, null);
        et_h = v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) et_s.requestFocus();
            }
        });
        et_s = v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) et_v.requestFocus();
            }
        });
        et_v = v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                }
            }
        });
        et_hex = v.findViewById(R.id.et_hex);
        tv_error = v.findViewById(R.id.error);
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(this)
                .setTitle(R.string.hsv_to_hex)
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(R.string.convert, null)
                .setNegativeButton(R.string.close, null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        already_converted = false;
                    }
                })
                .create();
        d.show();
        d.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already_converted) {
                    //If already converted, commit
                    updateDisplays(new Color(0, "Selection", android.graphics.Color.parseColor("#" + et_hex.getText().toString().trim()), System.currentTimeMillis()));
                    d.dismiss();
                } else {
                    //If not already converted, convert
                    try {
                        tv_error.setVisibility(View.GONE);
                        float[] hsv = new float[3];
                        hsv[0] = Float.parseFloat(et_h.getText().toString().trim());
                        hsv[1] = Float.parseFloat(et_s.getText().toString().trim());
                        hsv[2] = Float.parseFloat(et_v.getText().toString().trim());
                        int c = android.graphics.Color.HSVToColor(hsv);
                        selected_color.setRed(android.graphics.Color.red(c));
                        selected_color.setGreen(android.graphics.Color.green(c));
                        selected_color.setBlue(android.graphics.Color.blue(c));
                        et_hex.setText(String.format("%06X", (0xFFFFFF & c)));
                        container.setBackgroundColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue()));
                        d.getButton(DialogInterface.BUTTON_POSITIVE).setText(getString(R.string.enter));
                        already_converted = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(R.string.error);
                    }
                }
            }
        });
    }

    private void rateApp() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.rate)
                .setMessage(R.string.rate_m)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setPositiveButton(R.string.rate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String app_package_name = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_package_name)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_package_name)));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        d.show();
    }

    private void recommendApp() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.share)
                .setMessage(R.string.share_m)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setPositiveButton(R.string.share, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_string));
                        i.setType("text/plain");
                        startActivity(i);
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
        d.show();
    }
}