package me.tasy5kg.cutegif.toolbox

interface Callback<P,V> {
  @Throws(Exception::class)
  fun call(p: P?): V?
}