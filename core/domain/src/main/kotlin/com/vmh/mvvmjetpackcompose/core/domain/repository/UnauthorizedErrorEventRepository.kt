package com.vmh.mvvmjetpackcompose.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface UnauthorizedErrorEventRepository {
  val events: Flow<Unit>
}
