package me.surge.common.packet

import org.json.JSONObject

interface IEmbeddable<T> {

    fun embed(obj: T): JSONObject
    fun extract(key: String?, json: JSONObject): T?

}