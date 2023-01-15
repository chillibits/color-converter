/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Selection
import android.view.*
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.*
import androidx.core.widget.doAfterTextChanged
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.SimpleOnSeekBarChangeListener
import com.chillibits.colorconverter.tools.ClipboardTools
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.ui.adapter.ColorsAdapter
import com.chillibits.colorconverter.ui.dialog.*
import com.chillibits.colorconverter.ui.templates.showSettings
import com.chillibits.colorconverter.viewmodel.MainViewModel
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import com.chillibits.simplesettings.tool.getPrefIntValue
import com.chillibits.simplesettings.tool.getPrefObserver
import com.google.android.instantapps.InstantApps
import com.google.android.material.textfield.TextInputEditText
import com.mrgames13.jimdo.colorconverter.R
import com.mrgames13.jimdo.colorconverter.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import five.star.me.FiveStarMe
import net.margaritov.preference.colorpicker.ColorPickerDialog
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), ColorsAdapter.ColorSelectionListener, SimpleSettingsConfig.OptionsItemSelectedCallback {

    // Tools packages
    @Inject lateinit var st: StorageTools
    @Inject lateinit var ct: ColorTools
    @Inject lateinit var cnt: ColorNameTools
    @Inject lateinit var cbt: ClipboardTools

    // Variables as objects
    private lateinit var binding: ActivityMainBinding
    private val vm by viewModels<MainViewModel>()

    // Variables
    private var isAlphaEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets
        applyWindowInsets()

        // Initialize toolbar
        setSupportActionBar(binding.toolbar)

        // Execute initializations
        initializeSeekBarSection()
        initializeColorContainerSection()
        initializeButtonSection()
        setDefaultComponentValues()

        // Check if the in-app rating dialog can be displayed
        FiveStarMe.with(this)
            .setInstallDays(7)
            .setLaunchTimes(10)
            .monitor()

        // Redirect to ImageActivity, if needed
        if (intent.hasExtra(Constants.EXTRA_ACTION) &&
            intent.getStringExtra(Constants.EXTRA_ACTION) == "image") {
            pickColorFromImage()
        } else {
            // Show in-app rating dialog
            FiveStarMe.showRateDialogIfMeetsConditions(this)
        }

        // Set to choose color mode, if required
        if (intent.hasExtra(Constants.EXTRA_CHOOSE_COLOR)) {
            binding.finishWithColor.setOnClickListener { finishWithSelectedColor() }
            val color = intent.getIntExtra(Constants.EXTRA_CHOOSE_COLOR, android.graphics.Color.BLACK)
            updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, color, -1))
        } else {
            binding.finishWithColor.visibility = View.GONE
            val color = getPrefIntValue(Constants.NAME_SELECTED_COLOR, android.graphics.Color.BLACK)
            vm.selectedColor = Color(0, Constants.NAME_SELECTED_COLOR, color, -1)
            updateDisplays(vm.selectedColor)
        }

        subscribeToPreferenceValues()

        // Check if app was installed
        if (Intent.ACTION_SEND == intent.action && intent.type != null && intent.type!!.startsWith("image/"))
            pickColorFromImage(intent.getParcelableExtra(Intent.EXTRA_STREAM))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        if (InstantApps.isInstantApp(this)) menu?.findItem(R.id.action_install)?.isVisible = true
        if (intent.hasExtra(Constants.EXTRA_CHOOSE_COLOR)) {
            menu?.findItem(R.id.action_done)?.isVisible = true
            menu?.findItem(R.id.action_settings)?.isVisible = false
        }
        menu?.findItem(R.id.action_transparency)?.isVisible = vm.showTransparencyWarning
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_transparency -> showTransparencyWarning()
            R.id.action_palette -> showColorPaletteDialog(this, cnt, ct)
            R.id.action_settings -> showSettings()
            R.id.action_rate -> showRatingDialog()
            R.id.action_share -> showRecommendationDialog()
            R.id.action_install -> showInstantAppInstallDialog(R.string.install_app_download)
            R.id.action_done -> finishWithSelectedColor()
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == Constants.REQ_PERMISSIONS) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                pickColorFromImage()
            } else {
                Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applyWindowInsets() = window.run {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
                    if(systemInsets.top > 0) {
                        binding.toolbar.setPadding(0, systemInsets.top, 0, 0)
                        binding.scrollContainer.setPadding(0, 0, 0, systemInsets.bottom)
                        binding.finishWithColorWrapper.setPadding(0, 0, 0, systemInsets.bottom)
                    }
                    insets
                }
                setDecorFitsSystemWindows(false)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    binding.toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                    binding.scrollContainer.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                    binding.finishWithColorWrapper.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                    insets
                }
            }
            else -> {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
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

    private fun editHexCode() {
        // Initialize views
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hex, binding.container, false)
        val hexValue = dialogView.findViewById<TextInputEditText>(R.id.dialogHex)
        if(!isAlphaEnabled)
            hexValue.setText(String.format(getString(R.string.hex_format,
                "%06X".format((0xFFFFFF and vm.selectedColor.color)).uppercase(Locale.getDefault())
            )))
        else
            hexValue.setText(String.format(getString(R.string.hex_format),
                "%08X".format(vm.selectedColor.color).uppercase(Locale.getDefault())
            ))
        Selection.setSelection(hexValue.text, hexValue.text.toString().length)

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hex_code)
            .setView(dialogView)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton( R.string.choose_color) { _, _ ->
                var hex = hexValue.text.toString()
                if(!isAlphaEnabled && hex.length == 4)
                    hex = hex.replace(Regex("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])"), "#$1$1$2$2$3$3")
                if(isAlphaEnabled && hex.length == 5)
                    hex = hex.replace(Regex("#([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])"), "#$1$1$2$2$3$3$4$4")
                val tmp = vm.selectedColor
                tmp.apply {
                    color = android.graphics.Color.parseColor(hex)
                    alpha = color.alpha
                    red = color.red
                    green = color.green
                    blue = color.blue
                }
                updateDisplays(tmp)
            }
            .show()

        // Prepare views
        hexValue.doAfterTextChanged { s ->
            val value = s.toString()
            if(!value.startsWith("#")) {
                hexValue.setText("#")
                Selection.setSelection(hexValue.text, hexValue.text.toString().length)
            } else {
                if(value.length > 1 && !value.matches("#[a-fA-F0-9]+".toRegex())) s?.delete(value.length -1, value.length)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = if(isAlphaEnabled) {
                    s.toString().length == 9 || s.toString().length == 5 || s.toString().length == 7
                } else {
                    s.toString().length == 7 || s.toString().length == 4
                }
            }
        }
        hexValue.setSelection(1, if(isAlphaEnabled) 9 else 7)
        hexValue.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun editHSVCode() {
        // Initialize views
        val container = LayoutInflater.from(this).inflate(R.layout.dialog_edit_hsv, binding.container, false)
        val dialogH = container.findViewById<TextInputEditText>(R.id.dialogH)
        val dialogS = container.findViewById<TextInputEditText>(R.id.dialogS)
        val dialogV = container.findViewById<TextInputEditText>(R.id.dialogV)

        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(vm.selectedColor.color, hsv)
        dialogH.setText(hsv[0].toString())
        dialogS.setText(hsv[1].toString())
        dialogV.setText(hsv[2].toString())

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.hsv_code)
            .setView(container)
            .setNegativeButton(R.string.cancel, null)
            .setPositiveButton(R.string.choose_color) { _, _ ->
                val hsvSelected = floatArrayOf(
                    dialogH.text.toString().toFloat(),
                    dialogS.text.toString().toFloat(),
                    dialogV.text.toString().toFloat()
                )
                val tmp = vm.selectedColor
                tmp.apply {
                    color = android.graphics.Color.HSVToColor(hsvSelected)
                    alpha = color.alpha
                    red = color.red
                    green = color.green
                    blue = color.blue
                }
                updateDisplays(tmp)
            }
            .show()

        dialogH.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                s.toString().isNotEmpty() && dialogS.text.toString().isNotEmpty() &&
                        dialogV.text.toString().isNotEmpty()
        }
        dialogS.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                dialogH.text.toString().isNotEmpty() && s.toString().isNotEmpty() &&
                        dialogV.text.toString().isNotEmpty()
        }
        dialogV.doAfterTextChanged { s ->
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
                dialogH.text.toString().isNotEmpty() &&
                        dialogS.text.toString().isNotEmpty() && s.toString().isNotEmpty()
        }

        dialogH.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun chooseColor() {
        val colorPicker = ColorPickerDialog(this, vm.selectedColor.color)
        if(isAlphaEnabled) colorPicker.alphaSliderVisible = true
        colorPicker.hexValueEnabled = true
        colorPicker.setTitle(R.string.choose_color)
        colorPicker.setOnColorChangedListener { color ->
            updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, color, 0))
        }
        colorPicker.show()
    }

    private fun updateDisplays(color: Color) {
        // Update all views that are not animated
        binding.displayAlpha.text = color.alpha.toString()
        binding.displayRed.text = color.red.toString()
        binding.displayGreen.text = color.green.toString()
        binding.displayBlue.text = color.blue.toString()
        binding.displayName.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(color))

        // Update ARGB TextView
        binding.displayArgb.text = if(isAlphaEnabled) {
            String.format(getString(R.string.argb_), color.alpha, color.red, color.green, color.blue)
        } else {
            String.format(getString(R.string.rgb_), color.red, color.green, color.blue)
        }
        // Update HEX TextView
        binding.displayHex.text = if(isAlphaEnabled) {
            String.format(getString(R.string.hex_),
                "%08X".format(color.color).uppercase(Locale.getDefault())
            )
        } else {
            String.format(getString(R.string.hex_),
                "%06X".format(0xFFFFFF and color.color).uppercase(Locale.getDefault())
            )
        }
        // Update HSV TextView
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(color.red, color.green, color.blue, hsv)
        binding.displayHsv.text = String.format(getString(R.string.hsv_), String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[1]), String.format(Constants.HSV_FORMAT_STRING, hsv[2]))
        // Update CMYK TextView
        val cmyk = ct.getCmykFromRgb(color.red, color.green, color.blue)
        binding.displayCmyk.text = String.format(getString(R.string.cmyk_), cmyk[0], cmyk[1], cmyk[2], cmyk[3])

        // Update text colors
        val textColor = ct.getTextColor(this, android.graphics.Color.argb(color.alpha, color.red, color.green, color.blue))
        binding.displayName.setTextColor(textColor)
        binding.displayArgb.setTextColor(textColor)
        binding.displayHex.setTextColor(textColor)
        binding.displayHsv.setTextColor(textColor)
        binding.displayCmyk.setTextColor(textColor)
        val colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(textColor, BlendModeCompat.SRC_ATOP)
        binding.copyName.colorFilter = colorFilter
        binding.copyArgb.colorFilter = colorFilter
        binding.copyHex.colorFilter = colorFilter
        binding.copyHsv.colorFilter = colorFilter
        binding.copyCmyk.colorFilter = colorFilter
        binding.saveColor.colorFilter = colorFilter
        binding.loadColor.colorFilter = colorFilter

        // Update animated views
        ValueAnimator.ofInt(binding.colorAlpha.progress, color.alpha).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator ->
                binding.colorAlpha.progress = valueAnimator.animatedValue as Int
                binding.colorContainer.setBackgroundColor(color.color)
            }
            doOnEnd { vm.selectedColor = color }
        }.start()

        ValueAnimator.ofInt(binding.colorRed.progress, color.red).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> binding.colorRed.progress = valueAnimator.animatedValue as Int }
        }.start()

        ValueAnimator.ofInt(binding.colorGreen.progress, color.green).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> binding.colorGreen.progress = valueAnimator.animatedValue as Int }
        }.start()

        ValueAnimator.ofInt(binding.colorBlue.progress, color.blue).apply {
            duration = Constants.COLOR_ANIMATION_DURATION
            addUpdateListener { valueAnimator -> binding.colorBlue.progress = valueAnimator.animatedValue as Int }
        }.start()

        // Save color to shared preferences for restoring on the next app start
        st.putInt(Constants.NAME_SELECTED_COLOR, color.color)
    }

    private fun finishWithSelectedColor() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constants.EXTRA_CHOOSE_COLOR, vm.selectedColor.color)
        })
        finish()
    }

    private fun setDefaultComponentValues() {
        // Initialize views
        binding.displayName.text = String.format(getString(R.string.name_), cnt.getColorNameFromColor(vm.selectedColor))
        binding.displayArgb.text = String.format(
            getString(R.string.argb_),
            vm.selectedColor.alpha, vm.selectedColor.red, vm.selectedColor.green, vm.selectedColor.blue
        )
        binding.displayHex.text = String.format(getString(R.string.hex_),
            "%08X".format(vm.selectedColor.color).uppercase(
                Locale.getDefault()
            )
        )
        val hsv = FloatArray(3)
        android.graphics.Color.RGBToHSV(vm.selectedColor.red, vm.selectedColor.green, vm.selectedColor.blue, hsv)
        binding.displayHsv.text = String.format(
            getString(R.string.hsv_),
            String.format(Constants.HSV_FORMAT_STRING, hsv[0]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[1]),
            String.format(Constants.HSV_FORMAT_STRING, hsv[2])
        )
        val cmyk = ct.getCmykFromRgb(vm.selectedColor.red, vm.selectedColor.green, vm.selectedColor.blue)
        binding.displayCmyk.text =
            String.format(getString(R.string.cmyk_), cmyk[0], cmyk[1], cmyk[2], cmyk[3])
    }

    private fun initializeButtonSection() {
        // Edit codes
        binding.editHex.setOnClickListener { editHexCode() }
        binding.editHsv.setOnClickListener { editHSVCode() }

        // Speak color
        binding.speakColor.setOnClickListener {
            if (vm.isInstant) {
                // It's not allowed to use tts in an instant app
                showInstantAppInstallDialog(R.string.instant_install_m)
            } else vm.speakColor()
        }

        binding.pick.setOnClickListener { chooseColor() }
        binding.pickRandomColor.setOnClickListener { updateDisplays(ct.getRandomColor()) }
        binding.pickFromImage.setOnClickListener { pickColorFromImage() }
    }

    private fun initializeColorContainerSection() {
        binding.colorContainer.setOnClickListener { chooseColor() }

        // Load color
        binding.loadColor.setOnClickListener {
            startActivityForResult(
                Intent(this, ColorSelectionActivity::class.java),
                Constants.REQ_LOAD_COLOR
            )
        }

        // Save color
        binding.saveColor.setOnClickListener { showSaveColorDialog(cnt, vm) }

        // Copy color codes
        binding.copyName.setOnClickListener { cbt.copyNameToClipboard(cnt.getColorNameFromColor(vm.selectedColor)) }
        binding.copyArgb.setOnClickListener { cbt.copyArgbToClipboard(vm.selectedColor) }
        binding.copyHex.setOnClickListener { cbt.copyHexToClipboard(vm.selectedColor) }
        binding.copyHsv.setOnClickListener { cbt.copyHsvToClipboard(vm.selectedColor) }
        binding.copyCmyk.setOnClickListener { cbt.copyCMYKToClipboard(vm.selectedColor) }
    }

    private fun initializeSeekBarSection() {
        // Initialize other layout components
        binding.colorAlpha.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.gray), BlendModeCompat.SRC_ATOP
        )
        binding.colorRed.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP
            )
        binding.colorRed.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.red), BlendModeCompat.SRC_ATOP
        )
        binding.colorGreen.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP
            )
        binding.colorGreen.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.green), BlendModeCompat.SRC_ATOP
        )
        binding.colorBlue.progressDrawable.colorFilter =
            BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                ContextCompat.getColor(this, R.color.blue), BlendModeCompat.SRC_ATOP
            )
        binding.colorBlue.thumb.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
            ContextCompat.getColor(this, R.color.blue), BlendModeCompat.SRC_ATOP
        )

        binding.colorAlpha.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    binding.displayAlpha.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, progress, binding.colorRed.progress,
                        binding.colorGreen.progress, binding.colorBlue.progress, -1))
                }
                if ((vm.showTransparencyWarning && progress > 20) || (!vm.showTransparencyWarning && progress <= 20)) {
                    vm.showTransparencyWarning = !vm.showTransparencyWarning
                    invalidateOptionsMenu()
                }
            }
        })
        binding.colorRed.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    binding.displayRed.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, binding.colorAlpha.progress,
                        progress, binding.colorGreen.progress, binding.colorBlue.progress, -1))
                }
            }
        })
        binding.colorGreen.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    binding.displayGreen.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, binding.colorAlpha.progress,
                        binding.colorRed.progress, progress, binding.colorBlue.progress, -1))
                }
            }
        })
        binding.colorBlue.setOnSeekBarChangeListener(object : SimpleOnSeekBarChangeListener() {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val value = progress.toString()
                    binding.displayBlue.text = value
                    updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, binding.colorAlpha.progress,
                        binding.colorRed.progress, binding.colorGreen.progress, progress, -1))
                }
            }
        })
    }

    override fun onSettingsOptionsItemSelected(itemId: Int) {
        when(itemId) {
            R.id.action_rate -> showRatingDialog()
            R.id.action_share-> showRecommendationDialog()
        }
    }

    private fun enableAlpha(enabled: Boolean) {
        // Set alpha to 100%
        updateDisplays(Color(0, Constants.NAME_SELECTED_COLOR, 255, vm.selectedColor.red,
            vm.selectedColor.green, vm.selectedColor.blue, -1))
        // Show / hide components
        val visibility = if(enabled) View.VISIBLE else View.GONE
        binding.colorAlpha.visibility = visibility
        binding.displayAlpha.visibility = visibility
        binding.displayAlphaLabel.visibility = visibility
    }

    override fun onColorSelected(color: Color) = updateDisplays(color)

    private fun subscribeToPreferenceValues() {
        getPrefObserver(this@MainActivity, Constants.ENABLE_ALPHA, {
            isAlphaEnabled = it
            enableAlpha(isAlphaEnabled)
        }, true)
    }
}