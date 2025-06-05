package com.solaisc.notemark.util

sealed class Orientation {
    data object Phone_Portrait: Orientation()
    data object Landscape: Orientation()
    data object Tablet_Portrait: Orientation()
}