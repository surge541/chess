package me.surge.common.packet

import me.surge.common.auth.Account
import org.json.JSONObject

class RegisterPacket(json: JSONObject) : Packet("register", json) {

    val username = json.getString("username")
    val email = json.getString("email")
    val password = json.getString("password")

    constructor(username: String, email: String, password: String) : this(JSONObject(mapOf(
        "username" to username,
        "email" to email,
        "password" to password
    )))

    class RegistrationResponsePacket(json: JSONObject) : Packet("register-response", json) {

        val response = json.getEnum(RegistrationStatus::class.java, "response")
        val account = Account.extract("account", json)

        constructor(response: RegistrationStatus, account: Account?) : this(JSONObject(mapOf(
            "response" to response,
            "account" to if (account == null) null else Account.embed(account)
        )))

    }

    enum class RegistrationStatus {
        SUCCESS,
        ACCOUNT_ALREADY_EXISTS,
        USERNAME_ALREADY_EXISTS
    }


}