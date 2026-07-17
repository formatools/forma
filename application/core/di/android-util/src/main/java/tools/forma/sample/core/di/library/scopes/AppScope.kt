package tools.forma.sample.core.di.library.scopes

import javax.inject.Scope

/**
 * Scope for the entire app runtime.
 */
@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class AppScope
