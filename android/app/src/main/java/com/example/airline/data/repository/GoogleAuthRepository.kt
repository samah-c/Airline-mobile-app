package com.example.airline.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

class GoogleAuthRepository {

    suspend fun getGoogleIdToken(context: Context): Result<String> {
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)  // Affiche tous les comptes
                .setServerClientId("486909540404-8du9ojsngn05f9cohb1ns8bj9urhojm6.apps.googleusercontent.com")
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            val credential = result.credential

            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            Result.success(googleIdTokenCredential.idToken)

        } catch (e: GetCredentialException) {
            Result.failure(Exception("Google Sign-In annulé ou échoué: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}