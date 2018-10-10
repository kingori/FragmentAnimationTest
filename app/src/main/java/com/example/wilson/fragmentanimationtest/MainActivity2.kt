package com.example.wilson.fragmentanimationtest

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_2.*

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_2)
        btn_show_a.setOnClickListener {
            showFragment("A")
        }
        btn_show_b.setOnClickListener {
            showFragment("B")
        }
        btn_show_sub.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity2, SubActivity::class.java), REQ_CODE_SUB)
        }
        btn_main_2.setOnClickListener {
            startActivity(Intent(this@MainActivity2, MainActivity::class.java))
            finish()
        }
        showFragment("A")
    }

    fun showFragment(name: String) {
        val fm = supportFragmentManager
        var f = fm.findFragmentByTag(name)
        if (f == null) {
            f = ButtonFrag.newInstance(name, when (name) {
                "A" -> Color.WHITE
                "B" -> Color.BLUE
                else -> Color.RED
            })
        }
        val transaction = fm.beginTransaction()
                .setCustomAnimations(R.anim.fragment_in_2, R.anim.fragment_out_2)
                .replace(R.id.vg_content, f, name)
                .runOnCommit {
                    updateFragmentName()
                }
        transaction.commitNowAllowingStateLoss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SUB && resultCode == Activity.RESULT_OK) {
            val fragment = supportFragmentManager.findFragmentById(R.id.vg_content)
            if (fragment?.tag == "A") {
                showFragment("B")
            } else {
                showFragment("A")
            }
            startActivityForResult(Intent(this@MainActivity2, DummyActivity::class.java), REQ_CODE_SUB)
        }
    }

    override fun onResume() {
        super.onResume()
        updateFragmentName()
    }

    private fun updateFragmentName() {
        val fragment = supportFragmentManager.fragments.firstOrNull { it.isAdded && it.isVisible }
        val fragmentName = fragment?.let {
            it.arguments?.getString("name") ?: "null"
        } ?: "null"

        tv_cur_fragment.text = "Current Fragment: ${fragmentName}"
    }

    companion object {
        const val REQ_CODE_SUB = 100
    }
}

