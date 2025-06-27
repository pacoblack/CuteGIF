package me.tasy5kg.cutegif.wxapi

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.databinding.ActivityWxEntryBinding
import me.tasy5kg.cutegif.model.MyConstants.WX_LOGIN_CODE
import me.tasy5kg.cutegif.model.MySettings

class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
  private val binding by lazy { ActivityWxEntryBinding.inflate(layoutInflater) }
  private var api: IWXAPI? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID, true)
    api?.handleIntent(intent, this)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    api?.handleIntent(intent, this)
  }

  override fun onReq(baseReq: BaseReq) {
    // 微信发送的请求，如要求用户授权等
  }

  override fun onResp(baseResp: BaseResp) {
    when (baseResp.errCode) {
      BaseResp.ErrCode.ERR_OK ->                 // 用户同意
        if (baseResp is SendAuth.Resp) {
          // 登录成功
          val code = baseResp.code
          Toast.makeText(this, "登录成功，code: $code", Toast.LENGTH_LONG).show()
          MySettings.setString(WX_LOGIN_CODE, code)
        } else {
          // 分享成功
          Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show()
        }

      BaseResp.ErrCode.ERR_USER_CANCEL ->                 // 用户取消
        Toast.makeText(this, "用户取消", Toast.LENGTH_SHORT).show()

      BaseResp.ErrCode.ERR_AUTH_DENIED ->                 // 用户拒绝授权
        Toast.makeText(this, "用户拒绝授权", Toast.LENGTH_SHORT).show()

      else -> Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show()
    }
    finish()
  }
}