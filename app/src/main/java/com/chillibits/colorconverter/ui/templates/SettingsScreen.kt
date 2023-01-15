/*
 * Copyright © Marc Auberer 2017-2023. All rights reserved
 */

package com.chillibits.colorconverter.ui.templates

import android.content.Context
import android.content.pm.PackageManager
import com.chillibits.colorconverter.shared.Constants
import com.chillibits.simplesettings.clicklistener.PlayStoreClickListener
import com.chillibits.simplesettings.clicklistener.WebsiteClickListener
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import com.mrgames13.jimdo.colorconverter.R

fun Context.showSettings() {
    val config = SimpleSettingsConfig().apply {
        showResetOption = true
        iconSpaceReservedByDefault = false
    }

    SimpleSettings(this, config).show {
        Section {
            titleRes = R.string.general
            SwitchPref {
                key = Constants.ENABLE_ALPHA
                titleRes = R.string.enable_alpha
                summaryOnRes = R.string.enabled
                summaryOffRes = R.string.disabled
                defaultValue = true
            }
            SwitchPref {
                key = Constants.SPEAK_COLOR
                titleRes = R.string.speak_color
                summaryOnRes = R.string.enabled
                summaryOffRes = R.string.disabled
            }
        }
        Section {
            titleRes = R.string.about
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