package com.royzed.simple_bg_location.domain

import com.royzed.simple_bg_location.data.Position
import io.flutter.Log

class State(
) {
    var isTracking: Boolean = false
    var requestOptions: RequestOptions? = null
    var positions: List<Position>? = null

    fun toMap(): Map<String, Any?> {
        val state: MutableMap<String, Any?> = mutableMapOf()
        state["isTracking"] = isTracking
        state["requestOptions"] = requestOptions?.toMap()
        state["positions"] = positions?.map { it.toMap() }

        val retmap =  state.toMap()
        Log.d("State","$retmap")
        return retmap
    }

    override fun toString(): String {
        return "State(isTracking: $isTracking, requestOptions: $requestOptions, positions size: ${positions?.size})"
    }
}