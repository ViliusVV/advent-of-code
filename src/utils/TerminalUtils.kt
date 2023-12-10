package utils

import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame
import utils.interfaces.Grid
import java.awt.Font
import java.time.Duration
import java.time.Instant

data object TerminalUtilData {
    var lastFlush: Instant = Instant.now()
}

fun createTerminal(width: Int = 100, height: Int = 500, fontSize: Int = 20): SwingTerminalFrame {
    val tf = DefaultTerminalFactory()
        .setInitialTerminalSize(TerminalSize(width, height))
        .setTerminalEmulatorTitle("AoC")
        .setTerminalEmulatorFontConfiguration(SwingTerminalFontConfiguration.newInstance(Font(Font.MONOSPACED, Font.PLAIN, fontSize)))

    tf.setAutoOpenTerminalEmulatorWindow(true)

    val t = tf.createTerminalEmulator()
    t.setCursorVisible(false)

    return t as SwingTerminalFrame
}

fun Terminal.putStringAt(x: Int, y: Int, char: String,color: TextColor? = null,  bgColor: TextColor? = null) {
    if(color != null) {
        this.setForegroundColor(color)
    }

    if(bgColor != null) {
        this.setBackgroundColor(bgColor)
    }

    this.setCursorPosition(x, y)
    this.putString(char)
}

fun Terminal.putCharAt(x: Int, y: Int, char: Char, color: TextColor? = null, bgColor: TextColor? = null) {
    this.putStringAt(x, y, char.toString(), color, bgColor)
}

fun Terminal.putGrid(grid: Grid<Char>, xStart: Int = 0, yStart: Int = 0) {
    for(y in grid.indices) {
        for(x in grid[y].indices) {
            this.putCharAt(xStart + x, yStart + y, grid[x, y])
        }
    }
    this.flush()
}

fun Terminal.waitForClose() {
    this.flush()
    this.readInput()
    this.close()
}

fun Terminal.deferFlush(minSpace: Duration = Duration.parse("PT0.1S")) {
    val now = Instant.now()
    val diff = Duration.between(TerminalUtilData.lastFlush, now)
    if(diff < minSpace) {
        return
    }
    this.flush()
    TerminalUtilData.lastFlush = now
}