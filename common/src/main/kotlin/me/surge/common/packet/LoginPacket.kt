package me.surge.common.packet

import me.surge.common.auth.PublicAccountDetails
import org.json.JSONObject

class LoginPacket(json: JSONObject) : Packet("login", json) {

    val email: String = json.getString("email")
    val password: String = json.getString("password")

    constructor(email: String, password: String) : this(JSONObject(mapOf(
        "email" to email,
        "password" to password
    )))

    class LoginResponsePacket(json: JSONObject) : Packet("login-response", json) {

        val response: LoginStatus = json.getEnum(LoginStatus::class.java, "response")
        val accountDetails = PublicAccountDetails.extract("account-details", json)

        constructor(response: LoginStatus, accountDetails: PublicAccountDetails?) : this(JSONObject(mapOf(
            "response" to response,
            "account-details" to if (accountDetails != null) PublicAccountDetails.embed(accountDetails) else null
        )))

    }

    enum class LoginStatus {
        /**
         * Login was fully successful
         */
        SUCCESS,

        /**
         * Login was unsuccessful, as the account seemingly doesn't exist
         */
        ACCOUNT_DOESNT_EXIST,

        /**
         * Login was unsuccessful, as the user inputted the wrong password
         */
        INCORRECT_PASSWORD
    }

}