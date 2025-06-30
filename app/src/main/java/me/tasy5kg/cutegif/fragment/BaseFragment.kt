package me.tasy5kg.cutegif.fragment

import androidx.fragment.app.Fragment
import me.tasy5kg.cutegif.components.OnSelectFragment

open class BaseFragment: Fragment() {
  private var selectFragment: OnSelectFragment? = null
  fun setSelectFragment(select: OnSelectFragment) {
    this.selectFragment = select
  }



}