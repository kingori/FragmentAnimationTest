package com.example.wilson.fragmentanimationtest

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_sub.*

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        btn.setOnClickListener {
            setResult(Activity.RESULT_OK, null)
            finish()
        }
    }

}
