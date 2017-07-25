package com.mrgames13.jimdo.colorconverter.App;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
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

import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleSeekBarChangedListener;
import com.mrgames13.jimdo.colorconverter.HelpClasses.SimpleTextWatcherUtils;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

public class MainActivity extends AppCompatActivity {

    //Konstanten
    private final int REQ_PICK_COLOR_FROM_IMAGE = 10001;

    //Variablen als Objekte
    private Resources res;
    private Toolbar toolbar;
    private ColorUtils clru;

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
    private Button btn_pick_from_image;
    private Button btn_convert_rbg_to_hex;
    private Button btn_convert_hex_to_rgb;
    private Button btn_convert_rgb_to_hsv;
    private Button btn_convert_hex_to_hsv;
    private Button btn_convert_hsv_to_rgb;
    private Button btn_convert_hsv_to_hex;
    private TextView tv_error;
    private Button btn_convert;
    private AlertDialog d;
    private RelativeLayout container;
    private ColorPickerDialog color_picker;
    public static Bitmap selected_image = null;
    public static int selected_image_color;

    //Variablen
    private int red;
    private int green;
    private int blue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Resourcen initialisieren
        res = getResources();

        //Toolbar initialisieren
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //Komponenten initialisieren
        sb_red = (SeekBar) findViewById(R.id.color_red);
        sb_green = (SeekBar) findViewById(R.id.color_green);
        sb_blue = (SeekBar) findViewById(R.id.color_blue);

        sb_red.getProgressDrawable().setColorFilter(res.getColor(R.color.red), android.graphics.PorterDuff.Mode.SRC_IN);
        sb_green.getProgressDrawable().setColorFilter(res.getColor(R.color.green), android.graphics.PorterDuff.Mode.SRC_IN);
        sb_blue.getProgressDrawable().setColorFilter(res.getColor(R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);

        sb_red.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                red = progress;
                String value = String.valueOf(red);
                tv_r.setText(value);
                updateDisplays();
            }
        });
        sb_green.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                green = progress;
                String value = String.valueOf(green);
                tv_g.setText(value);
                updateDisplays();
            }
        });
        sb_blue.setOnSeekBarChangeListener(new SimpleSeekBarChangedListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blue = progress;
                String value = String.valueOf(blue);
                tv_b.setText(value);
                updateDisplays();
            }
        });

        tv_r = (TextView) findViewById(R.id.tv_r);
        tv_g = (TextView) findViewById(R.id.tv_g);
        tv_b = (TextView) findViewById(R.id.tv_b);

        tv_rgb = (TextView) findViewById(R.id.rgb_display);
        tv_hex = (TextView) findViewById(R.id.hex_display);
        tv_hsv = (TextView) findViewById(R.id.hsv_display);

        color_container = (RelativeLayout) findViewById(R.id.color_container);

        et_red = (EditText) findViewById(R.id.et_red);
        et_green = (EditText) findViewById(R.id.et_green);
        et_blue = (EditText) findViewById(R.id.et_blue);
        et_hex = (EditText) findViewById(R.id.et_hex);

        btn_pick = (Button) findViewById(R.id.pick);
        btn_pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                color_picker = new ColorPickerDialog(MainActivity.this, Color.parseColor(tv_hex.getText().toString().substring(5)));
                color_picker.setAlphaSliderVisible(false);
                color_picker.setHexValueEnabled(true);
                color_picker.setTitle(res.getString(R.string.pick_color));
                color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        sb_red.setProgress(Color.red(color));
                        sb_green.setProgress(Color.green(color));
                        sb_blue.setProgress(Color.blue(color));
                    }
                });
                color_picker.show();
            }
        });

        btn_pick_from_image = (Button) findViewById(R.id.pick_from_image);
        btn_pick_from_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, ImageActivity.class), REQ_PICK_COLOR_FROM_IMAGE);
            }
        });

        btn_convert_rbg_to_hex = (Button) findViewById(R.id.convert_rgb_to_hex);
        btn_convert_rbg_to_hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HEX();
            }
        });

        btn_convert_hex_to_rgb = (Button) findViewById(R.id.convert_hex_to_rgb);
        btn_convert_hex_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2RGB();
            }
        });

        btn_convert_rgb_to_hsv = (Button) findViewById(R.id.convert_rgb_to_hsv);
        btn_convert_rgb_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertRGB2HSV();
            }
        });

        btn_convert_hex_to_hsv = (Button) findViewById(R.id.convert_hex_to_hsv);
        btn_convert_hex_to_hsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHEX2HSV();
            }
        });

        btn_convert_hsv_to_rgb = (Button) findViewById(R.id.convert_hsv_to_rgb);
        btn_convert_hsv_to_rgb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHSV2RGB();
            }
        });

        btn_convert_hsv_to_hex = (Button) findViewById(R.id.convert_hsv_to_hex);
        btn_convert_hsv_to_hex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertHSV2HEX();
            }
        });

        //Codes in Zwischenablage kopieren

        ImageView btn_rgb_copy = (ImageView) findViewById(R.id.rgb_copy);
        btn_rgb_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("RGB-Code", tv_rgb.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btn_hex_copy = (ImageView) findViewById(R.id.hex_copy);
        btn_hex_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HEX-Code", tv_hex.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });

        ImageView btn_hsv_copy = (ImageView) findViewById(R.id.hsv_copy);
        btn_hsv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("HSV-Code", tv_hsv.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, res.getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
            }
        });
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
            int selected_color = data.getIntExtra("Color", 0);
            //Empfangenen Daten auswerten
            if(selected_color != 0) {
                sb_red.setProgress(Color.red(selected_color));
                sb_green.setProgress(Color.green(selected_color));
                sb_blue.setProgress(Color.blue(selected_color));
            }
        }
    }

    private void updateDisplays() {
        //Update RGB TextView
        tv_rgb.setText("RGB: " + String.valueOf(red) + ", " + String.valueOf(green) + ", " + String.valueOf(blue));
        //Update HEX TextView
        String hex_red = Integer.toHexString(red).toUpperCase();
        if(hex_red.length() < 2) hex_red = "0" + hex_red;
        String hex_green = Integer.toHexString(green).toUpperCase();
        if(hex_green.length() < 2) hex_green = "0" + hex_green;
        String hex_blue = Integer.toHexString(blue).toUpperCase();
        if(hex_blue.length() < 2) hex_blue = "0" + hex_blue;
        tv_hex.setText("HEX: #" + hex_red + hex_green + hex_blue);
        //Update HSV TextView
        float[] hsv = new float[3];
        Color.RGBToHSV(red, green, blue, hsv);
        tv_hsv.setText("HSV: " + String.format("%.02f", hsv[0]) + ", " + String.format("%.02f", hsv[1]) + ", " + String.format("%.02f", hsv[2]));
        tv_rgb.setTextColor(clru.getComplimentary(Color.rgb(red, green, blue)));
        tv_hex.setTextColor(clru.getComplimentary(Color.rgb(red, green, blue)));
        tv_hsv.setTextColor(clru.getComplimentary(Color.rgb(red, green, blue)));
        //Update Container Color
        color_container.setBackgroundColor(Color.parseColor("#" + hex_red + hex_green + hex_blue));
    }

    //----------------------------------------Konvertierung-----------------------------------------

    private void convertRGB2HEX() {
        View v;
        v = getLayoutInflater().inflate(R.layout.dialog_rgb_to_hex, null);
        et_red = (EditText) v.findViewById(R.id.et_red);
        et_red.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_red.getText().toString().length() >= 3) et_green.requestFocus();
            }
        });
        et_green = (EditText) v.findViewById(R.id.et_green);
        et_green.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_green.getText().toString().length() >= 3) et_blue.requestFocus();
            }
        });
        et_blue = (EditText) v.findViewById(R.id.et_blue);
        et_blue.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_blue.getText().toString().length() >= 3) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        et_hex = (EditText) v.findViewById(R.id.et_hex);
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv_error.setVisibility(View.GONE);
                    red = Integer.parseInt(et_red.getText().toString());
                    green = Integer.parseInt(et_green.getText().toString());
                    blue = Integer.parseInt(et_blue.getText().toString());
                    if(red > 255 || red < 0) {
                        et_red.requestFocus();
                        et_red.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(green > 255 || green < 0) {
                        et_green.requestFocus();
                        et_green.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(blue > 255 || blue < 0) {
                        et_blue.requestFocus();
                        et_blue.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else {
                        et_hex.setText(Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue));
                        container.setBackgroundColor(Color.rgb(red, green, blue));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

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
        et_red = (EditText) v.findViewById(R.id.et_red);
        et_green = (EditText) v.findViewById(R.id.et_green);
        et_blue = (EditText) v.findViewById(R.id.et_blue);
        et_hex = (EditText) v.findViewById(R.id.et_hex);
        et_hex.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_hex.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    tv_error.setVisibility(View.GONE);
                    String hex = et_hex.getText().toString().trim();
                    if(hex.length() == 6) {
                        red = Integer.valueOf(hex.substring(0, 2), 16);
                        green = Integer.valueOf(hex.substring(2, 4), 16);
                        blue = Integer.valueOf(hex.substring(4, 6), 16);
                        et_red.setText(String.valueOf(red));
                        et_green.setText(String.valueOf(green));
                        et_blue.setText(String.valueOf(blue));
                    } else {
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_6));
                        return;
                    }
                    container.setBackgroundColor(Color.parseColor("#" + hex));
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error_hex_chars));
                }
            }
        });
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

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
        et_red = (EditText) v.findViewById(R.id.et_red);
        et_red.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_red.getText().toString().length() >= 3) et_green.requestFocus();
            }
        });
        et_green = (EditText) v.findViewById(R.id.et_green);
        et_green.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_green.getText().toString().length() >= 3) et_blue.requestFocus();
            }
        });
        et_blue = (EditText) v.findViewById(R.id.et_blue);
        et_blue.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_blue.getText().toString().length() >= 3) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_blue.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        et_h = (EditText) v.findViewById(R.id.et_h);
        et_s = (EditText) v.findViewById(R.id.et_s);
        et_v = (EditText) v.findViewById(R.id.et_v);
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    red = Integer.parseInt(et_red.getText().toString());
                    green = Integer.parseInt(et_green.getText().toString());
                    blue = Integer.parseInt(et_blue.getText().toString());
                    if(red > 255 || red < 0) {
                        et_red.requestFocus();
                        et_red.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(green > 255 || green < 0) {
                        et_green.requestFocus();
                        et_green.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else if(blue > 255 || blue < 0) {
                        et_blue.requestFocus();
                        et_blue.setText(null);
                        tv_error.setVisibility(View.VISIBLE);
                        tv_error.setText(res.getString(R.string.error_number_between));
                    } else {
                        String hex_red = Integer.toHexString(red);
                        if(hex_red.length() < 2) hex_red = "0" + hex_red;
                        String hex_green = Integer.toHexString(green);
                        if(hex_green.length() < 2) hex_green = "0" + hex_green;
                        String hex_blue = Integer.toHexString(blue);
                        if(hex_blue.length() < 2) hex_blue = "0" + hex_blue;
                        float[] hsv = new float[3];
                        Color.RGBToHSV(red, green, blue, hsv);
                        et_h.setText(String.format("%.02f", hsv[0]));
                        et_s.setText(String.format("%.02f", hsv[1]));
                        et_v.setText(String.format("%.02f", hsv[2]));
                        container.setBackgroundColor(Color.parseColor("#" + hex_red + hex_green + hex_blue));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.rgb_to_hsv))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(red);
                            sb_green.setProgress(green);
                            sb_blue.setProgress(blue);
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
        et_hex = (EditText) v.findViewById(R.id.et_hex);
        et_hex.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_hex.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_hex.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        et_h = (EditText) v.findViewById(R.id.et_h);
        et_s = (EditText) v.findViewById(R.id.et_s);
        et_v = (EditText) v.findViewById(R.id.et_v);
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv_error.setVisibility(View.GONE);
                    String hex = et_hex.getText().toString().trim();
                    if(hex.length() == 6) {
                        red = Integer.valueOf(hex.substring(0, 2), 16);
                        green = Integer.valueOf(hex.substring(2, 4), 16);
                        blue = Integer.valueOf(hex.substring(4, 6), 16);
                        float[] hsv = new float[3];
                        Color.RGBToHSV(red, green, blue, hsv);
                        et_h.setText(String.format("%.02f", hsv[0]));
                        et_s.setText(String.format("%.02f", hsv[1]));
                        et_v.setText(String.format("%.02f", hsv[2]));
                        container.setBackgroundColor(Color.parseColor("#" + hex));
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
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hex_to_hsv))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(red);
                            sb_green.setProgress(green);
                            sb_blue.setProgress(blue);
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
        et_h = (EditText) v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) {
                    et_s.requestFocus();
                };
            }
        });
        et_s = (EditText) v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) {
                    et_v.requestFocus();
                };
            }
        });
        et_v = (EditText) v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        et_red = (EditText) v.findViewById(R.id.et_red);
        et_green = (EditText) v.findViewById(R.id.et_green);
        et_blue = (EditText) v.findViewById(R.id.et_blue);
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv_error.setVisibility(View.GONE);
                    float[] hsv = new float[3];
                    hsv[0] = Float.parseFloat(et_h.getText().toString().trim());
                    hsv[1] = Float.parseFloat(et_s.getText().toString().trim());
                    hsv[2] = Float.parseFloat(et_v.getText().toString().trim());
                    int c = Color.HSVToColor(hsv);
                    red = Color.red(c);
                    et_red.setText(String.valueOf(red));
                    green = Color.green(c);
                    et_green.setText(String.valueOf(green));
                    blue = Color.blue(c);
                    et_blue.setText(String.valueOf(blue));
                    container.setBackgroundColor(Color.rgb(red, green, blue));
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hsv_to_rgb))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(red);
                            sb_green.setProgress(green);
                            sb_blue.setProgress(blue);
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
        et_h = (EditText) v.findViewById(R.id.et_h);
        et_h.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_h.getText().toString().length() >= 6) {
                    et_s.requestFocus();
                };
            }
        });
        et_s = (EditText) v.findViewById(R.id.et_s);
        et_s.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_s.getText().toString().length() >= 6) {
                    et_v.requestFocus();
                };
            }
        });
        et_v = (EditText) v.findViewById(R.id.et_v);
        et_v.addTextChangedListener(new SimpleTextWatcherUtils() {
            @Override
            public void afterTextChanged(Editable s) {
                if(et_v.getText().toString().length() >= 6) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_v.getWindowToken(), 0);
                    btn_convert.requestFocus();
                };
            }
        });
        et_red = (EditText) v.findViewById(R.id.et_red);
        et_green = (EditText) v.findViewById(R.id.et_green);
        et_blue = (EditText) v.findViewById(R.id.et_blue);
        tv_error = (TextView) v.findViewById(R.id.error);
        btn_convert = (Button) v.findViewById(R.id.convert);
        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    tv_error.setVisibility(View.GONE);
                    float[] hsv = new float[3];
                    hsv[0] = Float.parseFloat(et_h.getText().toString().trim());
                    hsv[1] = Float.parseFloat(et_s.getText().toString().trim());
                    hsv[2] = Float.parseFloat(et_v.getText().toString().trim());
                    int c = Color.HSVToColor(hsv);
                    red = Color.red(c);
                    et_red.setText(String.valueOf(red));
                    green = Color.green(c);
                    et_green.setText(String.valueOf(green));
                    blue = Color.blue(c);
                    et_blue.setText(String.valueOf(blue));
                    et_hex.setText(Integer.toHexString(red) + Integer.toHexString(green) + Integer.toHexString(blue));
                    container.setBackgroundColor(Color.rgb(red, green, blue));
                } catch (Exception e) {
                    tv_error.setVisibility(View.VISIBLE);
                    tv_error.setText(res.getString(R.string.error));
                }
            }
        });
        container = (RelativeLayout) v.findViewById(R.id.dialog_container);

        d = new AlertDialog.Builder(MainActivity.this)
                .setTitle(res.getString(R.string.hsv_to_hex))
                .setCancelable(true)
                .setView(v)
                .setPositiveButton(res.getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            sb_red.setProgress(red);
                            sb_green.setProgress(green);
                            sb_blue.setProgress(blue);
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