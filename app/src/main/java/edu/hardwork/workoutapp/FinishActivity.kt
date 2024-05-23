package edu.hardwork.workoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import edu.hardwork.workoutapp.databinding.ActivityFinishBinding

class FinishActivity : AppCompatActivity() {
    private  var binding:ActivityFinishBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar(binding?.toolbarFinish)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }


        binding?.btnFinish?.setOnClickListener{
            val intent :Intent = Intent(this@FinishActivity,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}