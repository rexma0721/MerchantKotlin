package com.vroomvroom.fooddeliverys

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vroomvroom.fooddeliverys.view.ui.home.HomeActivity
import com.vroomvroom.fooddeliverys.utils.Utils.startNewActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTheme(R.style.Theme_VroomVroom)
        startNewActivity(HomeActivity::class.java)

    }
}