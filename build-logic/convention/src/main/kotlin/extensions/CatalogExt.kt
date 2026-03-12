package extensions

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
  get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun DependencyHandlerScope.implementation(dependency: Any) = add("implementation", dependency)

internal fun DependencyHandlerScope.coreLibraryDesugaring(dependency: Any) = add("coreLibraryDesugaring", dependency)

internal fun DependencyHandlerScope.ksp(dependency: Any) = add("ksp", dependency)

internal fun DependencyHandlerScope.kapt(dependency: Any) = add("kapt", dependency)

internal fun DependencyHandlerScope.api(dependency: Any) = add("api", dependency)
