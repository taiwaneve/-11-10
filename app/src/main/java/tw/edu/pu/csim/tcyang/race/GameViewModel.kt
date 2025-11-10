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
class Horse(val number: Int, private val trackY: Float) { // number 用於選擇圖片 (0, 1, 2, 3...)
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

    // 🚩 新增：重置所有馬匹位置的方法
    private fun resetRace() {
        for (horse in horses) {
            horse.horseX = 0
        }
        winner = null
        gameRunning = true // 準備開始下一輪
    }


    fun startGame() {
        // 🚩 修正：首次啟動時才初始化馬匹列表
        if (horses.isEmpty()) {
            // 計算三條賽道的 Y 座標 (將螢幕高度分為 4 份，從 1/4, 2/4, 3/4 處放置)
            val trackHeight = screenHeightPx / 4
            horses.add(Horse(0, trackHeight * 1)) // 馬匹 1 (使用 horse0 圖片)
            horses.add(Horse(1, trackHeight * 2)) // 馬匹 2 (使用 horse1 圖片)
            horses.add(Horse(2, trackHeight * 3)) // 馬匹 3 (使用 horse2 圖片)
        }

        // 確保遊戲狀態重置
        gameRunning = true
        winner = null
        circleX = 100f
        circleY = screenHeightPx - 100f
        // 確保所有馬匹回到起點
        resetRace()

        viewModelScope.launch {
            // 🚩 修正：只有當沒有勝利者且遊戲正在運行時才繼續循環
            while (gameRunning && winner == null) {

                // 🚩 馬匹移動與勝利判斷邏輯
                for (horse in horses) {
                    horse.HorseRun()

                    // 🚩 終點線判定 (終點設為螢幕寬度 - 200 像素，留出馬匹顯示空間)
                    if (horse.horseX >= screenWidthPx - 200) {
                        winner = horse.number + 1 // 記錄獲勝馬匹號碼 (1, 2, 3)
                        gameRunning = false // 停止遊戲循環
                        break
                    }
                }

                // 原來的圓圈移動邏輯 (保持不變)
                delay(100)
                circleX += 10
                if (circleX >= screenWidthPx - 100) {
                    circleX = 100f
                }
            }

            // 🚩 新增：如果遊戲結束，等待 2 秒後重置，準備下一輪
            if (winner != null) {
                delay(2000) // 顯示勝利訊息 2 秒
                resetRace() // 重置馬匹位置並重新開始遊戲
            }
        }
    }
}