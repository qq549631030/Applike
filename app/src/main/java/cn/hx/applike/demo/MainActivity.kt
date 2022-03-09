package cn.hx.applike.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.hx.applike.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.text = (application as? AppApplication)?.str
    }
}