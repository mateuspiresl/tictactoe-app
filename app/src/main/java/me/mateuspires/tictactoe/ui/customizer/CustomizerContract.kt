package me.mateuspires.tictactoe.ui.customizer

import io.reactivex.Single
import me.mateuspires.tictactoe.data.models.ImageSearch

interface CustomizerContract {

    interface Presenter {

        fun search(query: String): Single<ImageSearch.Result>

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
