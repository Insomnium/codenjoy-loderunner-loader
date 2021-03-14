package com.codenjoy.dojo.loderunner.manual

import com.codenjoy.dojo.client.Solver
import com.codenjoy.dojo.client.WebSocketRunner
import com.codenjoy.dojo.loderunner.client.Board
import com.codenjoy.dojo.services.Direction
import com.codenjoy.dojo.services.RandomDice
import java.util.*
import java.util.concurrent.Executors

class ManualSolver(
    private val controller: ConsoleController
) : Solver<Board> {
    private var board: Board? = null

    override fun get(board: Board): String {
        this.board = board
        return if (board.isGameOver) "" else controller.currentDirection().toString()
    }
}

class ConsoleController : Runnable {
    private val scanner = Scanner(System.`in`)
    private val directionsByControl = mapOf(
        "a" to Direction.LEFT,
        "s" to Direction.DOWN,
        "d" to Direction.RIGHT,
        "w" to Direction.UP
    )
    private var currentControl: String? = null

    fun currentDirection(): Direction = directionsByControl[currentControl] ?: Direction.STOP

    override fun run() {
        while (true) {
            currentControl = scanner.nextLine()
        }
    }
}

fun main() {
    val controller = ConsoleController()
    val executor = Executors.newSingleThreadExecutor()
    executor.submit(controller)
    WebSocketRunner.runClient( // paste here board page url from browser after registration
        "http://localhost:8080/codenjoy-contest/board/player/demo01?code=3478742445851294237",
        ManualSolver(controller),
        Board()
    )
    executor.shutdown()
}