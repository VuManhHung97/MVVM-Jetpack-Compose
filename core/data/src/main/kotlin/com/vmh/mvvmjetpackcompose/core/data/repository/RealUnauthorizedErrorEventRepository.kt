package com.vmh.mvvmjetpackcompose.core.data.repository

import com.vmh.mvvmjetpackcompose.core.domain.repository.UnauthorizedErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.UnauthorizedErrorEventDataSource
import javax.inject.Inject

internal class RealUnauthorizedErrorEventRepository @Inject constructor(
  unauthorizedErrorEventDataSource: UnauthorizedErrorEventDataSource,
) : UnauthorizedErrorEventRepository {
  override val events = unauthorizedErrorEventDataSource.events
}
