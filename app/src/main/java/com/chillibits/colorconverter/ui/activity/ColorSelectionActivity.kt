/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.colorconverter.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.LayoutTransition
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillibits.colorconverter.model.Color
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.colorconverter.shared.toObj
import com.chillibits.colorconverter.storage.AppDatabase
import com.chillibits.colorconverter.tools.ColorTools
import com.chillibits.colorconverter.tools.StorageTools
import com.chillibits.colorconverter.ui.adapter.ColorsAdapter
import com.chillibits.colorconverter.viewmodel.ColorSelectionViewModel
import com.mrgames13.jimdo.colorconverter.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_color_selection.*
import kotlinx.android.synthetic.main.dialog_color_rename.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ColorSelectionActivity : AppCompatActivity(), ColorsAdapter.ColorSelectionListener {

    // Tools packages
    @Inject lateinit var db: AppDatabase
    @Inject lateinit var st: StorageTools
    @Inject lateinit var ct: ColorTools

    // Variables as objects
    private val vm by viewModels<ColorSelectionViewModel>()
    private lateinit var adapter: ColorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color_selection)
        applyWindowInsets()

        // Initialize toolbar
        toolbar.layoutTransition = LayoutTransition()
        toolbar.setTitle(R.string.saved_colors)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize RecyclerView
        adapter = ColorsAdapter(this, this, st, ct)
        savedColors.layoutManager = LinearLayoutManager(this)
        savedColors.adapter = adapter

        // Setup data observer
        vm.colors.observe(this, { data ->
            adapter.updateData(data.map { it.toObj() })
            noItems.visibility = if (data.isNotEmpty()) View.GONE else View.VISIBLE
            loading.visibility = View.GONE
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_color_selection, menu)
        val isColorSelected = vm.selectedColor != null
        menu?.apply {
            findItem(R.id.action_import)?.isVisible = !isColorSelected
            findItem(R.id.action_export)?.isVisible = !isColorSelected
            findItem(R.id.action_edit)?.isVisible = isColorSelected
            findItem(R.id.action_delete)?.isVisible = isColorSelected
            findItem(R.id.action_done)?.isVisible = isColorSelected
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_import -> importColorPalette()
            R.id.action_export -> exportColorPalette()
            R.id.action_edit -> showRenameColorDialog()
            R.id.action_delete -> showDeleteColorDialog()
            R.id.action_done -> done()
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyWindowInsets() = window.run {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    val systemInsets = insets.getInsets(WindowInsets.Type.systemBars())
                    toolbar?.setPadding(0, systemInsets.top, 0, 0)
                    savedColors.setPadding(0, 0, 0, systemInsets.bottom)
                    insets
                }
                setDecorFitsSystemWindows(false)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                decorView.setOnApplyWindowInsetsListener { _, insets ->
                    toolbar?.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                    savedColors.setPadding(0, 0, 0, insets.systemWindowInsetBottom)
                    insets
                }
            }
            else -> {
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                statusBarColor = ContextCompat.getColor(context, R.color.colorPrimaryDark)
            }
        }
    }

    private fun done() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(Constants.EXTRA_COLOR, vm.selectedColor!!.color)
        })
        finish()
    }

    private fun showRenameColorDialog() {
        // Initialize views
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_color_rename, container, false)
        val newName = dialogView.dialogName
        newName.setText(vm.selectedColor?.name)

        // Create dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.rename)
            .setView(dialogView)
            .setPositiveButton(R.string.rename) { _, _ ->
                val name = newName.text.toString().trim()
                if (name.isNotEmpty()) {
                    vm.selectedColor?.name = name
                    // Update color
                    CoroutineScope(Dispatchers.IO).launch {
                        vm.update()
                        // Refresh subtitle
                        CoroutineScope(Dispatchers.Main).launch {
                            changeSubtitle("${getString(R.string.selected)}: $name")
                        }
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()

        // Prepare views
        newName.doAfterTextChanged {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = it.toString().isNotEmpty()
        }
        newName.selectAll()
        newName.requestFocus()
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun showDeleteColorDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.delete)
            .setMessage(String.format(getString(R.string.delete_m), vm.selectedColor?.name))
            .setIcon(R.drawable.delete_forever)
            .setPositiveButton(R.string.delete) { _, _ ->
                // Delete color in local db
                CoroutineScope(Dispatchers.IO).launch {
                    vm.delete()
                    // Reset toolbar
                    CoroutineScope(Dispatchers.Main).launch {
                        changeSubtitle(null)
                        invalidateOptionsMenu()
                        reveal.setBackgroundResource(R.color.colorPrimary)
                        revealBackground.setBackgroundResource(R.color.colorPrimary)
                    }
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun animateAppAndStatusBar(toColor: Int) {
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
                revealBackground.setBackgroundColor(toColor)
            }
        })

        animator.duration = 480
        animator.start()
        reveal.visibility = View.VISIBLE
    }

    override fun onColorSelected(color: Color) {
        vm.selectedColor = color
        invalidateOptionsMenu()
        changeSubtitle("${getString(R.string.selected)}: ${color.name}")
        animateAppAndStatusBar(color.color)
    }

    private fun changeSubtitle(@Nullable subtitle: String?) {
        if (subtitle == null) {
            toolbar.layoutTransition = null
            supportActionBar?.subtitle = null
        } else {
            toolbar.layoutTransition = LayoutTransition()
            supportActionBar?.subtitle = subtitle
        }
    }

    private fun exportColorPalette() {

    }

    private fun importColorPalette() {

    }
}