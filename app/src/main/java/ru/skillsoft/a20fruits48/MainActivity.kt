package ru.skillsoft.a20fruits48

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.log2

class MainActivity : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {
    var touchX = 0.0f
    var touchY = 0.0f
    var score = 0

    var registerMovement = false

    lateinit var adapter: ImageAdapter

    var board_positions_1 = arrayListOf(0, 0, 0, 0)
    var board_positions_2 = arrayListOf(0, 0, 0, 0)
    var board_positions_3 = arrayListOf(0, 0, 0, 0)
    var board_positions_4 = arrayListOf(0, 0, 0, 0)
    var nums = arrayListOf(2, 4)

    lateinit var sp: SharedPreferences

    var board_positions = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = getSharedPreferences("mySP", Context.MODE_PRIVATE)

        setRandomNumbers()
        setGird()

        best_score.text = sp.getInt("score", 0).toString()
        counter_score.text = score.toString()

        Log.d("TAG", "Stepen: " + log2(8.0f))
        root.setOnTouchListener(this)
    }

    private fun setRandomNumbers() {
        var isCanSet = true
        var randomPosition = (0..15).random()
        var randomNum = (0..1).random()
        while (board_positions[randomPosition] != 0) {
            if (!board_positions.contains(0)) {
                finishGame(false)
                root.setOnTouchListener(null)
                isCanSet = false
                break
            } else {
                randomPosition = (0..15).random()
            }
        }
        if (isCanSet) {
            score = score + 10
            counter_score.text = score.toString()
            board_positions[randomPosition] = nums[randomNum]
        }

    }

    private fun finishGame(isWin: Boolean) {
        root.visibility = View.GONE
        when(isWin){
            true -> {

                fieldWin.visibility = View.VISIBLE
                keep_going.setOnClickListener(this)
                try_again.setOnClickListener(this)
            }
            false -> {
                fieldLose.visibility = View.VISIBLE
                try_again_lose.setOnClickListener(this)
            }
        }
        Log.d("TAG1", "GameOver")
    }

    private fun setGird() {
        adapter = ImageAdapter(this, R.layout.list_item, board_positions)
        gridview.adapter = adapter
    }

    private fun doMove(direction: Direction) {
        when (direction) {
            Direction.RIGHT, Direction.LEFT -> {
                for (i in 0..3) {
                    board_positions_1[i] = board_positions[i]
                    board_positions_2[i] = board_positions[i + 4]
                    board_positions_3[i] = board_positions[i + 8]
                    board_positions_4[i] = board_positions[i + 12]
                }
            }

            Direction.UP, Direction.DOWN -> {
                var n = 0
                for (i in 0..15 step 4) {
                    board_positions_1[n] = board_positions[i]
                    board_positions_2[n] = board_positions[i + 1]
                    board_positions_3[n] = board_positions[i + 2]
                    board_positions_4[n] = board_positions[i + 3]
                    n++
                }
            }
        }

        board_positions_1 = calculateSumm(board_positions_1, direction)
        board_positions_2 = calculateSumm(board_positions_2, direction)
        board_positions_3 = calculateSumm(board_positions_3, direction)
        board_positions_4 = calculateSumm(board_positions_4, direction)

        when (direction) {
            Direction.RIGHT, Direction.LEFT -> {
                board_positions.clear()
                board_positions.addAll(board_positions_1 + board_positions_2 + board_positions_3 + board_positions_4)
                Log.d("TAG", "board_positions finish LEFT_RIGHT: $board_positions")
            }

            Direction.UP, Direction.DOWN -> {
                board_positions.clear()
                for (i in 0..3) {
                    board_positions.add(board_positions_1[i])
                    board_positions.add(board_positions_2[i])
                    board_positions.add(board_positions_3[i])
                    board_positions.add(board_positions_4[i])
                }
                Log.d("TAG", "board_positions finish UP_DOWN: $board_positions")
            }
        }

        if (board_positions.contains(2048)) finishGame(true)
        else {
            setRandomNumbers()
            setGird()
        }
    }

    private fun calculateSumm(array: ArrayList<Int>, dir: Direction): ArrayList<Int> {
        var new_board_position = arrayListOf<Int>()
        setZeroFirst(array).forEach() {
            new_board_position.add(it)
        }

        if (dir == Direction.RIGHT || dir == Direction.DOWN) new_board_position.reverse()

        if (new_board_position.size < 4) {
            for (i in 1..4 - new_board_position.size) {
                new_board_position.add(0)
            }
        }

        var n = new_board_position.size - 1
        for (i in 0..n) {

            if (i < n) {
                if (new_board_position[i] == new_board_position[i + 1]) {
                    new_board_position[i] = new_board_position[i] + new_board_position[i + 1]
                    new_board_position.apply {
                        add(0)
                        removeAt(i + 1)
                    }
                    n--
                }
            }
        }

        if (new_board_position.size < 4) {
            for (i in 1..4 - new_board_position.size) {
                new_board_position.add(0)
            }
        }

        if (dir == Direction.RIGHT || dir == Direction.DOWN) new_board_position.reverse()

        Log.d("TAG", "List: $new_board_position")

        return new_board_position
    }

    fun setZeroFirst(list: ArrayList<Int>) = list.filter { it > 0 }

    override fun onClick(v: View?) {
        if (sp.getInt("score", 0) < score){
            var ed = sp.edit()
            ed.putInt("score", score)
            ed.apply()
        }
        when (v!!.id){
            R.id.keep_going -> {}
            R.id.try_again -> {
                fieldWin.visibility = View.GONE
                root.setOnTouchListener(this)
                keep_going.setOnClickListener(null)
                try_again.setOnClickListener(null)
                root.visibility = View.VISIBLE
                board_positions = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                score = 0
                counter_score.text = score.toString()
                best_score.text = sp.getInt("score", 0).toString()
                setRandomNumbers()
                setGird()
            }
            R.id.try_again_lose -> {
                root.visibility = View.VISIBLE
                root.setOnTouchListener(this)
                fieldLose.visibility = View.GONE
                try_again_lose.setOnClickListener(null)
                board_positions = arrayListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
                score = 0
                counter_score.text = score.toString()
                best_score.text = sp.getInt("score", 0).toString()
                setRandomNumbers()
                setGird()
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.rawX
                touchY = event.rawY
                Log.d("TAG", "TouchX: $touchX")
                Log.d("TAG", "TouchY: $touchY")
            }

            MotionEvent.ACTION_MOVE -> {
                //Log.d("TAG", "MoveX: ${event.rawX}")
                //Log.d("TAG", "MoveY: ${event.rawY}")
                if (event.rawX - touchX > 50 && !registerMovement) {
                    Log.d("TAG", "Offset: right")
                    registerMovement = true
                    doMove(Direction.RIGHT)
                }

                if (event.rawX - touchX < -50 && !registerMovement) {
                    Log.d("TAG", "Offset: left")
                    registerMovement = true
                    doMove(Direction.LEFT)
                }

                if (event.rawY - touchY > 50 && !registerMovement) {
                    Log.d("TAG", "Offset: down")
                    registerMovement = true
                    doMove(Direction.DOWN)
                }

                if (event.rawY - touchY < -50 && !registerMovement) {
                    Log.d("TAG", "Offset: up")
                    registerMovement = true
                    doMove(Direction.UP)
                }
            }
            MotionEvent.ACTION_UP -> {
                touchX = 0.0f
                touchY = 0.0f
                registerMovement = false
            }
        }

        return true
    }


}

enum class Direction {
    UP, DOWN, LEFT, RIGHT
}
