/*
 * Copyright © Marc Auberer 2017-2021. All rights reserved
 */

package com.chillibits.colorconverter.ui.dialog

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.chillibits.simplesettings.clicklistener.PlayStoreClickListener
import com.chillibits.simplesettings.clicklistener.WebsiteClickListener
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import com.mrgames13.jimdo.colorconverter.R

fun Context.showSettings() {
    val config = SimpleSettingsConfig().apply {
        showResetOption = true
    }

    val about = getString(R.string.about)
    Log.d("CC", about)

    SimpleSettings(this, config).show {
        Section {
            titleRes = R.string.general
            /*
            - Disable alpha
            - Speak colors
            - Rate
            - Recommend
            - GH
            - Libraries
            - Version
            - More apps
            - Developers
             */

        }
        Section {
            title = about
            TextPref {
                titleRes = R.string.colorconverter_on_github
                summaryRes = R.string.tap_to_visit_us_on_github
                onClick = WebsiteClickListener(this@showSettings, this@showSettings.getString(R.string.url_github))
            }
            LibsPref {
                title = context.resources.getText(R.string.open_source_licenses).toString()
                summaryRes = R.string.tap_here_to_view_all_used_libraries
            }
            TextPref {
                titleRes = R.string.app_version
                summary = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES).versionName
                onClick = PlayStoreClickListener(this@showSettings)
            }
            TextPref {
                titleRes = R.string.the_developers
                summaryRes = R.string.developers
                onClick = WebsiteClickListener(this@showSettings, this@showSettings.getString(R.string.url_homepage))
            }
            TextPref {
                titleRes = R.string.more_apps_from_us
                summaryRes = R.string.our_playstore_page
                onClick = WebsiteClickListener(this@showSettings, this@showSettings.getString(R.string.url_play_developers_page))
            }
        }
    }
}