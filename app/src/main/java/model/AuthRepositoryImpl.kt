package model

import com.example.soundme.AuthRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AuthRepositoryImpl: AuthRepository {
    override suspend fun login(email: String, password: String): Deferred<String> {
        return GlobalScope.async {
            Thread.sleep(3000)
            ""
        }
    }

}