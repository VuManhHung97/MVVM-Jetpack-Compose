package com.vmh.mvvmjetpackcompose.core.local.mapper

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

fun Instant.toTimestamp(): Timestamp = Timestamp.newBuilder()
  .setSeconds(this.epochSecond)
  .setNanos(this.nano)
  .build()

fun LocalDate.toTimestamp(zoneId: ZoneId = ZoneOffset.UTC): Timestamp = this.atStartOfDay(zoneId)
  .toInstant()
  .toTimestamp()

fun Timestamp.toLocalDate(zoneId: ZoneId = ZoneOffset.UTC): LocalDate =
  Instant.ofEpochSecond(this.seconds, this.nanos.toLong())
    .atZone(zoneId)
    .toLocalDate()
