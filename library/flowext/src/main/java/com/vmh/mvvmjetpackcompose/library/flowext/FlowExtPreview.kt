@file:Suppress("ktlint:standard:max-line-length", "MaxLineLength")

package com.vmh.mvvmjetpackcompose.library.flowext

/**
 * Marks `FlowExt`-related API as a feature preview.
 *
 * FlowExt preview has **no** backward compatibility guarantees, including both binary and source compatibility.
 * Its API and semantics can and will be changed in next releases.
 *
 * Feature preview can be used to evaluate its real-world strengths and weaknesses, gather and provide feedback.
 * According to the feedback, `FlowExt` will be refined on its road to stabilization and promotion to a stable API.
 *
 * The best way to speed up preview feature promotion is providing the feedback on the feature.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(
  level = RequiresOptIn.Level.WARNING,
  message =
  "This declaration is in a preview state and can be changed in a backwards-incompatible manner with a best-effort migration. " +
    "Its usage should be marked with '@com.vmh.mvvmjetpackcompose.library.flowext.DelicateFlowExtApi' or '@OptIn(com.vmh.mvvmjetpackcompose.library.flowext.DelicateFlowExtApi::class)' " +
    "if you accept the drawback of relying on preview API",
)
@Target(
  AnnotationTarget.CLASS,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.TYPEALIAS,
  AnnotationTarget.PROPERTY,
)
public annotation class FlowExtPreview
