package tw.edu.pu.csim.tcyang.race

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel // 引入 ViewModel 相關函式

@Composable
fun GameScreen(
    message: String,
    gameViewModel: GameViewModel = viewModel()
) {
    // ... 保持現有的狀態讀取 ...
    val circleX = gameViewModel.circleX
    val circleY = gameViewModel.circleY
    val gameRunning = gameViewModel.gameRunning
    // 🚩 新增：讀取勝利者狀態
    val winner = gameViewModel.winner

    // 載入圖片 (假設 R.drawable.horse3 存在，如果不存在請將此行刪除)
    val imageBitmaps = listOf(
        ImageBitmap.imageResource(R.drawable.horse0),
        ImageBitmap.imageResource(R.drawable.horse1),
        ImageBitmap.imageResource(R.drawable.horse2),
        // 🚩 假設第四張圖 (用於圓圈或額外用途，如果沒有請移除)
        ImageBitmap.imageResource(R.drawable.horse3)
    )


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Yellow)
        // 使用 onSizeChanged 設定遊戲尺寸，並在尺寸確定後啟動遊戲
        .onSizeChanged { size ->
            gameViewModel.setGameSize(size.width.toFloat(), size.height.toFloat())
            if (!gameRunning) {
                gameViewModel.startGame()
            }
        }
    ){
        // 🚩 修正：顯示作者名稱
        Text(text = message)

        // 🚩 新增：顯示勝利者訊息
        if (winner != null) {
            Text(
                text = "第 $winner 馬獲勝",
                color = Color.Black,
                fontSize = 40.sp,
                modifier = Modifier.align(Alignment.Center) // 將文字置於中央
            )
        }
    }

    Canvas (modifier = Modifier
        .fillMaxSize()
        // 呼叫 ViewModel 實例的方法
        .pointerInput(Unit) {
            detectDragGestures { change, dragAmount ->
                change.consume()
                gameViewModel.MoveCircle(dragAmount.x, dragAmount.y)
            }

        }

    )
    {
        // 繪製圓圈 (保持不變，它用於您的拖曳測試，與賽馬無關)
        drawCircle(
            color = Color.Red,
            radius = 100f,
            center = Offset(circleX, circleY)
        )

        // 🚩 繪製三匹馬
        // 繪製馬匹，並使用 ViewModel 中馬匹的座標
        gameViewModel.horses.forEach { horse ->
            drawImage(
                image = imageBitmaps[horse.number],
                dstOffset = IntOffset(
                    horse.horseX,
                    horse.horseY
                ),
                dstSize = IntSize(200, 200) // 馬匹圖片大小
            )
        }

        // 🚩 繪製終點線
        drawLine(
            color = Color.Black,
            start = Offset(gameViewModel.screenWidthPx - 200, 0f),
            end = Offset(gameViewModel.screenWidthPx - 200, gameViewModel.screenHeightPx),
            strokeWidth = 10f
        )
    }
}