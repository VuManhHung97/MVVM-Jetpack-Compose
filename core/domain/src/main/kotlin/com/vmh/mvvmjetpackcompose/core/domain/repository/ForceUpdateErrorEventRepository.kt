package com.vmh.mvvmjetpackcompose.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface ForceUpdateErrorEventRepository {
  val events: Flow<Unit>
}
