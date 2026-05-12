package com.vmh.mvvmjetpackcompose.ui.widget.navigation

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat
import androidx.navigation.NavType
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

inline fun <reified T : Parcelable> moshiNavTypeOf(moshi: Moshi): MoshiNavType<T> = MoshiNavType(moshi, T::class)

class MoshiNavType<T : Parcelable>(moshi: Moshi, private val kClass: KClass<T>) :
  NavType<T>(isNullableAllowed = false) {

  @OptIn(ExperimentalStdlibApi::class)
  private val adapter = moshi.adapter<T>(kClass.createType())

  override fun get(bundle: Bundle, key: String): T = checkNotNull(BundleCompat.getParcelable(bundle, key, kClass.java))

  override fun parseValue(value: String): T = checkNotNull(
    adapter.fromJson(Uri.decode(value)!!),
  ) { "Failed to parse $value to $kClass" }

  override fun put(bundle: Bundle, key: String, value: T) = bundle.putParcelable(key, value)

  internal fun encodeToString(args: T): String = Uri.encode(
    checkNotNull(adapter.toJson(args)) { "Failed to encode $args to string" },
  )!!
}
