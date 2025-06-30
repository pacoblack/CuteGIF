package me.tasy5kg.cutegif.fragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
  private val fragmentData: MutableLiveData<HashMap<Int, String>> = MutableLiveData(HashMap())

  fun setPositionData(position:Int, data: String) {
    fragmentData.value.set(position, data)
  }

  fun getPositionData(): MutableLiveData<HashMap<Int, String>> {
    return fragmentData
  }
}