package com.tdsoft.learnodo.ui.viewclass

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tdsoft.learnodo.R
import kotlinx.android.synthetic.main.activity_view_class.*

class ViewClassActivity : AppCompatActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, ViewClassActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_class)
        button.setOnClickListener {
            scoreGraphView.setMarks(50,40,10)
        }
    }
}