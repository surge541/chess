package me.surge.common.packet

import me.surge.common.auth.Account
import org.json.JSONObject

class LoginPacket(json: JSONObject) : Packet("login", json) {

    val email: String = json.getString("email")
    val password: String = json.getString("password")

    constructor(email: String, password: String) : this(JSONObject(mapOf(
        "email" to email,
        "password" to password
    )))

    class LoginResponsePacket(json: JSONObject) : Packet("login-response", json) {

        val response = json.getEnum(LoginStatus::class.java, "response")
        val account = Account.extract("account", json)

        constructor(response: LoginStatus, account: Account?) : this(JSONObject(mapOf(
            "response" to response,
            "account" to if (account != null) Account.embed(account) else null
        )))

    }

    enum class LoginStatus {
        SUCCESS,
        ACCOUNT_DOESNT_EXIST,
        INCORRECT_PASSWORD
    }

}