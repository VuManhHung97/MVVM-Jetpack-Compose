package com.vmh.mvvmjetpackcompose.core.data.repository

import com.vmh.mvvmjetpackcompose.core.domain.repository.ForceUpdateErrorEventRepository
import com.vmh.mvvmjetpackcompose.core.network.remote.datasource.ForceUpdateErrorEventDataSource
import javax.inject.Inject

internal class DefaultForceUpdateErrorEventRepository @Inject constructor(
  forceUpdateErrorEventDataSource: ForceUpdateErrorEventDataSource,
) : ForceUpdateErrorEventRepository {
  override val events = forceUpdateErrorEventDataSource.events
}
