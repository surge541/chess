package me.surge.auth

import me.surge.common.auth.Account
import me.surge.common.packet.LoginPacket
import me.surge.common.packet.RegisterPacket
import java.util.UUID

object AuthorisationHandler {

    // TODO: replace with database
    val accounts = mutableMapOf<String, Account>()

    fun registerAccount(username: String, email: String, password: String): Pair<RegisterPacket.RegistrationStatus, Account?> {
        if (accounts.containsKey(email)) {
            return RegisterPacket.RegistrationStatus.ACCOUNT_ALREADY_EXISTS to null
        }

        if (accounts.any { (_, account) -> account.username == username }) {
            return RegisterPacket.RegistrationStatus.USERNAME_ALREADY_EXISTS to null
        }

        accounts[email] = Account(accounts.size, email, username, password)

        return RegisterPacket.RegistrationStatus.SUCCESS to accounts[email]
    }

    /**
     * Attempts to login to an account
     *
     * @param email or the username
     * @param password of the account
     * @return status, bound to a nullable account
     */
    fun login(email: String, password: String): Pair<LoginPacket.LoginStatus, Account?> {
        if (!accounts.containsKey(email)) {
            if (accounts.any { (_, account) -> account.username == email }) {
                val account = accounts.values.first { account -> account.username == email }

                if (account.password != password) {
                    return LoginPacket.LoginStatus.INCORRECT_PASSWORD to null
                }

                return LoginPacket.LoginStatus.SUCCESS to account
            }

            return LoginPacket.LoginStatus.ACCOUNT_DOESNT_EXIST to null
        }

        val account = accounts[email]!!

        if (account.password != password) {
            return LoginPacket.LoginStatus.INCORRECT_PASSWORD to null
        }

        return LoginPacket.LoginStatus.SUCCESS to account
    }

    fun fetch(id: Int): Account? {
        return accounts.values.firstOrNull { value -> value.id == id }
    }

}