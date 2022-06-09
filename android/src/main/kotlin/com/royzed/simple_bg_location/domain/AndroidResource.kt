package com.royzed.simple_bg_location.domain

data class AndroidResource(val name: String = "", val defType: String = "") {

    val isEmpty: Boolean
    get() = name.isEmpty()

    fun toMap(): Map<String, Any> {
        val map: MutableMap<String, Any> = mutableMapOf()
        map["name"] = name
        map["defType"] = defType

        return map.toMap()
    }

    companion object {

        @JvmStatic
        fun fromMap(arguments: Map<String, Any?>?): AndroidResource {
            if (arguments == null)
                return AndroidResource()
            return AndroidResource(arguments["name"] as? String ?:"", arguments["defType"] as? String ?:"")
        }

        val defaultAppIcon = AndroidResource("mipmap/ic_launcher")
    }
}