package me.surge.common.packet

import org.json.JSONObject

class KeepAlivePacket(json: JSONObject) : Packet("keep-alive", json) {

    constructor() : this(JSONObject())

}