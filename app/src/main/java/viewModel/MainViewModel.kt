package viewModel

import android.app.Application
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _resultText = MutableLiveData<String>()
    val resultText: LiveData<String> get() = _resultText

    private val _volumeText = MutableLiveData<String>()
    val volumeText: LiveData<String> get() = _volumeText

    val currentFragment: MutableLiveData<Class<out Fragment>?> = MutableLiveData()

    // Метод для обновления текущего фрагмента
    fun setCurrentFragment(fragmentClass: Class<out Fragment>?) {
        currentFragment.value = fragmentClass
    }

}


