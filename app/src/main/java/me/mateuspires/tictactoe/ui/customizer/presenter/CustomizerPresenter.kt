package me.mateuspires.tictactoe.ui.customizer.presenter

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.data.network.ImageSearchApi
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CustomizerPresenter(
        private val view: CustomizerContract.View,
        private val isX: Boolean,
        private val imageSearch: ImageSearchApi
): CustomizerContract.Presenter {

    private val searchResultsSubject: PublishSubject<ImageSearch.Result> = PublishSubject.create()
    var selectedImage: ImageSearch.Item? = null
    var lastRequestId = 0

    override fun observeSearchResults(): Observable<ImageSearch.Result> {
        return searchResultsSubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun search(query: String) {
        val requestId = lastRequestId + 1
        lastRequestId = requestId

        imageSearch.search(query).enqueue(object: Callback<ImageSearch.Result> {
            override fun onFailure(call: Call<ImageSearch.Result>?, t: Throwable?) {
                t?.let { searchResultsSubject.onError(t) }
            }

            override fun onResponse(call: Call<ImageSearch.Result>?, response: Response<ImageSearch.Result>?) {
                if (response != null) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        if (requestId == lastRequestId) {
                            searchResultsSubject.onNext(body)
                        }
                    } else {
                        searchResultsSubject.onError(Error(response.errorBody().toString()))
                    }
                }
            }
        })
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
