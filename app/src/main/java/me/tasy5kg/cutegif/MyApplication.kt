package me.tasy5kg.cutegif

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import me.tasy5kg.cutegif.model.MyConstants

class MyApplication : Application() {
  override fun onCreate() {
    super.onCreate()
    appContext = applicationContext
    WXAPIFactory.createWXAPI(this, MyConstants.WECHAT_APP_ID, true)?.registerApp(MyConstants.WECHAT_APP_ID)
  }

  companion object {
    @SuppressLint("StaticFieldLeak")
    lateinit var appContext: Context
  }
}
