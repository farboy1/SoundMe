package model

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.soundme.MainActivity
import com.example.soundme.R
import viewModel.AuthViewModel
import viewModel.LoginState

class LoginFragment : Fragment() {

    private lateinit var textEmail: EditText
    private lateinit var textPassword: EditText
    private lateinit var btnLogin: Button
    lateinit var viewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        textEmail = view.findViewById(R.id.textEmail)
        textPassword = view.findViewById(R.id.textPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        viewModel.initializeSharedPreferences(requireContext())
        return view
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)

        btnLogin.setOnClickListener {
            val email = textEmail.text.toString()
            val password = textPassword.text.toString()
            viewModel.login(email, password)

        }
        val isLoggedIn = viewModel.getLoginStatus()
        if (isLoggedIn) {
            (activity as MainActivity).navController.navigate(R.id.action_loginFragment_to_mainFragment)
            return
        }

        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is LoginState.LoginSucceededState -> {
                    Toast.makeText(requireContext(), "Success login", Toast.LENGTH_SHORT).show()
                    (activity as MainActivity).navController.navigate(R.id.action_loginFragment_to_mainFragment)
                }

                is LoginState.SendingState -> {
                    textEmail.isEnabled = false
                    textPassword.isEnabled = false
                    btnLogin.isEnabled = false
                }

                is LoginState.ErrorState<*> -> {
                    val errorMessage = when (state.message) {
                        is Int -> getString(state.message as Int)
                        is String -> state.message as String
                        else -> "Unknown error"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }

                is LoginState.DefaultState -> {
                    textEmail.isEnabled = true
                    textPassword.isEnabled = true
                    btnLogin.isEnabled = true
                }
            }
        })
    }


}
