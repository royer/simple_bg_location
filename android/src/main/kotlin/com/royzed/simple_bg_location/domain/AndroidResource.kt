package com.royzed.simple_bg_location.location

class AndroidResource(private val _name: String, private val _defType: String) {

    val name get() = _name
    val defType get() = _defType

    companion object {

        @JvmStatic
        fun parseArguments(arguments: Map<String, String>?): AndroidResource? {
            if (arguments == null)
                return null
            return AndroidResource(arguments["name"]?:"", arguments["defType"]?:"")
        }
    }
}