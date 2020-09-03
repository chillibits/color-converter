/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.InputType
import android.text.Selection
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.*
import androidx.core.widget.doAfterTextChanged
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.copyTextToClipboard
import com.chillibits.colorconverter.shared.round
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.SimpleOnSeekBarChangeListener
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.ui.dialog.*
import com.google.android.instantapps.InstantApps
import com.mrgames13.jimdo.colorconverter.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_edit_hex.view.*
import kotlinx.android.synthetic.main.dialog_edit_hsv.view.*
import kotlinx.android.synthetic.main.toolbar.*
import net.margaritov.preference.colorpicker.ColorPickerDialog
import java.util.*

class MainActivity : AppCompatActivity() {

    // Tools packages
    private val st = StorageTools(this)
    private val ct = ColorTools(this)
    private val cnt = ColorNameTools(this)

    // Variables as objects
    private lateinit var tts: TextToSpeech
    private var selectedColor = Color(0, Constants.NAME_SELECTED_COLOR, android.graphics.Color.BLACK, -1)
    private var disableAlpha: MenuItem? = null
    private var isAlphaDisabled = false

    // Variables
    private var initialized = false
    private var showTransparencyWarning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Apply window insets
        applyWindowInsets()

        // Initialize toolbar
        setSupportActionBar(toolbar)

        // Execute initializations
        initializeSeekBarSection()
        initializeColorContainerSection()
        initializeButtonSection()
        setDefaultComponentValues()
        isAlphaDisabled = st.getBoolean(Constants.DISABLE_ALPHA)
        enableAlpha(!isAlphaDisabled)

        // Initialize tts
        if (!InstantApps.isInstantApp(this@MainActivity)) {
            tts = TextToSpeech(this) { status ->
                if(status == TextToSpeech.SUCCESS) {
                    val result = tts.setLanguage(Locale.getDefault())
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(this, R.string.language_not_available, Toast.LENGTH_SHORT).show()
                    } else initialized = true
                } else Toast.makeText(this, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to ImageActivity, if needed
        if (intent.hasExtra(Constants.EXTRA_ACTION) && intent.getStringExtra(Constants.EXTRA_ACTION) == "image") pickColorFromImage()

        // Set to choose color mode, if required
        if(intent.hasExtra(Constants.EXTRA_CHOOSE_COLOR)) {
            finishWithColor.setOnClickListener { finishWithSelectedColor() }
            updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR,
                intent.getIntExtra(Constants.EXTRA_CHOOSE_COLOR, android.graphics.Color.BLACK), -1))
        } else {
            finishWithColor.visibility = View.GONE
        }

        // Check if app was installed
        if (Intent.ACTION_SEND == intent.action && intent.type != null && intent.type!!.startsWith("image/"))
            pickColorFromImage(intent.getParcelableExtra(Intent.EXTRA_STREAM))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        if (InstantApps.isInstantApp(this)) menu?.findItem(R.id.action_install)?.isVisible = true
        if (intent.hasExtra(Constants.EXTRA_CHOOSE_COLOR)) {
            menu?.findItem(R.id.action_done)?.isVisible = true
            menu?.findItem(R.id.action_disable_alpha)?.isVisible = false
        }
        menu?.findItem(R.id.action_transparency)?.isVisible = showTransparencyWarning
        disableAlpha = menu?.findItem(R.id.action_disable_alpha)
        disableAlpha?.isChecked = isAlphaDisabled
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_transparency -> showTransparencyWarning()
            R.id.action_rate -> showRatingDialog()
            R.id.action_share -> showRecommendationDialog()
            R.id.action_install -> showInstantAppInstallDialog(R.string.install_app_download)
            R.id.action_disable_alpha -> {
                isAlphaDisabled = !item.isChecked
                st.putBoolean(Constants.DISABLE_ALPHA, isAlphaDisabled)
                item.isChecked = isAlphaDisabled
                enableAlpha(!isAlphaDisabled)
            }
            R.id.action_done -> finishWithSelectedColor()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(Constants.EXTRA_SELECTED_COLOR, selectedColor.color)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, savedInstanceState.getInt(Constants.EXTRA_SELECTED_COLOR), -1))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            Constants.REQ_PICK_COLOR_FROM_IMAGE -> {
                if(resultCode == Activity.RESULT_OK)
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, data!!.getIntExtra(
                        Constants.EXTRA_COLOR, 0), -1))
            }
            Constants.REQ_LOAD_COLOR -> {
                if(resultCode == Activity.RESULT_OK)
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, data!!.getIntExtra(
                        Constants.EXTRA_COLOR, 0), -1))
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == Constants.REQ_PERMISSIONS) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickColorFromImage()
            } else {
                Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyWindowInsets() {
        window.run {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> setDecorFitsSystemWindows(false)
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    decorView.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    decorView.setOnApplyWindowInsetsListener { _, insets ->
                        toolbar?.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                        scrollContainer.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                        finishWithColorWrapper.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                        insets
                    }
                }
                else -> {
                    addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
                }
            }
        }
    }

    private fun pickColorFromImage(defaultImageUri: Uri? = null) {
        if (InstantApps.isInstantApp(this@MainActivity)) {
            showInstantAppInstallDialog(R.string.instant_install_m)
        } else {
            Intent(this, ImageActivity::class.java).run {
                if(defaultImageUri != null) putExtra("ImageUri", defaultImageUri)
                startActivityForResult(this, Constants.REQ_PICK_COLOR_FROM_IMAGE)
            }
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
        editTextName.doAfterTextChanged {s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().isNotEmpty()
        }
        editTextName.selectAll()
        editTextName.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun editHexCode() {
        // Initialize views
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hex, container, false)
        val hexValue = dialogView.dialogHex
        if(isAlphaDisabled)
            hexValue.setText(String.format(getString(R.string.hex_format, "%06X".format((0xFFFFFF and selectedColor.color)).toUpperCase(Locale.getDefault()))))
        else
            hexValue.setText(String.format(getString(R.string.hex_format), "%08X".format(selectedColor.color).toUpperCase(Locale.getDefault())))
        Selection.setSelection(hexValue.text, hexValue.text.length)

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hex_code)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.choose_color) { _, _ ->
                var hex = hexValue.text.toString()
                if(isAlphaDisabled && hex.length == 4) hex = hex.replace(Regex("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])"), "#$1$1$2$2$3$3")
                if(!isAlphaDisabled && hex.length == 5) hex = hex.replace(Regex("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])"), "#$1$1$2$2$3$3$4$4")
                val tmp = selectedColor
                tmp.color = android.graphics.Color.parseColor(hex)
                tmp.alpha = tmp.color.alpha
                tmp.red = tmp.color.red
                tmp.green = tmp.color.green
                tmp.blue = tmp.color.blue
                updateDisplays(tmp)
            }
            .show()

        // Prepare views
        hexValue.doAfterTextChanged { s ->
            val value = s.toString()
            if(!value.startsWith("#")) {
                hexValue.setText("#")
                Selection.setSelection(hexValue.text, hexValue.text.length)
            } else {
                if(value.length > 1 && !value.matches("#[a-fA-F0-9]+".toRegex())) s?.delete(value.length -1, value.length)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = if(isAlphaDisabled) {
                    s.toString().length == 7 || s.toString().length == 4
                } else {
                    s.toString().length == 9 || s.toString().length == 5
                }
            }
        }
        hexValue.setSelection(1, if(isAlphaDisabled) 7 else 9)
        hexValue.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun editHSVCode() {
        // Initialize views
        val container = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hsv, container, false)
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(selectedColor.color, hsv)
        container.dialogH.setText(hsv[0].toString())
        container.dialogS.setText(hsv[1].toString())
        container.dialogV.setText(hsv[2].toString())

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hsv_code)
            .setView(container)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.choose_color) { _, _ ->
                val hsvSelected = floatArrayOf(
                    container.dialogH.text.toString().toFloat(),
                    container.dialogS.text.toString().toFloat(),
                    container.dialogV.text.toString().toFloat()
                )
                val tmp = selectedColor
                tmp.color = android.graphics.Color.HSVToColor(hsvSelected)
                tmp.alpha = tmp.color.alpha
                tmp.red = tmp.color.red
                tmp.green = tmp.color.green
                tmp.blue = tmp.color.blue
                updateDisplays(tmp)
            }
            .show()

        container.dialogH.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = s.toString().isNotEmpty() && container.dialogS.text.isNotEmpty() && container.dialogV.text.isNotEmpty()
        }
        container.dialogS.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = container.dialogH.text.isNotEmpty() && s.toString().isNotEmpty() && container.dialogV.text.isNotEmpty()
        }
        container.dialogV.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = container.dialogH.text.isNotEmpty() && container.dialogS.text.isNotEmpty() && s.toString().isNotEmpty()
        }

        container.dialogH.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun randomizeColor() {
        val random = Random(System.currentTimeMillis())
        updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, 255, random.nextInt(256), random.nextInt(256), random.nextInt(256), -1))
    }

    private fun chooseColor() {
        val colorPicker = ColorPickerDialog(this, selectedColor.color)
        if(!isAlphaDisabled) colorPicker.alphaSliderVisible = true
        colorPicker.hexValueEnabled = true
        colorPicker.setTitle(R.string.choose_color)
        colorPicker.setOnColorChangedListener { color ->
            updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, color, 0))
        }
        colorPicker.show()
    }

    private fun updateDisplays(color: Color) {
        // Update all views that are not animated
        displayAlpha.text = color.alpha.toString()
        displayRed.text = color.red.toString()
        displayGreen.text = color.green.toString()
        displayBlue.text = color.blue.toString()
        displayName.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(color))

        // Update ARGB TextView
        displayArgb.text = if(isAlphaDisabled) {
            String.format(getString(R.string.rgb_), color.red, color.green, color.blue)
        } else {
            String.format(getString(R.string.argb_), color.alpha, color.red, color.green, color.blue)
        }
        // Update HEX TextView
        displayHex.text = if(isAlphaDisabled) {
            String.format(getString(R.string.hex_), "%06X".format((0xFFFFFF and color.color)).toUpperCase(Locale.getDefault()))
        } else {
            String.format(getString(R.string.hex_), "%08X".format(color.color).toUpperCase(Locale.getDefault()))
        }
        // Update HSV TextView
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
        displayHsv.text = String.format(getString(R.string.hsv_), String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[1]), String.format(Constants.HSV_FORMAT_STRING, hsv[2]))
        // Update CMYK TextView
        val cmyk = ct.getCmykFromRgb(color.red, color.green, color.blue)
        displayCmyk.text = String.format(getString(R.string.cmyk_), cmyk[0], cmyk[1], cmyk[2], cmyk[3])

        // Update text colors
        val textColor = ct.getTextColor(this, android.graphics.Color.argb(color.alpha, color.red, color.green, color.blue))
        displayName.setTextColor(textColor)
        displayArgb.setTextColor(textColor)
        displayHex.setTextColor(textColor)
        displayHsv.setTextColor(textColor)
        displayCmyk.setTextColor(textColor)
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        copyName.colorFilter = colorFilter
        copyArgb.colorFilter = colorFilter
        copyHex.colorFilter = colorFilter
        copyHsv.colorFilter = colorFilter
        copyCmyk.colorFilter = colorFilter
        saveColor.colorFilter = colorFilter
        loadColor.colorFilter = colorFilter

        // Update animated views
        ValueAnimator.ofInt(colorAlpha.progress, color.alpha).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator ->
                colorAlpha.progress = valueAnimator.animatedValue as Int
                colorContainer.setBackgroundColor(color.color)
            }
            doOnEnd { selectedColor = color }
        }.start()

        ValueAnimator.ofInt(colorRed.progress, color.red).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> colorRed.progress = valueAnimator.animatedValue as Int }
        }.start()

        ValueAnimator.ofInt(colorGreen.progress, color.green).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> colorGreen.progress = valueAnimator.animatedValue as Int }
        }.start()

        ValueAnimator.ofInt(colorBlue.progress, color.blue).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> colorBlue.progress = valueAnimator.animatedValue as Int }
        }.start()
    }

    @Suppress("DEPRECATION")
    private fun speakColor() {
        if(isAudioMuted()) {
            Toast.makeText(this, R.string.audio_muted, Toast.LENGTH_SHORT).show()
        } else {
            if(initialized) {
                val colorName = cnt.getColorNameFromColor(selectedColor)
                tts.speak(colorName, TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                Toast.makeText(this, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isAudioMuted(): Boolean {
        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }

    private fun finishWithSelectedColor() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constants.EXTRA_CHOOSE_COLOR, selectedColor.color)
        })
        finish()
    }

    private fun setDefaultComponentValues() {
        // Initialize views
        displayName.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(selectedColor))
        displayArgb.text = String.format(
            getString(R.string.argb_),
            selectedColor.alpha,
            selectedColor.red,
            selectedColor.green,
            selectedColor.blue
        )
        displayHex.text = String.format(getString(R.string.hex_), "%08X".format(selectedColor.color).toUpperCase(
            Locale.getDefault()))
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(
            selectedColor.red,
            selectedColor.green,
            selectedColor.blue,
            hsv
        )
        displayHsv.text = String.format(
            getString(R.string.hsv_),
            String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[2])
        )
        val cmyk = ct.getCmykFromRgb(selectedColor.red, selectedColor.green, selectedColor.blue)
        displayCmyk.text =
            String.format(getString(R.string.cmyk_), cmyk[0], cmyk[1], cmyk[2], cmyk[3])
    }

    private fun initializeButtonSection() {
        // Edit codes
        editHex.setOnClickListener { editHexCode() }
        editHsv.setOnClickListener { editHSVCode() }

        // Speak color
        speakColor.setOnClickListener {
            if (InstantApps.isInstantApp(this)) {
                // It's not allowed to use tts in an instant app
                showInstantAppInstallDialog(R.string.instant_install_m)
            } else speakColor()
        }

        pick.setOnClickListener { chooseColor() }
        pick_random_color.setOnClickListener { randomizeColor() }
        pickFromImage.setOnClickListener { pickColorFromImage() }
    }

    private fun initializeColorContainerSection() {
        colorContainer.setOnClickListener { chooseColor() }

        // Load color
        loadColor.setOnClickListener {
            startActivityForResult(
                Intent(this, ColorSelectionActivity::class.java),
                Constants.REQ_LOAD_COLOR
            )
        }

        // Save color
        saveColor.setOnClickListener { saveColor() }

        // Copy color codes
        copyName.setOnClickListener { copyNameToClipboard() }
        copyArgb.setOnClickListener { copyArgbToClipboard() }
        copyHex.setOnClickListener { copyHexToClipboard() }
        copyHsv.setOnClickListener { copyHsvToClipboard() }
        copyCmyk.setOnClickListener { copyCmykToClipboard() }
    }

    private fun copyNameToClipboard() = copyTextToClipboard(getString(R.string.color_name), displayName.text.toString())

    private fun copyArgbToClipboard() {
        if (isAlphaDisabled) {
            copyTextToClipboard(
                getString(R.string.rgb_code), String.format(
                    getString(R.string.rgb_clipboard),
                    selectedColor.red, selectedColor.green, selectedColor.blue
                )
            )
        } else {
            // Show multiple choice dialog
            if (!st.getBoolean(Constants.ARGB_REMEMBER, false)) {
                showArgbExportDialog(
                    selectedColor.alpha,
                    selectedColor.red,
                    selectedColor.green,
                    selectedColor.blue
                )
            } else {
                if (st.getBoolean(Constants.ARGB_REMEMBER_SELECTION, false)) {
                    copyTextToClipboard(
                        getString(R.string.argb_code), String.format(
                            getString(R.string.argb_clipboard),
                            selectedColor.alpha,
                            selectedColor.red,
                            selectedColor.green,
                            selectedColor.blue
                        )
                    )
                } else {
                    copyTextToClipboard(
                        getString(R.string.argb_code), String.format(
                            getString(R.string.rgba_clipboard_css),
                            selectedColor.red,
                            selectedColor.green,
                            selectedColor.blue,
                            (selectedColor.alpha / 255.0).round(3)
                        )
                    )
                }
            }
        }
    }

    private fun copyHexToClipboard() {
        copyTextToClipboard(
            getString(R.string.hex_code),
            if (isAlphaDisabled)
                "#%06X".format(0xFFFFFF and selectedColor.color).toUpperCase(Locale.getDefault())
            else
                "#%08X".format(selectedColor.color).toUpperCase(Locale.getDefault())
        )
    }

    private fun copyHsvToClipboard() = copyTextToClipboard(getString(R.string.hsv_clipboard), displayHsv.text.toString())

    private fun copyCmykToClipboard() {
        // Show multiple choice dialog
        val cmyk = ct.getCmykFromRgb(selectedColor.red, selectedColor.green, selectedColor.blue)
        if (!st.getBoolean(Constants.CMYK_REMEMBER, false)) {
            showCmykExportDialog(cmyk[0], cmyk[1], cmyk[2], cmyk[3])
        } else {
            if (st.getBoolean(Constants.CMYK_REMEMBER_SELECTION, false)) {
                copyTextToClipboard(
                    getString(R.string.cmyk_code), String.format(
                        getString(R.string.cmyk_clipboard),
                        cmyk[0] / 100.0, cmyk[1] / 100.0, cmyk[2] / 100.0, cmyk[3] / 100.0
                    )
                )
            } else {
                copyTextToClipboard(
                    getString(R.string.cmyk_code), String.format(
                        getString(R.string.cmyk_clipboard_css),
                        cmyk[0], cmyk[1], cmyk[2], cmyk[3]
                    )
                )
            }
        }
    }

    private fun initializeSeekBarSection() {
        // Initialize other layout components
        colorAlpha.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.gray), BlendModeCompat.SRC_ATOP
        )
        colorRed.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP
            )
        colorRed.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP
        )
        colorGreen.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP
            )
        colorGreen.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP
        )
        colorBlue.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.blue), BlendModeCompat.SRC_ATOP
            )
        colorBlue.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(
                this,
                R.color.blue
            ), BlendModeCompat.SRC_ATOP
        )

        colorAlpha.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    displayAlpha.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, progress, colorRed.progress,
                        colorGreen.progress, colorBlue.progress, -1))
                }
                if ((showTransparencyWarning && progress > 20) || (!showTransparencyWarning && progress <= 20)) {
                    showTransparencyWarning = !showTransparencyWarning
                    invalidateOptionsMenu()
                }
            }
        })
        colorRed.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    displayRed.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, colorAlpha.progress,
                        progress, colorGreen.progress, colorBlue.progress, -1))
                }
            }
        })
        colorGreen.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    displayGreen.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, colorAlpha.progress,
                        colorRed.progress, progress, colorBlue.progress, -1))
                }
            }
        })
        colorBlue.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    displayBlue.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, colorAlpha.progress,
                        colorRed.progress, colorGreen.progress, progress, -1))
                }
            }
        })
    }

    private fun enableAlpha(enabled: Boolean) {
        // Set alpha to 100%
        updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, 255, colorRed.progress,
            colorGreen.progress, colorBlue.progress, -1))
        // Show / hide components
        val visibility = if(enabled) View.VISIBLE else View.GONE
        colorAlpha.visibility = visibility
        displayAlpha.visibility = visibility
        displayAlphaLabel.visibility = visibility
    }
}