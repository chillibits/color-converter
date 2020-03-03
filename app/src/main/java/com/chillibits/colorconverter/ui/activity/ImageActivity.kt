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
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.viewmodel.DetailedFlagView
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.mrgames13.jimdo.colorconverter.R
import com.skydoves.colorpickerview.listeners.ColorListener
import kotlinx.android.synthetic.main.activity_image.*
import java.util.*

// Constants
private const val REQ_IMAGE_PICKER = 10001

class ImageActivity : AppCompatActivity() {

    // Tools packages
    private val ct = ColorTools(this)
    private val cnt = ColorNameTools(this)
    private val st = StorageTools(this)

    // Variables as objects
    private lateinit var tts: TextToSpeech
    private var speakItem: MenuItem? = null
    private var selectedColor: Int = Color.BLACK
    private var vibrantColor: Int = Color.BLACK
    private var vibrantColorLight: Int = Color.BLACK
    private var vibrantColorDark: Int = Color.BLACK
    private var mutedColor: Int = Color.BLACK
    private var mutedColorLight: Int = Color.BLACK
    private var mutedColorDark: Int = Color.BLACK
    private var imageUri: String? = null

    // Variables
    private var initialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        image.colorListener = ColorListener { color, _ ->
            selectedColor = color
            selected_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
            if(speakItem != null && speakItem!!.isChecked) speakColor()
        }
        image.flagView = DetailedFlagView(this, R.layout.flag_layout)

        selected_color.setOnClickListener { finishWithResult(selectedColor) }
        vibrant_color.setOnClickListener { finishWithResult(vibrantColor) }
        light_vibrant_color.setOnClickListener { finishWithResult(vibrantColorLight) }
        dark_vibrant_color.setOnClickListener { finishWithResult(vibrantColorDark) }
        muted_color.setOnClickListener { finishWithResult(mutedColor) }
        light_muted_color.setOnClickListener { finishWithResult(mutedColorLight) }
        dark_muted_color.setOnClickListener { finishWithResult(mutedColorDark) }

        tts = TextToSpeech(this) { status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = tts.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, R.string.language_not_available, Toast.LENGTH_SHORT).show()
                } else {
                    initialized = true
                }
            } else {
                Toast.makeText(this, R.string.initialization_failed, Toast.LENGTH_SHORT).show()
            }
        }

        if(intent.hasExtra("ImageUri")) {
            // Load default image
            val defaultImageUri = intent.getParcelableExtra("ImageUri") as Uri
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
        speakItem?.isChecked = st.getBoolean("speak_color")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.action_speak -> {
                val newState = !item.isChecked
                st.putBoolean("speak_color", newState)
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
        if(requestCode == REQ_IMAGE_PICKER) {
            if(resultCode == Activity.RESULT_OK) {
                try{
                    imageUri = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.get(0).toString()
                    applyImage(applyRotation(BitmapFactory.decodeFile(imageUri), imageUri!!)!!)
                } catch (ignored: Exception) {}
            } else finish()
        }
    }

    private fun applyRotation(source: Bitmap, path: String): Bitmap? {
        val ei = ExifInterface(path)
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(source, 90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(source, 180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(source, 270F)
            else -> source
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun applyImage(bitmap: Bitmap) {
        image.setPaletteDrawable(BitmapDrawable(resources, bitmap))
        vibrantColor = ct.getVibrantColor(bitmap)
        vibrant_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vibrantColor, BlendModeCompat.SRC_IN)
        vibrantColorLight = ct.getLightVibrantColor(bitmap)
        light_vibrant_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vibrantColorLight, BlendModeCompat.SRC_IN)
        vibrantColorDark = ct.getDarkVibrantColor(bitmap)
        dark_vibrant_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vibrantColorDark, BlendModeCompat.SRC_IN)
        mutedColor = ct.getMutedColor(bitmap)
        muted_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mutedColor, BlendModeCompat.SRC_IN)
        mutedColorLight = ct.getLightMutedColor(bitmap)
        light_muted_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mutedColorLight, BlendModeCompat.SRC_IN)
        mutedColorDark = ct.getDarkMutedColor(bitmap)
        dark_muted_color.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(mutedColorDark, BlendModeCompat.SRC_IN)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(this, Options.init().setRequestCode(REQ_IMAGE_PICKER))
            }
        }
    }

    private fun chooseImage() {
        Pix.start(this, Options.init().setRequestCode(REQ_IMAGE_PICKER))
    }

    private fun speakColor() {
        if(isAudioMuted()) {
            Toast.makeText(this, R.string.audio_muted, Toast.LENGTH_SHORT).show()
        } else {
            if(initialized) {
                val colorName = cnt.getColorNameFromColor(com.chillibits.colorconverter.model.Color(0, "", selectedColor, 0))
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
        val data = Intent()
        data.putExtra("Color", color)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun isAudioMuted(): Boolean {
        val manager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return manager.ringerMode != AudioManager.RINGER_MODE_NORMAL
    }
}
