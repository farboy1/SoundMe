package viewModel

import android.content.Context
import com.example.soundme.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import model.AuthRepositoryImpl
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soundme.extensions.default
import com.example.soundme.extensions.set
import android.content.SharedPreferences
import androidx.core.content.edit

sealed class LoginState {
    class DefaultState: LoginState()
    class SendingState: LoginState()
    class LoginSucceededState: LoginState()
    class ErrorState<T>(val message: T): LoginState()
}

class AuthViewModel : ViewModel() {

    private var authRepository = AuthRepositoryImpl()
    val state: MutableLiveData<LoginState> = MutableLiveData<LoginState>().default(initialValue = LoginState.DefaultState())
    val loginSuccess: MutableLiveData<Boolean> = MutableLiveData()

    private lateinit var sharedPreferences: SharedPreferences
    private val KEY_LOGIN_STATUS = "login_status"

    fun login(email: String, password: String) {
        if (!validateEmail(email)) {
            state.value = LoginState.ErrorState(message = R.string.error_email_invalid.toString())
            return
        }

        if (!validatePassword(password = password)) {
            state.value = LoginState.ErrorState(message = R.string.error_password_invalid.toString()) // Добавлено: Проверка и обработка недопустимого пароля
            return
        }

        state.set(newValue = LoginState.SendingState() )
        CoroutineScope(Dispatchers.IO).async {
            val errorMessage = authRepository.login(email = email, password = password).await()
            if (errorMessage.isEmpty()) {
                launch(Dispatchers.Main) {
                    state.set(newValue = LoginState.LoginSucceededState())
                    loginSuccess.value = true
                }
            } else
                launch(Dispatchers.Main) {
                    state.set(newValue = LoginState.ErrorState(message = errorMessage))
                }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    private fun validatePassword(password: String): Boolean {
        return password.length > 8
    }

    fun initializeSharedPreferences(context: Context) {
        sharedPreferences = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE)
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_LOGIN_STATUS, isLoggedIn)
        }
    }

    fun getLoginStatus(): Boolean {
        return sharedPreferences.getBoolean(KEY_LOGIN_STATUS, false)
    }
}