package com.mrgames13.jimdo.colorconverter.App;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrgames13.jimdo.colorconverter.CommonObjects.Color;
import com.mrgames13.jimdo.colorconverter.R;
import com.mrgames13.jimdo.colorconverter.RecyclerViewAdapters.ColorsAdapter;
import com.mrgames13.jimdo.colorconverter.Utils.ColorUtils;
import com.mrgames13.jimdo.colorconverter.Utils.StorageUtils;

import java.util.ArrayList;

public class ColorSelectionActivity extends AppCompatActivity {

    //Konstanten

    //Variablen als Objekte
    private Resources res;
    private Toolbar toolbar;
    private View reveal_view;
    private View reveal_background_view;
    private ColorUtils clru;
    private StorageUtils su;
    private RecyclerView colors_view;
    private RecyclerView.Adapter colors_view_adapter;
    private ArrayList<Color> colors;
    private static Color selected_color;
    public static ColorSelectionActivity own_instance;

    //Variablen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_selection);

        //Eigene Instanz initialisieren
        own_instance = this;

        //Resourcen initialisieren
        res = getResources();

        //Toolbar initialisieren
        toolbar = findViewById(R.id.toolbar);
        toolbar.setLayoutTransition(new LayoutTransition());
        setSupportActionBar(toolbar);

        //RevealView initialisieren
        reveal_view = findViewById(R.id.reveal);
        reveal_background_view = findViewById(R.id.reveal_background);

        //ColorUtils initialisieren
        clru = new ColorUtils(res);

        //StorageUtils initialisieren
        su = new StorageUtils(this);

        //Farben laden
        colors = su.loadColors();

        //Komponenten initialisieren
        colors_view = findViewById(R.id.saved_colors);
        RecyclerView.LayoutManager colors_view_manager = new LinearLayoutManager(this);
        colors_view.setLayoutManager(colors_view_manager);
        colors_view_adapter = new ColorsAdapter(colors);
        colors_view.setAdapter(colors_view_adapter);
        if(colors.size() > 0) findViewById(R.id.no_items).setVisibility(View.GONE);

        selected_color = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(selected_color != null) getMenuInflater().inflate(R.menu.menu_activity_color_selection, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_edit) {
            final EditText et_new_name = new EditText(ColorSelectionActivity.this);
            et_new_name.setHint(R.string.choose_name);
            et_new_name.setText(selected_color.getName());
            et_new_name.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

            @SuppressLint("RestrictedApi")
            AlertDialog d = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.rename)
                    .setView(et_new_name, 60, 0, 60, 0)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            String new_name = et_new_name.getText().toString().trim();
                            if(!new_name.equals("")) {
                                su.execSQL("UPDATE " + StorageUtils.TABLE_COLORS + " SET name='" + new_name + "' WHERE id='" + selected_color.getId() + "'");
                                //Adapter aktualisieren
                                colors = su.loadColors();
                                colors_view_adapter = new ColorsAdapter(colors);
                                colors_view.setAdapter(colors_view_adapter);
                            } else {
                                Toast.makeText(ColorSelectionActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .create();
            d.show();
            et_new_name.requestFocus();
            et_new_name.selectAll();
        } else if(id == R.id.action_delete) {
            AlertDialog d = new AlertDialog.Builder(this)
                    .setCancelable(true)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_m)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            su.removeRecord(StorageUtils.TABLE_COLORS, selected_color.getId());
                            //Adapter aktualisieren
                            colors = su.loadColors();
                            colors_view_adapter = new ColorsAdapter(colors);
                            colors_view.setAdapter(colors_view_adapter);
                        }
                    })
                    .create();
            d.show();
        } else if(id == R.id.action_done) {
            Intent data = new Intent();
            data.putExtra("Color", selected_color.getColor());
            setResult(RESULT_OK, data);
            finish();
        } else if(id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Toolbar Text und Farbe setzen
        getSupportActionBar().setTitle(R.string.title_activity_color_selection);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(clru.darkenColor(res.getColor(R.color.colorPrimary)));
    }

    public void selectedColor(Color color) {
        selected_color = color;
        invalidateOptionsMenu();
        getSupportActionBar().setSubtitle(R.string.selected + ": " + color.getName());
        animateAppAndStatusBar(color.getColor());
    }

    private void animateAppAndStatusBar(final int toColor) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator animator = ViewAnimationUtils.createCircularReveal(reveal_view, toolbar.getWidth() / 2, toolbar.getHeight() / 2, 0, toolbar.getWidth() / 2.0f + 50);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    reveal_view.setBackgroundColor(toColor);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    reveal_background_view.setBackgroundColor(toColor);
                }
            });

            animator.setDuration(480);
            animator.start();
            reveal_view.setVisibility(View.VISIBLE);
        } else {
            reveal_view.setBackgroundColor(toColor);
            reveal_background_view.setBackgroundColor(toColor);
        }
    }
}