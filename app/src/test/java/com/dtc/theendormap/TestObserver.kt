package com.dtc.theendormap

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

class TestObserver<T> : Observer<T> {

    // capturer les valeurs
    val observedValues = mutableListOf<T?>()

    override fun onChanged(t: T) {
        observedValues.add(t)
    }
}
// fonction d'extension
fun <T> LiveData<T>.testObserver() = TestObserver<T>().apply {
    observeForever(this)
}