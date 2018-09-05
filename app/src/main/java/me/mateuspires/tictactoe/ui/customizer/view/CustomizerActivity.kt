package me.mateuspires.tictactoe.ui.customizer.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_customizer.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.data.models.ImageSearch
import me.mateuspires.tictactoe.data.network.ImageSearchApiService
import me.mateuspires.tictactoe.ui.customizer.CustomizerContract
import me.mateuspires.tictactoe.ui.customizer.presenter.CustomizerPresenter
import java.util.concurrent.TimeUnit


class CustomizerActivity : AppCompatActivity(), CustomizerContract.View,
        ImagesAdapter.OnImageClickListener {

    companion object {
        const val TAG = "TTT.CustomizerActivity"
    }

    private val imagesAdapter = ImagesAdapter(this,  this)
    private var presenter: CustomizerPresenter? = null
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customizer)

        val isX = intent.getBooleanExtra("is_x", true)
        presenter = CustomizerPresenter(this, isX, ImageSearchApiService.load())

        presenter?.observeSearchResults()?.subscribe(
                { updateImages(it.items) }, { Log.e(TAG, it.message) })

        val searchFieldSubject: PublishSubject<String> = PublishSubject.create()
        searchFieldSubject.debounce(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { search(it) }

        et_search.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) { }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchFieldSubject.onNext(s.toString())
            }
        })

        rv_images.adapter = imagesAdapter
        rv_images.layoutManager = GridLayoutManager(this, 4)
        (rv_images.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        bt_select.setOnClickListener { presenter?.confirm() }
        bt_default.setOnClickListener { presenter?.unselect() }
    }

    override fun onImageClick(image: ImageSearch.Item) {
        Log.d(TAG, "Image click ${image.url}")
        presenter?.selectImage(image)
    }

    override fun onShowImage(image: ImageSearch.Item) {
        Log.d(TAG, "Show image ${image.url}")
        tv_not_selected.visibility = View.INVISIBLE
        iv_preview.visibility = View.VISIBLE

        Glide.with(this).load(image.url).apply(RequestOptions().centerCrop())
                .into(iv_preview)
    }

    override fun onHide() {
        Log.d(TAG, "Hide")
        tv_not_selected.visibility = View.VISIBLE
        iv_preview.visibility = View.INVISIBLE
    }

    private fun search(query: String) {
        Log.d(TAG, "Search $query")

        setLoading(true)
        presenter?.search(query)
    }

    private fun updateImages(images: Array<ImageSearch.Item>) {
        Log.d(TAG, "Update ${images.size}")

        imagesAdapter.update(images)
        setLoading(false)
        rv_images.startLayoutAnimation()
    }

    private fun setLoading(loading: Boolean) {
        if (this.loading != loading) {
            this.loading = loading

            if (loading) {
                rv_images.visibility = View.INVISIBLE
                pb_loading_spinner.visibility = View.VISIBLE
            } else {
                rv_images.visibility = View.VISIBLE
                pb_loading_spinner.visibility = View.INVISIBLE
            }
        }
    }
}
