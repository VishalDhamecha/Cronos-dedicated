package com.company.mysapcpsdkprojectoffline.mdui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.company.mysapcpsdkprojectoffline.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.settings_container, SettingsFragment()).commit()
    }
}