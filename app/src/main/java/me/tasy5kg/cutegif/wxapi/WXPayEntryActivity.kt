package me.tasy5kg.cutegif.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mm.opensdk.constants.ConstantsAPI
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import me.tasy5kg.cutegif.BuildConfig
import me.tasy5kg.cutegif.databinding.ActivityWxPayEntryBinding

class WXPayEntryActivity : AppCompatActivity(), IWXAPIEventHandler {
  private val binding by lazy { ActivityWxPayEntryBinding.inflate(layoutInflater) }
  private var api: IWXAPI? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    api = WXAPIFactory.createWXAPI(this, BuildConfig.WECHAT_APP_ID)
    api?.handleIntent(intent, this)
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    api?.handleIntent(intent, this)
  }

  override fun onReq(baseReq: BaseReq) {
    // 微信发送的请求
  }

  override fun onResp(baseResp: BaseResp) {
    if (baseResp.type == ConstantsAPI.COMMAND_PAY_BY_WX) {
      when (baseResp.errCode) {
        BaseResp.ErrCode.ERR_OK -> Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show()
        BaseResp.ErrCode.ERR_USER_CANCEL -> Toast.makeText(this, "支付取消", Toast.LENGTH_SHORT).show()
        else -> Toast.makeText(this, "支付失败: " + baseResp.errCode, Toast.LENGTH_SHORT).show()
      }
      finish()
    }
  }
}