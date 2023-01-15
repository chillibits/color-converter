/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.exifinterface.media.ExifInterface
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.dpToPx
import com.chillibits.colorconverter.tools.ColorNameTools
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.view.DetailedFlagView
import com.chillibits.colorconverter.viewmodel.ImageViewModel
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.fxn.pix.Options
import com.fxn.pix.Pix
import com.fxn.utility.PermUtil
import com.mrgames13.jimdo.colorconverter.R
import com.mrgames13.jimdo.colorconverter.databinding.ActivityImageBinding
import com.skydoves.colorpickerview.listeners.ColorListener
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class ImageActivity : AppCompatActivity() {

    // Tools packages
    @Inject lateinit var cnt: ColorNameTools
    @Inject lateinit var st: StorageTools

    // Variables as objects
    private lateinit var binding: ActivityImageBinding
    private val vm by viewModels<ImageViewModel>()
    private var speakItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply window insets
        applyWindowInsets()

        // Initialize toolbar
        binding.toolbar.setTitle(R.string.pick_color_from_image)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize other layout components
        binding.image.colorListener = ColorListener { color, _ ->
            vm.valueSelectedColor = color
            binding.selectedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_IN)
            if(speakItem != null && speakItem!!.isChecked) speakColor()
        }
        binding.image.flagView = DetailedFlagView(this, R.layout.flag_layout).apply {
            isFlipAble = false
        }

        binding.selectedColor.setOnClickListener { finishWithResult(vm.valueSelectedColor) }
        binding.vibrantColor.setOnClickListener { finishWithResult(vm.valueVibrantColor) }
        binding.lightVibrantColor.setOnClickListener { finishWithResult(vm.valueVibrantColorLight) }
        binding.darkVibrantColor.setOnClickListener { finishWithResult(vm.valueVibrantColorDark) }
        binding.mutedColor.setOnClickListener { finishWithResult(vm.valueMutedColor) }
        binding.lightMutedColor.setOnClickListener { finishWithResult(vm.valueMutedColorLight) }
        binding.darkMutedColor.setOnClickListener { finishWithResult(vm.valueMutedColorDark) }

        if(intent.hasExtra(Constants.EXTRA_IMAGE_URI)) {
            // Load default image
            val defaultImageUri = intent.getParcelableExtra<Uri>(Constants.EXTRA_IMAGE_URI)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, defaultImageUri)
            applyImage(bitmap)
        } else if(savedInstanceState == null) {
            // Launch image picker
            chooseImage()
        }

        vm.imageUri?.run { applyImage(applyRotation(BitmapFactory.decodeFile(this), this)) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_image, menu)
        speakItem = menu?.getItem(0)
        speakItem?.isChecked = getPrefBooleanValue(Constants.SPEAK_COLOR)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Constants.REQ_IMAGE_PICKER) {
            if(resultCode == Activity.RESULT_OK) {
                try{
                    vm.imageUri = data?.getStringArrayListExtra(Pix.IMAGE_RESULTS)?.get(0).toString()
                    applyImage(applyRotation(BitmapFactory.decodeFile(vm.imageUri), vm.imageUri!!)!!)
                } catch (e: IOException) {
                    Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                }
            } else finish()
        }
    }

    private fun applyWindowInsets() = window.run {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
                    if(systemInsets.top > 0) {
                        binding.toolbar.setPadding(0, systemInsets.top, 0, 0)
                        binding.colorButtonContainer.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), systemInsets.bottom + dpToPx(3))
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
                    binding.colorButtonContainer.setPadding(dpToPx(3), dpToPx(3), dpToPx(3), insets.systemWindowInsetBottom + dpToPx(3))
                    insets
                }
            }
            else -> {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            }
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
        vm.computeVibrantColors(bitmap)
        binding.image.setPaletteDrawable(BitmapDrawable(resources, bitmap))
        binding.vibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueVibrantColor, BlendModeCompat.SRC_IN)
        binding.lightVibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueVibrantColorLight, BlendModeCompat.SRC_IN)
        binding.darkVibrantColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueVibrantColorDark, BlendModeCompat.SRC_IN)
        binding.mutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueMutedColor, BlendModeCompat.SRC_IN)
        binding.lightMutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueMutedColorLight, BlendModeCompat.SRC_IN)
        binding.darkMutedColor.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(vm.valueMutedColorDark, BlendModeCompat.SRC_IN)
        binding.image.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseImage()
        } else {
            Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun chooseImage() {
        val options = Options.init()
            .setMode(Options.Mode.Picture)
            .setRequestCode(Constants.REQ_IMAGE_PICKER)
        Pix.start(this, options)
    }

    private fun speakColor() {
        if(isAudioMuted()) {
            Toast.makeText(this, R.string.audio_muted, Toast.LENGTH_SHORT).show()
        } else {
            if(vm.initialized) {
                val colorName = cnt.getColorNameFromColor(com.chillibits.colorconverter.model.Color(0, "", vm.valueSelectedColor, 0))
                vm.tts.speak(colorName, TextToSpeech.QUEUE_FLUSH, null, null)
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