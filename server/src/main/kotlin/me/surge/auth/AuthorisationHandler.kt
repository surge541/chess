package me.surge.auth

import me.surge.common.auth.Account
import me.surge.common.auth.PublicAccountDetails
import me.surge.common.chess.ChessGame
import me.surge.common.chess.Side
import me.surge.common.packet.LoginPacket
import me.surge.common.packet.RegisterPacket

object AuthorisationHandler {

    // TODO: replace with database
    private val accounts = mutableListOf<Account>()

    /**
     * Attempts account registration
     *
     * @param username [String] to be used as a sign-in option
     * @param email [String] that will be authenticated with, and also used as an additional sign-in option
     * @param password [String] that will be used to authenticate logins
     * @return [Pair] of a [RegisterPacket.RegistrationStatus] relaying the registration result, and, if successful, the [Account] object
     */
    fun register(username: String, email: String, password: String): Pair<RegisterPacket.RegistrationStatus, PublicAccountDetails?> {
        // account with provided email already exists
        if (fetch(email, Fetch.EMAIL) != null) {
            return RegisterPacket.RegistrationStatus.EMAIL_ALREADY_EXISTS to null
        }

        // account with provided username already exists
        if (fetch(username, Fetch.USERNAME) != null) {
            return RegisterPacket.RegistrationStatus.USERNAME_ALREADY_EXISTS to null
        }

        return RegisterPacket.RegistrationStatus.SUCCESS to
               Account(accounts.size, email, username, password).also {
                   accounts.add(it)
               }.public
    }

    /**
     * Attempts to login to an account
     *
     * @param qualifier or the username
     * @param password of the account
     * @return [LoginPacket.LoginStatus], bound to the found [Account] if applicable
     */
    fun login(qualifier: String, password: String): Pair<LoginPacket.LoginStatus, PublicAccountDetails?> {
        val account = fetch(qualifier) ?: return LoginPacket.LoginStatus.ACCOUNT_DOESNT_EXIST to null

        if (account.password != password) {
            return LoginPacket.LoginStatus.INCORRECT_PASSWORD to null
        }

        return LoginPacket.LoginStatus.SUCCESS to account.public
    }

    /**
     * Updates a [game] for an account with the given [accountId]
     *
     * @param accountId the account to update the game of
     * @param game the new game obj
     */
    fun updateGame(accountId: Int, game: ChessGame?) {
        accounts.first { it.id == accountId }.also {
            it.game = game

            if (game == null) {
                it.side = null
            } else {
                it.side = if (game.white.id == accountId) Side.WHITE else Side.BLACK
            }
        }
    }

    /**
     * Attempts to fetch an account from the database // TODO: Database
     *
     * @param qualifier [String] to compare to the email / username of accounts
     * @param fetch [Fetch] mode
     * @return [Account] instance, or null depending on whether it was found
     */
    private fun fetch(qualifier: String, fetch: Fetch = Fetch.EITHER): Account? {
        return accounts.firstOrNull {
            when (fetch) {
                Fetch.EMAIL -> it.email == qualifier
                Fetch.USERNAME -> it.username == qualifier
                else -> it.email == qualifier || it.username == qualifier
            }
        }
    }

    /**
     * Fetches an account based on its ID
     *
     * @param id of the account
     * @return [PublicAccountDetails] instance, or null depending on whether it was found
     */
    fun fetchId(id: Int): PublicAccountDetails? {
        return accounts.firstOrNull { it.id == id }?.public
    }

    enum class Fetch {
        /**
         * Filter accounts only through email
         */
        EMAIL,

        /**
         * Filter accounts only through username
         */
        USERNAME,

        /**
         * Filter accounts by whether the email or username is equal
         */
        EITHER
    }

}