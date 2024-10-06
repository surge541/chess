package me.surge.common.packet

import me.surge.common.auth.PublicAccountDetails
import org.json.JSONObject

class RegisterPacket(json: JSONObject) : Packet("register", json) {

    val username: String = json.getString("username")
    val email: String = json.getString("email")
    val password: String = json.getString("password")

    constructor(username: String, email: String, password: String) : this(JSONObject(mapOf(
        "username" to username,
        "email" to email,
        "password" to password
    )))

    class RegistrationResponsePacket(json: JSONObject) : Packet("register-response", json) {

        val response: RegistrationStatus = json.getEnum(RegistrationStatus::class.java, "response")
        val accountDetails = PublicAccountDetails.extract("accountDetails", json)

        constructor(response: RegistrationStatus, accountDetails: PublicAccountDetails?) : this(JSONObject(mapOf(
            "response" to response,
            "accountDetails" to if (accountDetails == null) null else PublicAccountDetails.embed(accountDetails)
        )))

    }

    enum class RegistrationStatus {
        SUCCESS,
        EMAIL_ALREADY_EXISTS,
        USERNAME_ALREADY_EXISTS
    }


}