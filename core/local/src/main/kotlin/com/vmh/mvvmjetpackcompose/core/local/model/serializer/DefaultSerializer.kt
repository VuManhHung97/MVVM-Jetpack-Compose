package com.vmh.mvvmjetpackcompose.core.local.model.serializer

import androidx.annotation.AnyThread
import androidx.collection.ArrayMap
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.GeneratedMessageLite
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Method
import javax.annotation.concurrent.GuardedBy
import kotlin.collections.getOrPut
import kotlin.jvm.java

private typealias SomeGeneratedMessageLiteClass = Class<out GeneratedMessageLite<*, *>>

private object CacheHolder {
  @GuardedBy("this")
  @JvmField
  val parseFromMethodCache = ArrayMap<SomeGeneratedMessageLiteClass, Method>()

  @GuardedBy("this")
  @JvmField
  val getDefaultInstanceMethodCache = ArrayMap<SomeGeneratedMessageLiteClass, Method>()
}

@AnyThread
private fun <T : GeneratedMessageLite<T, *>> parseFrom(clazz: Class<T>, inputStream: InputStream): T {
  val method = synchronized(CacheHolder) {
    CacheHolder.parseFromMethodCache.getOrPut(clazz) {
      clazz.getMethod("parseFrom", InputStream::class.java)
    }
  }

  try {
    @Suppress("UNCHECKED_CAST")
    return method.invoke(null, inputStream) as T
  } catch (exception: InvalidProtocolBufferException) {
    throw CorruptionException("Cannot read proto.", exception)
  }
}

@AnyThread
private fun <T : GeneratedMessageLite<T, *>> getDefaultInstance(clazz: Class<T>): T {
  val method = synchronized(CacheHolder) {
    CacheHolder.getDefaultInstanceMethodCache.getOrPut(clazz) {
      clazz.getMethod("getDefaultInstance")
    }
  }
  @Suppress("UNCHECKED_CAST")
  return method.invoke(null) as T
}

@AnyThread
class DefaultSerializer<T : GeneratedMessageLite<T, *>>(private val clazz: Class<T>) : Serializer<T> {
  override val defaultValue: T get() = getDefaultInstance(clazz)

  override suspend fun readFrom(input: InputStream): T = parseFrom(clazz, input)

  @Suppress("BlockingMethodInNonBlockingContext")
  override suspend fun writeTo(t: T, output: OutputStream) = t.writeTo(output)
}

@AnyThread
inline fun <reified T : GeneratedMessageLite<T, *>> getDefaultSerializer(): Serializer<T> =
  DefaultSerializer(T::class.java)
