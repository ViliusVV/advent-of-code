package sandbox

import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.WindowConstants.EXIT_ON_CLOSE

data class Cell(val x: Int, val y: Int)

class FloodFillVisualizer : JFrame() {
    private val gridSize = 20
    private val cellSize = 30
    private val grid = Array(gridSize) { IntArray(gridSize) { 0 } }

    private var steps = mutableListOf<List<Cell>>()
    private var currentStep = 0

    private val canvas = object : JPanel() {
        override fun paintComponent(g: Graphics) {
            super.paintComponent(g)
            drawGrid(g)
        }
    }

    init {
        title = "Flood Fill Visualizer"
        defaultCloseOperation = EXIT_ON_CLOSE
        isResizable = false

        // Initialize a simple pattern
        initializePattern()

        // Setup canvas
        canvas.preferredSize = Dimension(gridSize * cellSize, gridSize * cellSize)
        canvas.background = Color.WHITE
        add(canvas, BorderLayout.CENTER)

        // Info panel
        val infoLabel = JLabel("Press SPACE to advance, R to reset, Click to start flood fill", SwingConstants.CENTER)
        add(infoLabel, BorderLayout.SOUTH)

        // Mouse listener for starting flood fill
        canvas.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val x = e.x / cellSize
                val y = e.y / cellSize
                if (x in 0 until gridSize && y in 0 until gridSize) {
                    startFloodFill(x, y)
                    canvas.repaint()
                }
            }
        })

        // Key binding for SPACE
        canvas.isFocusable = true
        canvas.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_SPACE -> {
                        if (currentStep < steps.size - 1) {
                            currentStep++
                            applyStep(currentStep)
                            canvas.repaint()
                        }
                    }
                    KeyEvent.VK_R -> {
                        resetVisualization()
                        canvas.repaint()
                    }
                }
            }
        })

        pack()
        setLocationRelativeTo(null)
        canvas.requestFocusInWindow()
    }

    private fun initializePattern() {
        // Create a border
        for (i in 0 until gridSize) {
            grid[i][0] = 1
            grid[i][gridSize - 1] = 1
            grid[0][i] = 1
            grid[gridSize - 1][i] = 1
        }

        // Add some obstacles
        for (i in 5..15) {
            grid[i][10] = 1
        }
        for (i in 5..10) {
            grid[5][i] = 1
        }
    }

    private fun startFloodFill(startX: Int, startY: Int) {
        if (grid[startY][startX] != 0) return

        resetVisualization()
        steps.clear()
        currentStep = 0

        val targetColor = 0
        val fillColor = 2
        val queue = ArrayDeque<Cell>()
        val visited = mutableSetOf<Cell>()

        queue.add(Cell(startX, startY))
        visited.add(Cell(startX, startY))

        while (queue.isNotEmpty()) {
            val stepCells = mutableListOf<Cell>()
            val batchSize = queue.size

            repeat(batchSize) {
                val cell = queue.removeFirst()
                stepCells.add(cell)

                // Check 4 neighbors
                val directions = listOf(
                    Cell(0, -1), Cell(0, 1), Cell(-1, 0), Cell(1, 0)
                )

                for (dir in directions) {
                    val nx = cell.x + dir.x
                    val ny = cell.y + dir.y
                    val neighbor = Cell(nx, ny)

                    if (nx in 0 until gridSize &&
                        ny in 0 until gridSize &&
                        grid[ny][nx] == targetColor &&
                        neighbor !in visited) {
                        queue.add(neighbor)
                        visited.add(neighbor)
                    }
                }
            }

            steps.add(stepCells)
        }
    }

    private fun applyStep(step: Int) {
        for (i in 0..step) {
            for (cell in steps[i]) {
                grid[cell.y][cell.x] = 2
            }
        }
    }

    private fun resetVisualization() {
        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                if (grid[y][x] == 2) grid[y][x] = 0
            }
        }
        currentStep = 0
    }

    private fun drawGrid(g: Graphics) {
        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                when (grid[y][x]) {
                    0 -> g.color = Color.WHITE
                    1 -> g.color = Color.BLACK
                    2 -> g.color = Color(100, 200, 255)
                }
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize)

                g.color = Color.LIGHT_GRAY
                g.drawRect(x * cellSize, y * cellSize, cellSize, cellSize)
            }
        }

        // Draw current step indicator
        if (steps.isNotEmpty() && currentStep < steps.size) {
            for (cell in steps[currentStep]) {
                g.color = Color(255, 100, 100)
                g.fillRect(cell.x * cellSize + 5, cell.y * cellSize + 5, cellSize - 10, cellSize - 10)
            }
        }
    }
}

fun main() {
    SwingUtilities.invokeLater {
        FloodFillVisualizer().isVisible = true
    }
}