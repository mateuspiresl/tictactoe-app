package me.mateuspires.tictactoe.ui.customizer.presenter

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import me.mateuspires.tictactoe.data.Repository
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.data.models.PlayersImages
import me.mateuspires.tictactoe.data.network.ImageSearchApi
import me.mateuspires.tictactoe.game.Player
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract
import me.mateuspires.tictactoe.ui.customizer.view.PreviewHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit


class CustomizerPresenter(
        private val view: CustomizerContract.View,
        private val repository: Repository<PlayersImages>,
        private val imageSearch: ImageSearchApi
): CustomizerContract.Presenter, PreviewHolder.PreviewHolderActionsListener {

    companion object {
        const val TAG = "CustomizerPresenter"
    }

    private val searchResultsSubject: PublishSubject<ImageSearch.Result> = PublishSubject.create()
    private var xImage: ImageSearch.Item? = null
    private var yImage: ImageSearch.Item? = null
    private var selectedImage: ImageSearch.Item? = null
    private var lastRequestId = 0

    init {
        val images = repository.load()
        images.xUrl?.let { xImage = ImageSearch.Item(it, 100, 100) }
        images.yUrl?.let { yImage = ImageSearch.Item(it, 100, 100) }
    }

    override fun getSearchResultsObservable(): Observable<ImageSearch.Result> {
        return searchResultsSubject.observeOn(AndroidSchedulers.mainThread())
    }

    override fun search(query: String) {
        if (query.isNotEmpty()) {
            lastRequestId += 1
            search(query, lastRequestId)
        }
    }

    override fun selectImage(image: ImageSearch.Item) {
        selectedImage = image
    }

    override fun unselect() {
        selectedImage = null
    }

    override fun done() {
        repository.save(PlayersImages(xImage?.url, yImage?.url))
    }

    override fun onImageAttach(player: Player) {
        selectedImage?.let {
            selectedImage = null

            when (player) {
                Player.X -> xImage = it
                Player.Y -> yImage = it
            }

            view.onImageAttach(player, it)
        }
    }

    override fun onImageDetach(player: Player) {
        when (player) {
            Player.X -> xImage = null
            Player.Y -> yImage = null
        }

        view.onImageDetach(player)
    }

    private fun search(query: String, id: Int) {
        imageSearch.search(query).enqueue(object: Callback<ImageSearch.Result> {
            override fun onFailure(call: Call<ImageSearch.Result>?, t: Throwable?) {
                Log.d(TAG, "On search failure")

                if (id == lastRequestId) {
                    Completable.complete()
                            .delay(500, TimeUnit.SECONDS)
                            .doOnComplete {
                                if (id == lastRequestId) {
                                    search(query, id)
                                }
                            }
                            .subscribe()
                }
            }

            override fun onResponse(call: Call<ImageSearch.Result>?, response: Response<ImageSearch.Result>?) {
                Log.d(TAG, "On search response")
                if (response != null) {
                    val body = response.body()

                    if (response.isSuccessful && body != null) {
                        // Ignore result if another search was made after
                        if (id == lastRequestId) {
                            searchResultsSubject.onNext(body)
                        }
                    } else {
                        Log.d(TAG, "On search failure ${response.errorBody().toString()}")
                    }
                }
            }
        })
    }
}
