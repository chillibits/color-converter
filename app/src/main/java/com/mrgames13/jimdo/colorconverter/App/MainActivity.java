package com.mrgames13.jimdo.colorconverter.App;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleSeekBarChangedListener;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleTextWatcherUtils;
import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;
import com.mrgames13.jimdo.colorconverter.Utils.StorageUtils;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //Konstanten
    private final int REQ_PICK_COLOR_FROM_IMAGE = 10001;
    private final int REQ_LOAD_COLOR = 10002;
    private final int COLOR_ANIMATION_DURATION = 500;

    //Variablen als Objekte
    private Resources res;
    private Toolbar toolbar;
    private ColorUtils clru;
    private StorageUtils su;

    //Komponenten
    private SeekBar sb_red;
    private SeekBar sb_green;
    private SeekBar sb_blue;
    private TextView tv_r;
    private TextView tv_g;
    private TextView tv_b;
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
    private Button btn_pick;
    private Button btn_pick_random;
    private Button btn_pick_from_image;
    private Button btn_convert_rbg_to_hex;
    private Button btn_convert_hex_to_rgb;
    private Button btn_convert_rgb_to_hsv;
    private Button btn_convert_hex_to_hsv;
    private Button btn_convert_hsv_to_rgb;
    private Button btn_convert_hsv_to_hex;
    private ImageView btn_rgb_copy;
    private ImageView btn_hex_copy;
    private ImageView btn_hsv_copy;
    private ImageView btn_load_color;
    private ImageView btn_save_color;
    private TextView tv_error;
    private Button btn_convert;
    private AlertDialog d;
    private RelativeLayout container;
    private ColorPickerDialog color_picker;
    public static Bitmap selected_image = null;
    private Random random;
    public static Color selected_color = new Color(0, "Selection", android.graphics.Color.BLACK, -1);

    //Variablen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Resourcen initialisieren
        res = getResources();

        //Toolbar initialisieren
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //StorageUtils initialisieren
        su = new StorageUtils(this, res);

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
                if(fromUser) {
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
                if(fromUser) {
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
                if(fromUser) {
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

        btn_pick = findViewById(R.id.pick);
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseColor();
            }
        });

        btn_pick_random = findViewById(R.id.pick_random_color);
        btn_pick_random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomizeColor();
            }
        });

        btn_pick_from_image = findViewById(R.id.pick_from_image);
        btn_pick_from_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, ImageActivity.class), REQ_PICK_COLOR_FROM_IMAGE);
            }
        });

        btn_convert_rbg_to_hex = findViewById(R.id.convert_rgb_to_hex);
        btn_convert_rbg_to_hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HEX();
            }
        });

        btn_convert_hex_to_rgb = findViewById(R.id.convert_hex_to_rgb);
        btn_convert_hex_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2RGB();
            }
        });

        btn_convert_rgb_to_hsv = findViewById(R.id.convert_rgb_to_hsv);
        btn_convert_rgb_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HSV();
            }
        });

        btn_convert_hex_to_hsv = findViewById(R.id.convert_hex_to_hsv);
        btn_convert_hex_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2HSV();
            }
        });

        btn_convert_hsv_to_rgb = findViewById(R.id.convert_hsv_to_rgb);
        btn_convert_hsv_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHSV2RGB();
            }
        });

        btn_convert_hsv_to_hex = findViewById(R.id.convert_hsv_to_hex);
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
        btn_rgb_copy = findViewById(R.id.rgb_copy);
        btn_rgb_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("RGB-Code", tv_rgb.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        btn_hex_copy = findViewById(R.id.hex_copy);
        btn_hex_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HEX-Code", tv_hex.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        btn_hsv_copy = findViewById(R.id.hsv_copy);
        btn_hsv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HSV-Code", tv_hsv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        //Bei Bedarf auf ImageActivity umleiten
        if(getIntent().hasExtra("action") && (getIntent().getStringExtra("action").equals("photo") || getIntent().getStringExtra("action").equals("image"))) {
            Intent i = new Intent(this, ImageActivity.class);
            i.putExtras(getIntent().getExtras());
            startActivityForResult(i, REQ_PICK_COLOR_FROM_IMAGE);
            getIntent().removeExtra("action");
        }

        Intent intent = getIntent();
        if(Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null && intent.getType().startsWith("image/")) {
            try{
                Uri image_uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                selected_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri);
            } catch (Exception e) {
                Toast.makeText(this, res.getString(R.string.error), Toast.LENGTH_SHORT).show();
            }
            startActivityForResult(new Intent(MainActivity.this, ImageActivity.class), REQ_PICK_COLOR_FROM_IMAGE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toolbar Text und Farbe setzen
        getSupportActionBar().setTitle(res.getString(R.string.title_activity_main));
        if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(clru.darkenColor(res.getColor(R.color.colorPrimary)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_rate) {
            rateApp();
        } else if(id == R.id.action_share) {
            recommendApp();
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
        et_name.setHint(res.getString(R.string.choose_name));
        et_name.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

        AlertDialog d = new AlertDialog.Builder(MainActivity.this)
                .setCancelable(true)
                .setTitle(res.getString(R.string.save_color))
                .setView(et_name, 60, 0, 60, 0)
                .setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(res.getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                        String name = et_name.getText().toString().trim();
                        if(!name.equals("")) {
                            selected_color.setName(name);
                            su.saveColor(selected_color);
                        } else {
                            Toast.makeText(MainActivity.this, res.getString(R.string.error), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .create();
        d.show();
    }

    private void randomizeColor() {
        random = new Random(System.currentTimeMillis());
        updateDisplays(new Color(0, "Selection", random.nextInt(256), random.nextInt(256), random.nextInt(256), -1));
    }

    private void chooseColor() {
        color_picker = new ColorPickerDialog(MainActivity.this, android.graphics.Color.parseColor(tv_hex.getText().toString().substring(5)));
        color_picker.setAlphaSliderVisible(false);
        color_picker.setHexValueEnabled(true);
        color_picker.setTitle(res.getString(R.string.pick_color));
        color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                updateDisplays(new Color(0, "Selection", android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color), 0));
            }
        });
        color_picker.show();
    }

    private void updateDisplays(final Color selected_color) {
        //Update RGB TextView
        tv_rgb.setText("RGB: " + String.valueOf(selected_color.getRed()) + ", " + String.valueOf(selected_color.getGreen()) + ", " + String.valueOf(selected_color.getBlue()));
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
        tv_rgb.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        tv_hex.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        tv_hsv.setTextColor(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_rgb_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_hex_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_hsv_copy.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_save_color.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        btn_load_color.setColorFilter(clru.getTextColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue())));
        //Update Container Color

        ValueAnimator red_anim = ValueAnimator.ofInt(sb_red.getProgress(), selected_color.getRed());
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
                    btn_convert.requestFocus();
                }
            }
        });
        et_hex = v.findViewById(R.id.et_hex);
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv_error.setVisibility(View.GONE);
                    selected_color.setRed(Integer.parseInt(et_red.getText().toString()));
                    selected_color.setGreen(Integer.parseInt(et_green.getText().toString()));
                    selected_color.setBlue(Integer.parseInt(et_blue.getText().toString()));
                    if(selected_color.getRed() > 255 || selected_color.getRed() < 0) {
                        et_red.requestFocus();
                        et_red.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(selected_color.getGreen() > 255 || selected_color.getGreen() < 0) {
                        et_green.requestFocus();
                        et_green.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(selected_color.getBlue() > 255 || selected_color.getBlue() < 0) {
                        et_blue.requestFocus();
                        et_blue.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else {
                        et_hex.setText(Integer.toHexString(selected_color.getRed()) + Integer.toHexString(selected_color.getGreen()) + Integer.toHexString(selected_color.getBlue()));
                        container.setBackgroundColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.rgb_to_hex))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(Integer.parseInt(et_red.getText().toString()));
                            sb_green.setProgress(Integer.parseInt(et_green.getText().toString()));
                            sb_blue.setProgress(Integer.parseInt(et_blue.getText().toString()));
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
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
                    btn_convert.requestFocus();
                }
            }
        });
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    } else {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_6));
                        return;
                    }
                    container.setBackgroundColor(android.graphics.Color.parseColor("#" + hex));
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error_hex_chars));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hex_to_rgb))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(Integer.parseInt(et_red.getText().toString()));
                            sb_green.setProgress(Integer.parseInt(et_green.getText().toString()));
                            sb_blue.setProgress(Integer.parseInt(et_blue.getText().toString()));
                        } catch (Exception e) {
                            et_hex.setText(res.getString(R.string.error));
                        }
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
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
                    btn_convert.requestFocus();
                }
            }
        });
        et_h = v.findViewById(R.id.et_h);
        et_s = v.findViewById(R.id.et_s);
        et_v = v.findViewById(R.id.et_v);
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    selected_color.setRed(Integer.parseInt(et_red.getText().toString()));
                    selected_color.setGreen(Integer.parseInt(et_green.getText().toString()));
                    selected_color.setBlue(Integer.parseInt(et_blue.getText().toString()));
                    if(selected_color.getRed() > 255 || selected_color.getRed() < 0) {
                        et_red.requestFocus();
                        et_red.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(selected_color.getGreen() > 255 || selected_color.getGreen() < 0) {
                        et_green.requestFocus();
                        et_green.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(selected_color.getBlue() > 255 || selected_color.getBlue() < 0) {
                        et_blue.requestFocus();
                        et_blue.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.rgb_to_hsv))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(selected_color.getRed());
                            sb_green.setProgress(selected_color.getGreen());
                            sb_blue.setProgress(selected_color.getBlue());
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
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
                    btn_convert.requestFocus();
                }
            }
        });
        et_h = v.findViewById(R.id.et_h);
        et_s = v.findViewById(R.id.et_s);
        et_v = v.findViewById(R.id.et_v);
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    } else {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_6));
                        return;
                    }
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error_hex_chars));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hex_to_hsv))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(selected_color.getRed());
                            sb_green.setProgress(selected_color.getGreen());
                            sb_blue.setProgress(selected_color.getBlue());
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }

    private void convertHSV2RGB() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hsv_to_rgb, null);
        et_h = v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) {
                    et_s.requestFocus();
                }
            }
        });
        et_s = v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) {
                    et_v.requestFocus();
                }
            }
        });
        et_v = v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                    btn_convert.requestFocus();
                }
            }
        });
        et_red = v.findViewById(R.id.et_red);
        et_green = v.findViewById(R.id.et_green);
        et_blue = v.findViewById(R.id.et_blue);
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hsv_to_rgb))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(selected_color.getRed());
                            sb_green.setProgress(selected_color.getGreen());
                            sb_blue.setProgress(selected_color.getBlue());
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }

    private void convertHSV2HEX() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_hsv_to_rgb, null);
        et_h = v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) {
                    et_s.requestFocus();
                }
            }
        });
        et_s = v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) {
                    et_v.requestFocus();
                }
            }
        });
        et_v = v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                    btn_convert.requestFocus();
                }
            }
        });
        et_red = v.findViewById(R.id.et_red);
        et_green = v.findViewById(R.id.et_green);
        et_blue = v.findViewById(R.id.et_blue);
        tv_error = v.findViewById(R.id.error);
        btn_convert = v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    et_hex.setText(Integer.toHexString(selected_color.getRed()) + Integer.toHexString(selected_color.getGreen()) + Integer.toHexString(selected_color.getBlue()));
                    container.setBackgroundColor(android.graphics.Color.rgb(selected_color.getRed(), selected_color.getGreen(), selected_color.getBlue()));
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hsv_to_hex))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(selected_color.getRed());
                            sb_green.setProgress(selected_color.getGreen());
                            sb_blue.setProgress(selected_color.getBlue());
                        } catch (Exception e) {}
                    }
                })
                .setNegativeButton(res.getString(R.string.close), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }

    private void rateApp() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(res.getString(R.string.rate))
                .setMessage(res.getString(R.string.rate_m))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setPositiveButton(res.getString(R.string.rate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final String app_package_name = getPackageName();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + app_package_name)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + app_package_name)));
                        }
                    }
                })
                .setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }

    private void recommendApp() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(res.getString(R.string.share))
                .setMessage(res.getString(R.string.share_m))
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(true)
                .setPositiveButton(res.getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_SEND);
                        i.putExtra(Intent.EXTRA_TEXT, res.getString(R.string.recommend_string));
                        i.setType("text/plain");
                        startActivity(i);
                    }
                })
                .setNegativeButton(res.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        d.show();
    }
}