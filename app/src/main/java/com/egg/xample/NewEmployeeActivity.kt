package com.egg.xample


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import xample.R

class NewEmployeeActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_word)
        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            val inFirstName = findViewById<EditText>(R.id.in_first_name).text.toString()
            val inLastName = findViewById<EditText>(R.id.in_last_name).text.toString()
            val inRole = findViewById<EditText>(R.id.in_role).text.toString()

            if (TextUtils.isEmpty(inFirstName) or TextUtils.isEmpty(inLastName) or TextUtils.isEmpty(inRole)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            } else {
                replyIntent.putExtra(FIRST_NAME, inFirstName)
                replyIntent.putExtra(LAST_NAME, inLastName)
                replyIntent.putExtra(ROLE, inRole)
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object {
        const val FIRST_NAME = "com.egg.roomdb.FIRST_NAME"
        const val LAST_NAME = "com.egg.roomdb.LAST_NAME"
        const val ROLE = "com.egg.roomdb.ROLE"
    }
}

