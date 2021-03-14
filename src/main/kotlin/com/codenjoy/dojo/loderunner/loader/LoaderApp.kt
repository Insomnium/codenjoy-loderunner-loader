package com.codenjoy.dojo.loderunner.loader

import com.codenjoy.dojo.client.WebSocketRunner
import com.codenjoy.dojo.loderunner.client.Board
import com.codenjoy.dojo.loderunner.client.ai.AISolver
import com.codenjoy.dojo.services.Dice
import com.codenjoy.dojo.services.RandomDice
import com.codenjoy.dojo.services.hash.Hash
import org.slf4j.LoggerFactory
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Loader(
    private val count: Int,
    private val nicknamePrefix: String,
    private val baseUrl: String
) {

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(count)

    fun perform() {
        repeat(count) {
            threadPool.submit {
                WebSocketRunner.runClient(
                    buildConnectionString(it),
                    AiSolverWrapper(RandomDice()),
                    Board()
                )
            }
        }
        threadPool.shutdown()
    }

    private fun buildConnectionString(playerIndex: Int): String =
        with("$nicknamePrefix${(playerIndex+1).toString().padStart(2, '0')}") {
            "$baseUrl/board/player/$this?code=${Hash.getCode(this, this)}"
        }
}

class AiSolverWrapper(dice: Dice) : AISolver(dice) {

    companion object {
        val LOG = LoggerFactory.getLogger(Loader::class.java)
    }

    override fun get(board: Board?): String = with(super.get(board)) {
        LOG.info(">>> $this")
        this
    }
}

fun main(args: Array<String>) {
    val count = args.getOrDefault(0, "20").toInt()
    val baseUrl = args.getOrDefault(1, "http://localhost:8080/codenjoy-contest")
    val nicknamePrefix = args.getOrDefault(2, "demo")
    Loader(count, nicknamePrefix, baseUrl).perform()
}

fun <T : Any> Array<T>.getOrDefault(i: Int, defaultValue: T): T = if (this.size >= i+1) this[i] else defaultValue