package com.example.soundme.extensions

import androidx.lifecycle.MutableLiveData
// исключение взаимопересекающихся состояний при изменениях в приложении

// set default value for any type of MutableLiveData
fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }
// set new value for any type of MutableLiveData
fun <T> MutableLiveData<T>.set(newValue: T) = apply { setValue(newValue) }
