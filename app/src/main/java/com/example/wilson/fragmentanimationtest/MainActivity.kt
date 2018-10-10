package com.example.wilson.fragmentanimationtest

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_show_a.setOnClickListener {
            showFragment("A")
        }
        btn_show_b.setOnClickListener {
            showFragment("B")
        }
        btn_show_sub.setOnClickListener {
            startActivityForResult(Intent(this@MainActivity, SubActivity::class.java), REQ_CODE_SUB)
        }
        btn_main_2.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity2::class.java))
            finish()
        }
        showFragment("A")
    }

    fun showFragment(name: String) {
        val fm = supportFragmentManager
        val f = ButtonFrag.newInstance(name, when (name) {
            "A" -> Color.WHITE
            "B" -> Color.BLUE
            else -> Color.RED
        })

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
            startActivityForResult(Intent(this@MainActivity, DummyActivity::class.java), REQ_CODE_SUB)
        }
    }

    override fun onResume() {
        super.onResume()
        updateFragmentName()
    }

    private fun updateFragmentName() {
        val fragment = supportFragmentManager.findFragmentById(R.id.vg_content)
        val fragmentName = fragment?.let {
            it.arguments?.getString("name") ?: "null"
        } ?: "null"

        tv_cur_fragment.text = "Current Fragment: ${fragmentName}"
    }

    companion object {
        const val REQ_CODE_SUB = 100
    }

}

class ButtonFrag : Fragment() {
    lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        name = arguments?.getString("name") ?: "name"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Thread.sleep(300)
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            Toast.makeText(activity, "I'm ${name}", Toast.LENGTH_SHORT).show()
        }
        view.setBackgroundColor(arguments?.getInt("bg") ?: Color.WHITE)
        button.text = "I'm ${name}"
    }

    companion object {
        fun newInstance(name: String, bg: Int): ButtonFrag {

            val frag = ButtonFrag()
            frag.arguments = Bundle().apply {
                putString("name", name)
                putInt("bg", bg)
            }
            return frag
        }
    }
}


