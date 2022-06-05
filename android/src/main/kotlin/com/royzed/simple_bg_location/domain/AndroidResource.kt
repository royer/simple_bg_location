package com.royzed.simple_bg_location.domain

class AndroidResource(val name: String = "", val defType: String = "") {

    val isEmpty: Boolean
    get() = name.isEmpty()


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