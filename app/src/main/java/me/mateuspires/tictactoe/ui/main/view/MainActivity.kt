package me.mateuspires.tictactoe.ui.main.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.SimpleItemAnimator
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import me.mateuspires.tictactoe.R
import me.mateuspires.tictactoe.ui.customizer.view.CustomizerActivity
import me.mateuspires.tictactoe.ui.main.BoardCell
import me.mateuspires.tictactoe.ui.main.MainContract
import me.mateuspires.tictactoe.ui.main.Status
import me.mateuspires.tictactoe.ui.main.presenter.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.View, BoardAdapter.OnCellClickListener {

    private var presenter: MainContract.Presenter = MainPresenter(this)
    private var boardView: MainContract.BoardView = BoardAdapter(this, this)

    companion object {
        private const val TAG = "TTT.MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val grid = GridLayoutManager(this, 3)
        grid.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 3 else 1
            }
        }

        rv_board.adapter = boardView as BoardAdapter
        rv_board.layoutManager = grid
        rv_board.setHasFixedSize(true)
        rv_board.startLayoutAnimation()
        (rv_board.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

        bt_start.setOnClickListener { presenter.startNewGame() }
        bt_customize.setOnClickListener {
            startActivity(Intent(this, CustomizerActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        presenter.startNewGame()
    }

    override fun cellClickIsAllowed(): Boolean {
        return presenter.isPlaying()
    }

    override fun onCellClick(position: Int): Boolean {
        Log.d(TAG, "Move $position")
        return presenter.move(position)
    }

    override fun onUpdate(xTurn: Boolean, board: Array<BoardCell>) {
        Log.d(TAG, "Update $xTurn")
        boardView.update(if (xTurn) Status.X_TURN else Status.O_TURN, board)
    }

    override fun onWin(xPlayer: Boolean, board: Array<BoardCell>) {
        Log.d(TAG, "Win $xPlayer")
        boardView.update(if (xPlayer) Status.X_WINS else Status.O_WINS, board)
    }

    override fun onTie(board: Array<BoardCell>) {
        Log.d(TAG, "Tie")
        boardView.update(Status.TIED, board)
    }
}
