package com.vmh.mvvmjetpackcompose.core.local.extention

import com.google.protobuf.StringValue

fun String?.toProtoStringValue(): StringValue =
  if (this != null) StringValue.of(this) else StringValue.getDefaultInstance()
