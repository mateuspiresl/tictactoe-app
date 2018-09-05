package me.mateuspires.tictactoe.ui.customizer.presenter

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.data.network.ImageSearchApi
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract

class CustomizerPresenter(
        private val view: CustomizerContract.View,
        private val isX: Boolean,
        private val imageSearch: ImageSearchApi
): CustomizerContract.Presenter {

    var selectedImage: ImageSearch.Item? = null
    var lastRequestId = 0

    override fun search(query: String): Single<ImageSearch.Result> {
        val requestId = lastRequestId + 1
        lastRequestId = requestId

        return Single.create<ImageSearch.Result> { emitter ->
            val result = imageSearch.search(query).execute()
            result.body()?.let {
                if (requestId == lastRequestId) {
                    emitter.onSuccess(it)
                } else {
                    emitter.onError(Error("Response too late"))
                }
            }
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
    }

    override fun selectImage(image: ImageSearch.Item) {
        selectedImage = image
        view.onShowImage(image)
    }

    override fun unselect() {
        selectedImage = null
        view.onHide()
    }

    override fun confirm() {

    }
}
