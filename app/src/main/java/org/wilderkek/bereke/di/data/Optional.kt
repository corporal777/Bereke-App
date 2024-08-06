package org.wilderkek.bereke.di.data

data class Optional<out T>(
    val value: T? = null
)

fun <T> T?.asOptional() = Optional(this)