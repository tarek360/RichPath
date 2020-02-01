package com.richpathanimator

sealed class RepeatMode(val value: Int) {
    object None : RepeatMode(-2)
    object Restart : RepeatMode(1)
    object Reverse : RepeatMode(2)
}
