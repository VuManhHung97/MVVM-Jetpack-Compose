package com.vmh.mvvmjetpackcompose.core.common.extension

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentMapOf

//region buildPersistentMap
@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
inline fun <K, T> buildPersistentMap(
  @BuilderInference builderAction: MutableMap<K, T>.() -> Unit,
): PersistentMap<K, T> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }

  return persistentMapOf<K, T>().mutate(builderAction)
}
//endregion
