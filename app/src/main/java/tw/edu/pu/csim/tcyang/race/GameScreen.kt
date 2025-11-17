package tw.edu.pu.csim.tcyang.race

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun GameScreen(
    message: String,
    gameViewModel: GameViewModel = viewModel()
) {
    // 讀取狀態
    val circleX = gameViewModel.circleX
    val circleY = gameViewModel.circleY
    val gameRunning = gameViewModel.gameRunning
    val winner = gameViewModel.winner
    val screenWidth = gameViewModel.screenWidthPx

    // 載入圖片
    val imageBitmaps = listOf(
        ImageBitmap.imageResource(R.drawable.horse0),
        ImageBitmap.imageResource(R.drawable.horse1),
        ImageBitmap.imageResource(R.drawable.horse2),
        ImageBitmap.imageResource(R.drawable.horse3)
    )

    // 使用 LaunchedEffect 處理首次啟動 (保持不變)
    LaunchedEffect(screenWidth) {
        if (screenWidth > 0f && !gameRunning) {
            gameViewModel.startGame()
        }
    }


    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color.Yellow)
        .onSizeChanged { size ->
            gameViewModel.setGameSize(size.width.toFloat(), size.height.toFloat())
        }
    ){
        // 🚩 修正 1: 將 Canvas 移到 Box 內部，並放在 UI 元素之前
        Canvas (modifier = Modifier
            .fillMaxSize()
            // 處理圓圈拖曳手勢
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    gameViewModel.MoveCircle(dragAmount.x, dragAmount.y)
                }

            }
        )
        {
            // 繪製圓圈
            drawCircle(
                color = Color.Red,
                radius = 100f,
                center = Offset(circleX, circleY)
            )

            // 繪製馬匹
            gameViewModel.horses.forEach { horse ->
                drawImage(
                    image = imageBitmaps[horse.number],
                    dstOffset = IntOffset(
                        horse.horseX,
                        horse.horseY
                    ),
                    dstSize = IntSize(200, 200)
                )

            }
        }

        // 🚩 修正 2: UI 元素 (Text 和 Button) 放在 Canvas 之後，確保它們疊在最上層

        // 顯示作者名稱
        Text(text = message)

        // 顯示勝利者訊息
        if (winner != null) {
            Text(
                text = "第 $winner 馬獲勝",
                color = Color.Black,
                fontSize = 40.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 遊戲暫停或結束時顯示按鈕
        if (!gameRunning) {
            Button(
                onClick = {
                    gameViewModel.startGame()
                },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp)
            ) {
                Text("遊戲開始")
            }
        }
    }
}