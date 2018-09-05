package me.mateuspires.tictactoe.ui.customizer

import io.reactivex.Observable
import me.mateuspires.tictactoe.data.models.ImageSearch

interface CustomizerContract {

    interface Presenter {

        fun observeSearchResults(): Observable<ImageSearch.Result>

        fun search(query: String)

        fun selectImage(image: ImageSearch.Item)

        fun unselect()

        fun confirm()
    }

    interface View {

        fun onShowImage(image: ImageSearch.Item)

        fun onHide()
    }

    interface ImageSearchAdapter {

        fun update(images: Array<ImageSearch.Item>)
    }
}
