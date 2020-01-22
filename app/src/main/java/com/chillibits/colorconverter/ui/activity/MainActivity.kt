/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.Selection
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.*
import com.chillibits.colorconverter.R
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.SimpleTextWatcher
import com.chillibits.colorconverter.tools.StorageTools
import com.google.android.instantapps.InstantApps
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_edit_hex.view.*
import kotlinx.android.synthetic.main.dialog_edit_hsv.view.*
import net.margaritov.preference.colorpicker.ColorPickerDialog
import java.util.*

// Constants
private const val REQ_PICK_COLOR_FROM_IMAGE: Int = 10001
private const val REQ_LOAD_COLOR = 10002
private const val REQ_INSTANT_INSTALL = 10003
private const val REQ_PERMISSIONS = 10004
private const val COLOR_ANIMATION_DURATION = 500L

class MainActivity : AppCompatActivity() {

    // Tools packages
    private val st = StorageTools(this)
    private val ct = ColorTools(this)
    private val cnt = ColorNameTools(this)

    // Variables as objects
    private var selectedColor = Color(0, "Selection", android.graphics.Color.BLACK, -1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                toolbar?.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                insets
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        }

        setSupportActionBar(toolbar)

        color_red.progressDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP)
        color_red.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP)
        color_green.progressDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP)
        color_green.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP)
        color_blue.progressDrawable.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.blue), BlendModeCompat.SRC_ATOP)
        color_blue.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(this, R.color.blue), BlendModeCompat.SRC_ATOP)

        color_red.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    display_red.text = value
                    updateDisplays(Color(0, "Selection", progress, color_green.progress, color_blue.progress, -1))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        color_green.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    display_green.text = value
                    updateDisplays(Color(0, "Selection", color_red.progress, progress, color_blue.progress, -1))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        color_blue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    display_blue.text = value
                    updateDisplays(Color(0, "Selection", color_red.progress, color_green.progress, progress, -1))
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        color_container.setOnClickListener { chooseColor() }

        // Load color
        load_color.setOnClickListener {
            startActivityForResult(Intent(this, ColorSelectionActivity::class.java), REQ_LOAD_COLOR)
        }

        // Save color
        save_color.setOnClickListener { saveColor() }

        // Copy color codes
        copy_name.setOnClickListener {
            copyTextToClipboard(getString(R.string.color_name), display_name.text.toString())
        }
        copy_rgb.setOnClickListener {
            copyTextToClipboard(getString(R.string.rgb_code), display_rgb.text.toString())
        }
        copy_hex.setOnClickListener {
            copyTextToClipboard(getString(R.string.hex_code), display_hex.text.toString())
        }
        copy_hsv.setOnClickListener {
            copyTextToClipboard(getString(R.string.hsv_code), display_hsv.text.toString())
        }

        // Edit hex code
        edit_hex.setOnClickListener {
            editHexCode()
        }

        // Edit hsv code
        edit_hsv.setOnClickListener {
            editHSVCode()
        }

        pick.setOnClickListener { chooseColor() }
        pick_random_color.setOnClickListener { randomizeColor() }
        pick_from_image.setOnClickListener { pickColorFromImage() }

        // Initialize views
        display_name.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(selectedColor))
        display_rgb.text = String.format(getString(R.string.rgb_), selectedColor.red, selectedColor.green, selectedColor.blue)
        display_hex.text = String.format(getString(R.string.hex_), String.format("#%06X", 0xFFFFFF and selectedColor.color))
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(selectedColor.red, selectedColor.green, selectedColor.blue, hsv)
        display_hsv.text = String.format(getString(R.string.hsv_), String.format("%.02f", hsv[0]), String.format("%.02f", hsv[1]), String.format("%.02f", hsv[2]))

        // Redirect to ImageActivity, if needed
        if (intent.hasExtra("action") && intent.getStringExtra("action") == "image") pickColorFromImage()

        // Check if app was installed
        val intent = intent
        if (intent.getBooleanExtra("InstantInstalled", false)) {
            val d: AlertDialog =
                AlertDialog.Builder(this)
                    .setTitle(R.string.instant_installed_t)
                    .setMessage(R.string.instant_installed_m)
                    .setCancelable(true)
                    .setPositiveButton(R.string.ok, null)
                    .create()
            d.show()
        } else if (Intent.ACTION_SEND == intent.action && intent.type != null && intent.type!!.startsWith("image/")) {
            pickColorFromImage(intent.getParcelableExtra(Intent.EXTRA_STREAM))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        if (InstantApps.isInstantApp(this)) menu?.getItem(0)?.isVisible = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_rate -> rateApp()
            R.id.action_share -> recommendApp()
            R.id.action_install -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.install_app)
                    .setMessage(R.string.install_app_download)
                    .setPositiveButton(R.string.install_app) { _, _ ->
                        val i = Intent(this@MainActivity, MainActivity::class.java)
                        i.putExtra("InstantInstalled", true)
                        InstantApps.showInstallPrompt(this@MainActivity, i, REQ_INSTANT_INSTALL, "")
                    }
                    .setNegativeButton(R.string.close, null)
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("SelectedColor", selectedColor.color)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateDisplays(Color(0, "Selection", savedInstanceState.getInt("SelectedColor"), -1))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQ_PICK_COLOR_FROM_IMAGE -> {
                if(resultCode == Activity.RESULT_OK) updateDisplays(Color(0, "Selection", data!!.getIntExtra("Color", 0), -1))
            }
            REQ_LOAD_COLOR -> {
                if(resultCode == Activity.RESULT_OK) updateDisplays(Color(0, "Selection", data!!.getIntExtra("Color", 0), -1))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQ_PERMISSIONS) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickColorFromImage()
            } else {
                Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickColorFromImage(defaultImageUri: Uri? = null) {
        if (InstantApps.isInstantApp(this@MainActivity)) {
            AlertDialog.Builder(this@MainActivity)
                .setTitle(R.string.install_app)
                .setMessage(R.string.instant_install_m)
                .setPositiveButton(R.string.install_app) { _, _ ->
                    val i = Intent(this@MainActivity, MainActivity::class.java)
                    i.putExtra("InstantInstalled", true)
                    InstantApps.showInstallPrompt(this@MainActivity, i, REQ_INSTANT_INSTALL, "")
                }
                .setNegativeButton(R.string.close, null)
                .show()
        } else {
            val i = Intent(this, ImageActivity::class.java)
            if(defaultImageUri != null) i.putExtra("ImageUri", defaultImageUri)
            startActivityForResult(i, REQ_PICK_COLOR_FROM_IMAGE)
            getString(R.string.message_to_drag_up)
        }
    }

    private fun saveColor() {
        // Initialize views
        val editTextName = EditText(this)
        editTextName.hint = getString(R.string.choose_name)
        editTextName.setText(cnt.getColorNameFromColor(selectedColor))
        editTextName.inputType = InputType.TYPE_TEXT_VARIATION_URI
        val container = FrameLayout(this)
        val containerParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        containerParams.marginStart = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        containerParams.marginEnd = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        editTextName.layoutParams = containerParams
        container.addView(editTextName)

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.save_color)
            .setView(container)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.save) { _, _ ->
                selectedColor.name = editTextName.text.toString().trim()
                st.addColor(selectedColor)
            }
            .show()

        // Prepare views
        editTextName.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().isNotEmpty()
            }
        })
        editTextName.selectAll()
        editTextName.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun editHexCode() {
        // Initialize views
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hex, container, false)
        val hexValue = dialogView.dialog_hex
        hexValue.setText(String.format("#%06X", 0xFFFFFF and selectedColor.color))
        Selection.setSelection(hexValue.text, hexValue.text.length)

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hex_code)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.choose_color) { _, _ ->
                var hex = hexValue.text.toString()
                if(hex.length == 4) hex = hex.replace(Regex("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])"), "#$1$1$2$2$3$3")
                val tmp = selectedColor
                tmp.color = android.graphics.Color.parseColor(hex)
                tmp.red = tmp.color.red
                tmp.green = tmp.color.green
                tmp.blue = tmp.color.blue
                updateDisplays(tmp)
            }
            .show()

        // Prepare views
        hexValue.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                val value = s.toString()
                if(!value.startsWith("#")) {
                    hexValue.setText("#")
                    Selection.setSelection(hexValue.text, hexValue.text.length)
                } else {
                    if(value.length > 1 && !value.matches("#[a-fA-F0-9]+".toRegex())) s?.delete(value.length -1, value.length)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().length == 7 || s.toString().length == 4
                }
            }
        })
        hexValue.setSelection(1, 7)
        hexValue.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun editHSVCode() {
        // Initialize views
        val container = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hsv, container, false)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(selectedColor.color, hsv)
        container.dialog_h.setText(hsv[0].toString())
        container.dialog_s.setText(hsv[1].toString())
        container.dialog_v.setText(hsv[2].toString())

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hsv_code)
            .setView(container)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.choose_color) { _, _ ->
                val hsvSelected = floatArrayOf(
                    container.dialog_h.text.toString().toFloat(),
                    container.dialog_s.text.toString().toFloat(),
                    container.dialog_v.text.toString().toFloat()
                )
                val tmp = selectedColor
                tmp.color = android.graphics.Color.HSVToColor(hsvSelected)
                tmp.red = tmp.color.red
                tmp.green = tmp.color.green
                tmp.blue = tmp.color.blue
                updateDisplays(tmp)
            }
            .show()

        container.dialog_h.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().isNotEmpty() && container.dialog_s.text.isNotEmpty() && container.dialog_v.text.isNotEmpty()
            }
        })
        container.dialog_s.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = container.dialog_h.text.isNotEmpty() && s.toString().isNotEmpty() && container.dialog_v.text.isNotEmpty()
            }
        })
        container.dialog_v.addTextChangedListener(object : SimpleTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = container.dialog_h.text.isNotEmpty() && container.dialog_s.text.isNotEmpty() && s.toString().isNotEmpty()
            }
        })

        container.dialog_h.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun randomizeColor() {
        val random = Random(System.currentTimeMillis())
        updateDisplays(Color(0, "Selection", random.nextInt(256), random.nextInt(256), random.nextInt(256), -1))
    }

    private fun chooseColor() {
        val colorPicker = ColorPickerDialog(this, android.graphics.Color.parseColor(display_hex.text.toString().substring(5)))
        colorPicker.alphaSliderVisible = false
        colorPicker.hexValueEnabled = true
        colorPicker.setTitle(R.string.choose_color)
        colorPicker.setOnColorChangedListener { color ->
            updateDisplays(Color(0, "Selection", android.graphics.Color.red(color), android.graphics.Color.green(color), android.graphics.Color.blue(color), 0))
        }
        colorPicker.show()
    }

    private fun copyTextToClipboard(key: String, value: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(key, value))
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    private fun updateDisplays(color: Color) {
        // Update all views that are not animated
        display_red.text = color.red.toString()
        display_green.text = color.green.toString()
        display_blue.text = color.blue.toString()
        display_name.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(color))
        // Update RGB TextView
        display_rgb.text = String.format(getString(R.string.rgb_), color.red, color.green, color.blue)
        // Update HEX TextView
        display_hex.text = String.format(getString(R.string.hex_), String.format("#%06X", 0xFFFFFF and color.color))
        // Update HSV TextView
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
        display_hsv.text = String.format(getString(R.string.hsv_), String.format("%.02f", hsv[0]), String.format("%.02f", hsv[1]), String.format("%.02f", hsv[2]))

        // Update text colors
        val textColor = ct.getTextColor(android.graphics.Color.rgb(color.red, color.green, color.blue))
        display_name.setTextColor(textColor)
        display_rgb.setTextColor(textColor)
        display_hex.setTextColor(textColor)
        display_hsv.setTextColor(textColor)
        copy_name.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        copy_rgb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        copy_hex.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        copy_hsv.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        save_color.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        load_color.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)

        // Update animated views
        val redAnim = ValueAnimator.ofInt(color_red.progress, color.red)
        redAnim.duration = COLOR_ANIMATION_DURATION
        redAnim.addUpdateListener { valueAnimator ->
            color_red.progress = valueAnimator.animatedValue as Int
            color_container.setBackgroundColor(color.color)
        }
        redAnim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                selectedColor = color
            }
            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        redAnim.start()

        val greenAnim = ValueAnimator.ofInt(color_green.progress, color.green)
        greenAnim.duration = COLOR_ANIMATION_DURATION
        greenAnim.addUpdateListener { valueAnimator -> color_green.progress = valueAnimator.animatedValue as Int }
        greenAnim.start()

        val blueAnim = ValueAnimator.ofInt(color_blue.progress, color.blue)
        blueAnim.duration = COLOR_ANIMATION_DURATION
        blueAnim.addUpdateListener { valueAnimator -> color_blue.progress = valueAnimator.animatedValue as Int }
        blueAnim.start()
    }

    private fun rateApp() {
        AlertDialog.Builder(this)
            .setTitle(R.string.rate)
            .setMessage(R.string.rate_m)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton(R.string.rate) { _, _ ->
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
                } catch (e: ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun recommendApp() {
        AlertDialog.Builder(this)
            .setTitle(R.string.share)
            .setMessage(R.string.share_m)
            .setIcon(R.mipmap.ic_launcher)
            .setPositiveButton(R.string.share) { _, _ ->
                val i = Intent()
                i.action = Intent.ACTION_SEND
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.recommend_string))
                i.type = "text/plain"
                startActivity(i)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}