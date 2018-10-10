package com.example.wilson.fragmentanimationtest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_dummy.*

class DummyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        btn_finish.setOnClickListener { finish() }
    }

}
