package me.surge.display.components

import me.surge.Main
import me.surge.amalia.handler.Listener
import me.surge.animation.Animation
import me.surge.animation.Easing
import me.surge.client.Settings
import me.surge.common.chess.*
import me.surge.common.chess.operators.Operator
import me.surge.common.chessColumn
import me.surge.common.chessRow
import me.surge.common.packet.ClientGameUpdate
import me.surge.common.packet.GameUpdateRequestPacket
import me.surge.common.title
import me.surge.util.Button
import me.surge.util.Theme
import me.surge.util.Theme.boardAlternate
import me.surge.util.integrateAlpha
import org.lwjgl.nanovg.NanoVG.nvgGlobalAlpha
import org.nvgu.NVGU
import org.nvgu.util.Alignment
import java.awt.Color
import java.awt.geom.Rectangle2D

class BoardComponent(board: Board, x: Float, y: Float, dimension: Float) : Component(x, y, dimension, dimension) {

    var board: Board = board
        set(value) {
            field = value
            resetCells()
        }

    private val cells = mutableListOf<RenderCell>()

    var selectedCell: Cell? = null
    var operator: Operator? = null
    val selectableMoves = mutableListOf<Move>()

    private val ended = Animation(500f, false, Easing.EXPO_IN_OUT)
    private var removePrioritised = false

    init {
        resetCells()
    }

    override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
        if (Main.game != null && Main.game!!.turn != Main.side) {
            selectableMoves.clear()
        }

        // draw board
        run {
            val cellDimensions = width / 8f

            var x = x
            var y = y

            val renderCells = ArrayList(cells)

            if (Main.game != null && Main.side == Side.BLACK) {
                renderCells.reverse()
            }

            var relativeX = 0
            var relativeY = 8

            for (renderCell in renderCells) {
                renderCell.x = x
                renderCell.y = y
                renderCell.relativeX = relativeX
                renderCell.relativeY = relativeY
                renderCell.dimension = cellDimensions

                if (removePrioritised) {
                    renderCell.pawnPromoter = null
                }

                renderCell.draw(ctx, mouseX, mouseY)
                renderCell.drawPiece(ctx, renderCell.x, renderCell.y)
                renderCell.drawOverlay(ctx)

                x += cellDimensions
                relativeX++

                if (relativeX == 8) {
                    x = this.x
                    y += cellDimensions
                    relativeX = 0
                    relativeY -= 1
                }
            }
        }

        // draw ending overlay
        run {
            if (Main.game != null) {
                val game = Main.game!!

                if (!game.playing && Main.lastEndReason != null && Main.lastWinner != null) {
                    ended.state = true

                    val bounds = Rectangle2D.Float(x + (width - (width - 90f)) / 2f, y + (height - (height - 190f)) / 2f, width - 90f, height - 190f).bounds

                    ctx.rectangle(x, y, width, height, Settings.theme.boardHighlight.integrateAlpha(0.4f * ended.linearFactor.toFloat()))
                        .save()
                        .also { nvgGlobalAlpha(ctx.handle, ended.linearFactor.toFloat()) }
                        .scale(x + width / 2f, y + height / 2f, 0.8f + (0.2f * ended.animationFactor.toFloat()))
                        .roundedRectangle(bounds, Settings.theme.cornerRadius, Settings.theme.surface)
                        .text("Game Over", bounds.centerX.toFloat(), bounds.y + 30f, Settings.theme.onSurface, "poppins", 22, Alignment.CENTER_TOP)
                        .text("${Main.lastWinner!!.name.title} Wins", bounds.centerX.toFloat(), bounds.centerY.toFloat(), Settings.theme.onSurface, "poppins", 32, Alignment.CENTER_BOTTOM)
                        .text("by ${Main.lastEndReason!!.name.lowercase()}", bounds.centerX.toFloat(), bounds.centerY.toFloat(), Settings.theme.onSurface, "poppins", 16, Alignment.CENTER_TOP)
                        .restore()

                } else {
                    ended.state = false
                }
            } else {
                ended.state = false
            }
        }
    }

    override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
        if (Main.game?.playing == true) {
            cells.forEach {
                if (it.click(mouseX, mouseY, button)) {
                    return true
                }
            }
        }

        ended.state = !ended.state

        return false
    }

    fun get(cell: Cell): RenderCell = cells.find { it.cell == cell }!!

    fun isCellOurs(cell: Cell): Boolean = cell.piece.second == Main.side

    @Listener
    fun onGameUpdate(packet: GameUpdateRequestPacket.GameUpdateRequestResponsePacket) {
        board.sync(packet.game!!.board)
    }

    fun resetCells() {
        cells.clear()

        var colour = Settings.theme.boardOne
        var y = 0
        val cellDimensions = width / 8f

        board.cells.forEach { cell ->
            if (cell.y != y) {
                colour = if (colour == Settings.theme.boardOne) Settings.theme.boardTwo else Settings.theme.boardOne
                y = cell.y
            }

            cells.add(RenderCell(0f, 0f, cellDimensions, cell, colour))
            colour = if (colour == Settings.theme.boardOne) Settings.theme.boardTwo else Settings.theme.boardOne
        }
    }

    inner class RenderCell(x: Float, y: Float, var dimension: Float, val cell: Cell, val colour: Color) : Component(x, y, dimension, dimension) {

        private val hovered = Animation(400f, false, Theme.easing)

        var relativeX = 0
        var relativeY = 0

        var pawnPromoter: PawnPromoter? = null

        override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
            hovered.state = hovered(mouseX, mouseY)

            ctx.rectangle(x, y, dimension, dimension, colour)

            if (relativeX == 0) {
                ctx.text((9 - cell.y.chessRow).toString(), x + 2f, y + 2f, colour.boardAlternate, "poppins", 12, Alignment.LEFT_TOP)
            }

            if (relativeY == 1) {
                ctx.text(cell.x.chessColumn, x + width - 2f, y + height - 2f, colour.boardAlternate, "poppins", 12, Alignment.RIGHT_BOTTOM)
            }

            pawnPromoter?.draw(ctx, mouseX, mouseY)
        }

        fun drawPiece(ctx: NVGU, x: Float, y: Float) {
            if (cell.piece.first != Piece.EMPTY && cell.piece.second != Side.EITHER) {
                ctx.save()
                    .scale(x + dimension / 2f, y + dimension / 2f, 0.7f + (0.2f * hovered.animationFactor).toFloat())
                    .texturedRectangle(
                        x,
                        y,
                        dimension,
                        dimension,
                        "${Settings.theme.id}.${cell.piece.first.name.lowercase()}.${cell.piece.second.name.lowercase()}"
                    )
                    .restore()
            }
        }

        fun drawOverlay(ctx: NVGU) {
            if (selectableMoves.any { it.to == cell }) {
                ctx.circle(x + dimension / 2f, y + dimension / 2f, ((dimension / 2f) - 10f) + (2.5f * hovered.animationFactor).toFloat(), Settings.theme.boardHighlight)
            }
        }

        override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
            if (pawnPromoter != null && Main.screen.prioritised == pawnPromoter) {
                pawnPromoter = null
                Main.screen.prioritised = null
            }

            if (hovered(mouseX, mouseY) && Main.game != null && Main.side != null && Main.game!!.turn == Main.side) {
                if (selectableMoves.any { it.to == cell } && selectedCell != null) {
                    val move = selectableMoves.first { it.to == cell }

                    if (move.tag == Move.Tag.PAWN_PROMOTION) {
                        pawnPromoter = PawnPromoter(this, move, mouseX, mouseY)
                        Main.screen.prioritised = pawnPromoter
                        return true
                    }

                    Main.connection!!.post(ClientGameUpdate(Main.game!!.id, move))
                    return true
                }

                selectableMoves.clear()

                if (isCellOurs(cell)) {
                    operator = Operator.getOperator(this.cell, Main.side!!)

                    if (operator != null) {
                        selectedCell = this.cell
                        selectableMoves.addAll(operator!!.collectTiles(this.cell, board, Main.side!!))
                    }
                } else {
                    operator = null
                }

                return true
            }

            return false
        }

    }

    inner class PawnPromoter(private val cell: RenderCell, val move: Move, x: Float, y: Float) : Component(x, y, 228f, 60f) {

        private val buttons = listOf(
            object : IconButtonComponent("${Settings.theme.id}.queen.${move.from.piece.second.name.lowercase()}", x, y, 52f, 52f) {
                override fun pressed(mouseX: Float, mouseY: Float, button: Button) = press(Piece.QUEEN)
            },

            object : IconButtonComponent("${Settings.theme.id}.bishop.${move.from.piece.second.name.lowercase()}", x, y, 52f, 52f) {
                override fun pressed(mouseX: Float, mouseY: Float, button: Button) = press(Piece.BISHOP)
            },

            object : IconButtonComponent("${Settings.theme.id}.knight.${move.from.piece.second.name.lowercase()}", x, y, 52f, 52f) {
                override fun pressed(mouseX: Float, mouseY: Float, button: Button) = press(Piece.KNIGHT)
            },

            object : IconButtonComponent("${Settings.theme.id}.rook.${move.from.piece.second.name.lowercase()}", x, y, 52f, 52f) {
                override fun pressed(mouseX: Float, mouseY: Float, button: Button) = press(Piece.ROOK)
            }
        )

        override fun update(mouseX: Float, mouseY: Float) {
            var buttonX = x + 4f

            buttons.forEach {
                it.setBounds(buttonX, y + 4f)
                it.update(mouseX, mouseY)

                buttonX += 56f
            }
        }

        override fun draw(ctx: NVGU, mouseX: Float, mouseY: Float) {
            ctx.roundedRectangle(x, y, width, height, Settings.theme.cornerRadius, Settings.theme.surface)

            buttons.forEach {
                it.draw(ctx, mouseX, mouseY)
            }
        }

        override fun click(mouseX: Float, mouseY: Float, button: Button): Boolean {
            buttons.forEach {
                if (it.click(mouseX, mouseY, button)) {
                    return true
                }
            }

            return false
        }

        private fun press(piece: Piece) {
            Main.connection!!.post(ClientGameUpdate(Main.game!!.id, move.piece(piece)))
            Main.screen.prioritised = null
            cell.pawnPromoter = null
        }

    }

}