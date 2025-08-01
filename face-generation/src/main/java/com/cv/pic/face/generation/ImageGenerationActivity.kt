package com.cv.pic.face.generation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cv.pic.face.generation.databinding.ActivityImageGenerationBinding
import com.cv.pic.face.generation.diffusion.DiffusionActivity
import com.cv.pic.face.generation.loraweights.LoRAWeightActivity
import com.cv.pic.face.generation.plugins.PluginActivity

class ImageGenerationActivity : AppCompatActivity() {

  private lateinit var binding: ActivityImageGenerationBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityImageGenerationBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.btnDiffusion.setOnClickListener {
      startActivity(Intent(this, DiffusionActivity::class.java))
    }

    binding.btnPlugins.setOnClickListener {
      startActivity(Intent(this, PluginActivity::class.java))
    }

    binding.btnLoRA.setOnClickListener {
      startActivity(Intent(this, LoRAWeightActivity::class.java))
    }
  }
  companion object{
    fun start(context: Context) {
      context.startActivity(Intent(context, ImageGenerationActivity::class.java))
    }
  }

}