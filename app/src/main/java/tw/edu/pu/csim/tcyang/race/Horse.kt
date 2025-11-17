package tw.edu.pu.csim.tcyang.race

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

class Horse(val number: Int, trackY: Float) {
    var horseX by mutableIntStateOf(0)
    var horseY by mutableIntStateOf(trackY.toInt())

    fun run() {
        horseX += Random.nextInt(5, 20)
    }
}

