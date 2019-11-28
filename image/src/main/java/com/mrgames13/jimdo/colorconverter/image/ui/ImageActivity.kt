package com.mrgames13.jimdo.colorconverter.image.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asksira.bsimagepicker.BSImagePicker
import com.bumptech.glide.Glide
import com.mrgames13.jimdo.colorconverter.image.R
import com.mrgames13.jimdo.colorconverter.tools.ColorTools
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.IOException

class ImageActivity : AppCompatActivity(), BSImagePicker.OnSingleImageSelectedListener, BSImagePicker.ImageLoaderDelegate {

    // Constants

    // Tools packages
    private val ct = ColorTools(this)

    // Variables as objects
    private var selectedImage: Bitmap? = null

    // Variables

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        choose_from_gallery.setOnClickListener { chooseFromGallery() }
        choose_from_photo.setOnClickListener { chooseFromPhoto() }
        choose_from_url.setOnClickListener { chooseFromUrl() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_image, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.action_gallery -> chooseFromGallery()
            R.id.action_photo -> chooseFromPhoto()
            R.id.choose_from_url -> chooseFromUrl()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseFromGallery() {
        val picker = BSImagePicker.Builder("com.mrgames13.jimdo.colorconverter.fileprovider")
            .hideCameraTile()
            .build()
        picker.show(supportFragmentManager, "picker")
    }

    private fun chooseFromPhoto() {

    }

    private fun chooseFromUrl() {

    }

    private fun setBitmapToImageView() {
        try {
            image.scaleType = ImageView.ScaleType.FIT_CENTER
            color_button_container.visibility = View.GONE
            image.setImageBitmap(selectedImage)
            vibrant_color.setBackgroundColor(ct.getVibrantColor(selectedImage!!))
            light_vibrant_color.setBackgroundColor(ct.getLightVibrantColor(selectedImage!!))
            dark_vibrant_color.setBackgroundColor(ct.getDarkVibrantColor(selectedImage!!))
            muted_color.setBackgroundColor(ct.getMutedColor(selectedImage!!))
            light_muted_color.setBackgroundColor(ct.getLightMutedColor(selectedImage!!))
            dark_muted_color.setBackgroundColor(ct.getDarkMutedColor(selectedImage!!))
            val anim = AnimationUtils.loadAnimation(this, R.anim.animation_scale_up)
            color_button_container.visibility = View.VISIBLE
            color_button_container.startAnimation(anim)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this@ImageActivity, R.string.image_broken, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSingleImageSelected(uri: Uri?, tag: String?) {
        try {
            val bitmap = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                    val source = ImageDecoder.createSource(this.contentResolver, uri!!)
                    ImageDecoder.decodeBitmap(source)
                }
                else -> MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            }


            selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            setBitmapToImageView()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun loadImage(imageFile: File?, ivImage: ImageView?) {
        Glide.with(this).load(imageFile).into(image)
    }
}
