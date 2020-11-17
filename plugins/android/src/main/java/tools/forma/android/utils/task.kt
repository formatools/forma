package tools.forma.android.utils

import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskProvider
import kotlin.reflect.KClass

fun <T : Task> TaskContainer.register(name: String, type: KClass<T>, configurationAction: Action<in T>): TaskProvider<T> =
        register(name, type.java, configurationAction)
