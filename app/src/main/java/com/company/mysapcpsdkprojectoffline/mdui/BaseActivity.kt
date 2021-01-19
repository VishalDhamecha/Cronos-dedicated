package com.company.mysapcpsdkprojectoffline.mdui

import android.Manifest
import android.annotation.SuppressLint
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity() {

    private var connectivityDisposable: Disposable? = null
    private val TAG = "BaseActivity"
    private var mutableNetworkLiveData: MutableLiveData<Boolean> = MutableLiveData()


    fun isNetworkConnected(): LiveData<Boolean> {
        return mutableNetworkLiveData
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility =
                    view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }

        setContentView(getLayoutResourceId())
        connectivityDisposable = ReactiveNetwork.observeNetworkConnectivity(applicationContext)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { connectivity ->
                Log.d(TAG, connectivity.toString())
                if (connectivity.state() == NetworkInfo.State.CONNECTED) {
                    mutableNetworkLiveData.postValue(true)
                } else {
                    mutableNetworkLiveData.postValue(false)
                }

            }



        main()
    }

    protected abstract fun getLayoutResourceId(): Int

    protected abstract fun main()


    override fun onDestroy() {
        super.onDestroy()
        safelyDispose(connectivityDisposable)

    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }




}
