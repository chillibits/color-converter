/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.exifinterface.media.ExifInterface
import com.chillibits.colorconverter.tools.*
import com.chillibits.colorconverter.viewmodel.DetailedFlagView
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.mrgames13.jimdo.colorconverter.R
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class ImageActivity : AppCompatActivity() {

    // Tools packages
    private val ct = ColorTools(this)
    private val cnt = ColorNameTools(this)
    private val st = StorageTools(this)

    // Variables as objects
    private lateinit var tts: TextToSpeech
    private var speakItem: MenuItem? = null
    private var valueSelectedColor: Int = Color.BLACK
    private var valueVibrantColor: Int = Color.BLACK
    private var valueVibrantColorLight: Int = Color.BLACK
    private var valueVibrantColorDark: Int = Color.BLACK
    private var valueMutedColor: Int = Color.BLACK
    private var valueMutedColorLight: Int = Color.BLACK
    private var valueMutedColorDark: Int = Color.BLACK
    private var imageUri: String? = null

    // Variables
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        // Apply window insets
        window.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    toolbar?.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                    colorButtonContainer.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), insets.systemWindowInsetBottom + dpToPx(3))
                    insets
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            }
        }

        // Initialize toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize other layout components
        image.colorListener = ColorListener { color, _ ->
            valueSelectedColor = color
            selectedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
            if(speakItem != null && speakItem!!.isChecked) speakColor()
        }
        image.flagView = DetailedFlagView(this, R.layout.flag_layout).apply {
            isFlipAble = false
        }

        selectedColor.setOnClickListener { finishWithResult(valueSelectedColor) }
        vibrantColor.setOnClickListener { finishWithResult(valueVibrantColor) }
        lightVibrantColor.setOnClickListener { finishWithResult(valueVibrantColorLight) }
        darkVibrantColor.setOnClickListener { finishWithResult(valueVibrantColorDark) }
        mutedColor.setOnClickListener { finishWithResult(valueMutedColor) }
        lightMutedColor.setOnClickListener { finishWithResult(valueMutedColorLight) }
        darkMutedColor.setOnClickListener { finishWithResult(valueMutedColorDark) }

        // Initialize tts
        tts = TextToSpeech(this) { status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, R.string.language_not_available, Toast.LENGTH_SHORT).show()
                } else initialized = true
            } else Toast.makeText(this, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
        }

        if(intent.hasExtra(Constants.EXTRA_IMAGE_URI)) {
            // Load default image
            val defaultImageUri = intent.getParcelableExtra(Constants.EXTRA_IMAGE_URI) as Uri
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, defaultImageUri)
            applyImage(bitmap)
        } else if(savedInstanceState == null) {
            // Launch image picker
            chooseImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_image, menu)
        speakItem = menu?.getItem(0)
        speakItem?.isChecked = st.getBoolean(Constants.SPEAK_COLOR)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.action_speak -> {
                val newState = !item.isChecked
                st.putBoolean(Constants.SPEAK_COLOR, newState)
                item.isChecked = newState
            }
            R.id.action_new_image -> chooseImage()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("ImageUri", imageUri.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        imageUri = savedInstanceState.getString("ImageUri")
        applyImage(applyRotation(BitmapFactory.decodeFile(imageUri), imageUri!!)!!)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.REQ_IMAGE_PICKER) {
            if(resultCode == Activity.RESULT_OK) {
                try{
                    imageUri = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.get(0).toString()
                    applyImage(applyRotation(BitmapFactory.decodeFile(imageUri), imageUri!!)!!)
                } catch (ignored: Exception) {}
            } else finish()
        }
    }

    private fun applyRotation(source: Bitmap, path: String) = when (ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(source, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(source, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(source, 270F)
        else -> source
    }

    private fun rotateImage(source: Bitmap, angle: Float) = Matrix().run {
        postRotate(angle)
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, this, true)
    }

    private fun applyImage(bitmap: Bitmap) {
        image.setPaletteDrawable(BitmapDrawable(resources, bitmap))
        valueVibrantColor = ct.getVibrantColor(bitmap)
        vibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueVibrantColor, BlendModeCompat.SRC_IN)
        valueVibrantColorLight = ct.getLightVibrantColor(bitmap)
        lightVibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueVibrantColorLight, BlendModeCompat.SRC_IN)
        valueVibrantColorDark = ct.getDarkVibrantColor(bitmap)
        darkVibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueVibrantColorDark, BlendModeCompat.SRC_IN)
        valueMutedColor = ct.getMutedColor(bitmap)
        mutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueMutedColor, BlendModeCompat.SRC_IN)
        valueMutedColorLight = ct.getLightMutedColor(bitmap)
        lightMutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueMutedColorLight, BlendModeCompat.SRC_IN)
        valueMutedColorDark = ct.getDarkMutedColor(bitmap)
        darkMutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(valueMutedColorDark, BlendModeCompat.SRC_IN)
        image.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            chooseImage()
    }

    private fun chooseImage() {
        val options = Options.init()
            .setExcludeVideos(true)
            .setRequestCode(Constants.REQ_IMAGE_PICKER)
        Pix.start(this, options)
    }

    private fun speakColor() {
        if(isAudioMuted()) {
            Toast.makeText(this, R.string.audio_muted, Toast.LENGTH_SHORT).show()
        } else {
            if(initialized) {
                val colorName = cnt.getColorNameFromColor(com.chillibits.colorconverter.model.Color(0, "", valueSelectedColor, 0))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(colorName, TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts.speak(colorName, TextToSpeech.QUEUE_FLUSH, null)
                }
            } else {
                Toast.makeText(this, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun finishWithResult(color: Int) {
        Intent().apply {
            putExtra(Constants.EXTRA_COLOR, color)
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun isAudioMuted(): Boolean {
        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }
}
