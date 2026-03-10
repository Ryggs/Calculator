package com.ryggs.kmpcalc

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform