package com.example.mzantsilingo.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mzantsilingo.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // TODO: fetch lessons via ApiService.getLessons() and bind to RecyclerView
    }
}