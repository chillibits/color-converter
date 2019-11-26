package com.mrgames13.jimdo.colorconverter.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mrgames13.jimdo.colorconverter.R
import com.mrgames13.jimdo.colorconverter.model.Color
import com.mrgames13.jimdo.colorconverter.tools.ColorTools
import com.mrgames13.jimdo.colorconverter.tools.StorageTools
import kotlinx.android.synthetic.main.activity_color_selection.*
import kotlinx.android.synthetic.main.toolbar.*

class ColorSelectionActivity : AppCompatActivity() {

    // Tools packages
    private val ct = ColorTools(this)
    private val st = StorageTools(this)

    // Varibles as objects
    private lateinit var colors: ArrayList<Color>
    private var selectedColor: Color? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selection)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Load colors
        colors = st.loadColors()


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (selectedColor != null) menuInflater.inflate(R.menu.menu_activity_color_selection, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_edit -> {
                val newName = EditText(this)
                newName.hint = getString(R.string.choose_name)
                newName.setText(selectedColor?.name)
                newName.inputType = InputType.TYPE_TEXT_VARIATION_URI

                AlertDialog.Builder(this)
                    .setTitle(R.string.rename)
                    .setView(newName)
                    .setPositiveButton(R.string.rename) { _, _ ->
                        val name = newName.text.toString().trim()
                        if(name.isNotEmpty()) st.updateColor(selectedColor!!.id, name)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()

                newName.requestFocus()
            }
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.delete)
                    .setMessage(R.string.delete_m)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        st.removeColor(selectedColor!!.id)
                    }
            }
            R.id.action_done -> {
                val data = Intent()
                data.putExtra("Color", selectedColor!!.color)
                setResult(Activity.RESULT_OK, data)
                finish()
            }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun selectedColor(color: Color) {
        selectedColor = color
        invalidateOptionsMenu()
        supportActionBar?.subtitle = R.string.selected.toString() + ": " + color.name
        animateAppAndStatusBar(color.color)
    }

    private fun animateAppAndStatusBar(toColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val animator = ViewAnimationUtils.createCircularReveal(
                reveal,
                toolbar.width / 2,
                toolbar.height / 2,
                0f,
                toolbar.width / 2.0f + 50
            )
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    reveal.setBackgroundColor(toColor)
                }

                override fun onAnimationEnd(animation: Animator) {
                    reveal_background.setBackgroundColor(toColor)
                }
            })
            animator.duration = 480
            animator.start()
            reveal.visibility = View.VISIBLE
        } else {
            reveal.setBackgroundColor(toColor)
            reveal_background.setBackgroundColor(toColor)
        }
    }
}