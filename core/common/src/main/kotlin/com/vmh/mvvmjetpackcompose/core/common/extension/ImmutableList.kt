@file:Suppress("TooManyFunctions") // It is ok to suppress TooManyFunctions

package com.vmh.mvvmjetpackcompose.core.common.extension

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/**
 * Marks a type parameter as immutable.
 */
@Target(AnnotationTarget.TYPE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class ImmutableElement

//region buildPersistentList
// TODO(immutable-collections): remove after https://github.com/Kotlin/kotlinx.collections.immutable/pull/166 is merged/.

/**
 * Builds a new [PersistentList] by populating a [MutableList] using the given [builderAction]
 * and returning an immutable list with the same elements.
 *
 * The list passed as a receiver to the [builderAction] is valid only inside that function.
 * Using it outside the function produces an unspecified behavior.
 */
@OptIn(ExperimentalTypeInference::class, ExperimentalContracts::class)
inline fun <T> buildPersistentList(@BuilderInference builderAction: MutableList<T>.() -> Unit): PersistentList<T> {
  contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }

  return persistentListOf<T>().mutate(builderAction)
}
//endregion

//region Iterable<T> -> PersistentList<R>
// TODO(immutable-collections): remove after https://github.com/Kotlin/kotlinx.collections.immutable/issues/123 is resolved.
inline fun <T, R> Iterable<T>.mapToPersistentList(transform: (T) -> R): PersistentList<R> = buildPersistentList {
  for (element in this@mapToPersistentList) {
    add(transform(element))
  }
}

// TODO(immutable-collections): remove after https://github.com/Kotlin/kotlinx.collections.immutable/issues/123 is resolved.
inline fun <T, R : Any> Iterable<T>.mapNotNullToPersistentList(transform: (T) -> R?): PersistentList<R> =
  buildPersistentList {
    for (element in this@mapNotNullToPersistentList) {
      transform(element)?.let(::add)
    }
  }

// TODO(immutable-collections): remove after https://github.com/Kotlin/kotlinx.collections.immutable/issues/123 is resolved.
inline fun <T : Any> Iterable<T>.filterToPersistentList(predicate: (T) -> Boolean): PersistentList<T> =
  buildPersistentList {
    for (element in this@filterToPersistentList) if (predicate(element)) add(element)
  }

inline fun <T, R> Iterable<T>.mapIndexedToPersistentList(transform: (index: Int, element: T) -> R): PersistentList<R> =
  buildPersistentList {
    for ((index, element) in this@mapIndexedToPersistentList.withIndex()) {
      add(transform(index, element))
    }
  }
//endregion

//region PersistentList<@ImmutableElement T> -> PersistentList<T>
// TODO(immutable-collections): remove after https://github.com/Kotlin/kotlinx.collections.immutable/issues/123 is resolved.
@Deprecated(
  message = "Consider using PersistentList.mapConditionallyToPersistentList",
  level = DeprecationLevel.WARNING,
)
inline fun <@ImmutableElement T> PersistentList<T>.mapToPersistentList(transform: (T) -> T): PersistentList<T> =
  mutate { builder ->
    for (i in builder.indices) {
      val element = builder[i]
      val updated = transform(element)
      if (updated != element) {
        builder[i] = updated
      }
    }
  }

inline fun <@ImmutableElement T> PersistentList<T>.mapIndexedToPersistentList(
  transform: (index: Int, element: T) -> T,
): PersistentList<T> = mutate { builder ->
  for (i in builder.indices) {
    val element = builder[i]
    val updated = transform(i, element)
    if (updated != element) {
      builder[i] = updated
    }
  }
}

@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated(
  message = "Consider using PersistentList.mapToPersistentList",
  level = DeprecationLevel.WARNING,
)
inline fun <@ImmutableElement T> ImmutableList<T>.mapToPersistentList(transform: (T) -> T): PersistentList<T> =
  toPersistentList().mapToPersistentList(transform)
//endregion

//region PersistentList<@ImmutableElement T>.mapConditionallyToPersistentList
inline fun <@ImmutableElement T : Any> PersistentList<T>.mapConditionallyToPersistentList(
  predicate: (element: T) -> Boolean,
  transform: (element: T) -> T,
): PersistentList<T> = mutate { builder ->
  for (i in builder.indices) {
    val element = builder[i]

    if (predicate(element)) {
      val updated = transform(element)
      if (updated != element) {
        builder[i] = updated
      }
    }
  }
}
//endregion

//region PersistentList<T>.filterDuplicatesAndAddAll
inline fun <T, K> PersistentList<T>.filterDuplicatesAndAddAll(
  items: List<T>,
  keySelector: (T) -> K,
): PersistentList<T> {
  val existingKeys = mapTo(HashSet(), keySelector)
  val uniqueNewItems = items.filterNot { keySelector(it) in existingKeys }
  return addAll(uniqueNewItems)
}
//endregion

//region PersistentList<T>.replaceDuplicatesAndAddAll

/**
 * Upserts elements by [keySelector]:
 * - For each existing element, if a new element with the same key exists, it replaces it **in place** (order preserved).
 * - Elements in [items] whose keys don’t exist yet are **appended** at the end, in the order of their first appearance.
 *
 * Notes:
 * - If [items] contains duplicate keys, the **last** value wins for that key, but the append order is defined by the **first** occurrence.
 * - Time: O(N + M), Space: O(N + M), where N = size of this list, M = size of [items].
 */
inline fun <T, K> PersistentList<T>.upsertByKey(
  items: Iterable<T>,
  crossinline keySelector: (T) -> K,
): PersistentList<T> {
  // LinkedHashMap: preserves insertion order of the *first* time a key appears.
  val newItemsByKey = items.associateByTo(LinkedHashMap(), keySelector)

  return buildPersistentList {
    for (old in this@upsertByKey) {
      val k = keySelector(old)
      // If we have a replacement for this key, use it and remove from map
      add(newItemsByKey.remove(k) ?: old)
    }
    // Whatever remains are new keys that didn't exist before → append in insertion order
    addAll(newItemsByKey.values)
  }
}
//endregion
