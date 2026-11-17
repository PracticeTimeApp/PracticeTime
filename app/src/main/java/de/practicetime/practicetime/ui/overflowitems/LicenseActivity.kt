/*
 * This software is licensed under the MIT license
 */

package de.practicetime.practicetime.ui.overflowitems

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import de.practicetime.practicetime.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class LicenseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        setSupportActionBar(findViewById(R.id.license_toolbar))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            title = getString(R.string.licences_title)
        }

        setupLicenseItem(R.id.layout_license_lib1, R.string.lib1_name, R.string.lib1_author, R.string.lib1_link, "LICENSE_APACHE-2.0.txt")
        setupLicenseItem(R.id.layout_license_lib2, R.string.lib2_name, R.string.lib2_author, R.string.lib2_link, "LICENSE_APACHE-2.0.txt")
        setupLicenseItem(R.id.layout_license_lib3, R.string.lib3_name, R.string.lib3_author, R.string.lib3_link, "LICENSE_APACHE-2.0.txt")
        setupLicenseItem(R.id.layout_license_lib4, R.string.lib4_name, R.string.lib4_author, R.string.lib4_link, "LICENSE_APACHE-2.0.txt")
    }

    private fun setupLicenseItem(layoutId: Int, nameRes: Int, authorRes: Int, linkRes: Int, licenseFile: String) {
        val layout = findViewById<LinearLayout>(layoutId) ?: return
        val tvName = layout.findViewById<TextView>(R.id.tv_license_name)
        val tvAuthor = layout.findViewById<TextView>(R.id.tv_license_author)

        tvName.text = getString(nameRes)
        tvAuthor.text = getString(authorRes)

        layout.setOnClickListener {
            val items = arrayOf<CharSequence>("View website", "View license")
            AlertDialog.Builder(this)
                .setTitle(tvName.text)
                .setItems(items) { _, which ->
                    if (which == 0) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(linkRes))))
                    } else if (which == 1) {
                        showLicenseText(licenseFile)
                    }
                }
                .show()
        }
    }

    private fun showLicenseText(licenseTextFile: String) {
        try {
            val reader = BufferedReader(InputStreamReader(
                assets.open(licenseTextFile), "UTF-8"))
            val licenseText = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                licenseText.append(line).append("\n")
            }
            AlertDialog.Builder(this)
                .setMessage(licenseText)
                .show()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
