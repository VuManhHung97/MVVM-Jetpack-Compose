package com.vmh.mvvmjetpackcompose.core.ui.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import com.vmh.mvvmjetpackcompose.library.flowext.ThrottleConfiguration
import com.vmh.mvvmjetpackcompose.library.flowext.throttleTime
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

// Using composed for modifiers is not recommended anymore, due to the performance issues it creates.
// You should consider migrating this modifier to be based on Modifier.Node instead.
@Suppress("ModifierComposed")
fun Modifier.throttledClickable(
  enabled: Boolean = true,
  onClickLabel: String? = null,
  role: Role? = null,
  onClick: () -> Unit,
) = composed(
  inspectorInfo = debugInspectorInfo {
    name = "clickable"
    properties["enabled"] = enabled
    properties["onClickLabel"] = onClickLabel
    properties["role"] = role
    properties["onClick"] = onClick
  },
) {
  DebouncedClickable // early access...

  Modifier.clickable(
    enabled = enabled,
    onClickLabel = onClickLabel,
    onClick = { DebouncedClickable.onClick(onClick) },
    role = role,
    indication = LocalIndication.current,
    interactionSource = remember { MutableInteractionSource() },
  )
}

@OptIn(ExperimentalCoroutinesApi::class)
object DebouncedClickable {
  private val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

  private val onClickFlow = MutableSharedFlow<() -> Unit>(
    extraBufferCapacity = 1,
    replay = 0,
  )

  init {
    scope.launch {
      onClickFlow
        .throttleTime(
          duration = 700.milliseconds,
          throttleConfiguration = ThrottleConfiguration.LEADING,
        )
        .collect { it() }
    }
  }

  fun onClick(onClick: () -> Unit) {
    scope.launch { onClickFlow.emit(onClick) }
  }
}
