package tw.edu.pu.csim.tcyang.race

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// 🚩 新增：馬匹類別
class Horse1(val number: Int, private val trackY: Float) { // number 用於選擇圖片 (0, 1, 2, 3...)
    var horseX by mutableIntStateOf(0)
    // 固定的賽道Y座標 (將 Float 轉為 Int，用於 IntOffset 繪圖)
    var horseY by mutableIntStateOf(trackY.toInt())

    // 隨機移動邏輯
    fun HorseRun() {
        horseX += Random.nextInt(5, 20) // 隨機步長移動
    }
}

class GameViewModel : ViewModel() {

    // ... 保持現有的 screenWidthPx, screenHeightPx, circleX, circleY 狀態 ...
    var screenWidthPx by mutableStateOf(0f)
        private set
    var screenHeightPx by mutableStateOf(0f)
        private set
    var circleX by mutableStateOf(100f)
        private set
    var circleY by mutableStateOf(0f)
        private set

    var gameRunning by mutableStateOf(false)
    // 🚩 新增：勝利者狀態，用於 UI 顯示
    var winner by mutableStateOf<Int?>(null) // null 表示還沒有勝利者

    fun setGameSize(w: Float, h: Float) {
        screenWidthPx = w
        screenHeightPx = h
    }

    fun MoveCircle(x: Float, y: Float) {
        circleX += x
        circleY += y
    }

    // 保持現有的 horses 定義，但因為要在 UI 上響應，改為 var
    val horses = mutableListOf<Horse>()



    fun startGame() {
        // 初始化馬匹（只做一次）
        if (horses.isEmpty()) {
            val trackHeight = screenHeightPx / 4
            horses.add(Horse(0, trackHeight * 1))
            horses.add(Horse(1, trackHeight * 2))
            horses.add(Horse(2, trackHeight * 3))
        }

        // 重置狀態
        for (horse in horses) {
            horse.horseX = 0
        }
        winner = null
        circleX = 100f
        circleY = screenHeightPx - 100f
        gameRunning = true

        // 啟動遊戲循環
        viewModelScope.launch {
            while (gameRunning && winner == null) {
                for (horse in horses) {
                    horse.run()
                    if (horse.horseX >= screenWidthPx - 200) {
                        winner = horse.number + 1
                        gameRunning = false
                        break
                    }
                }

                delay(100)
                circleX += 10
                if (circleX >= screenWidthPx - 100) {
                    circleX = 100f
                }
            }
        }
    }
}