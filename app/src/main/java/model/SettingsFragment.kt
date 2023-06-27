package model

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.soundme.R
import viewModel.MainViewModel

class SettingsFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val themeButton: Button = view.findViewById(R.id.themeButton)
        themeButton.setOnClickListener {
            showThemeSelectionDialog()
        }

        return view
    }

    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Светлая тема", "Тёмная тема", "Тема ОС")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Выберите тему")
        builder.setItems(themes) { _, which ->
            val selectedTheme = themes[which]
            Toast.makeText(requireContext(), "Выбрана тема: $selectedTheme", Toast.LENGTH_SHORT).show()

            when (selectedTheme) {
                "Светлая тема" -> {
                    requireActivity().setTheme(R.style.Theme_SoundMe)
                }
                "Тёмная тема" -> {
                    requireActivity().setTheme(R.style.Theme_SoundMe)
                }
                "Тема ОС" -> {
                    // Не требуется явно устанавливать тему, так как используется тема ОС
                }
            }

            requireActivity().recreate()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.setCurrentFragment(null)
    }
}
