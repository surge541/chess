package me.surge.display

import me.surge.Main
import me.surge.common.int
import me.surge.event.CharPressedEvent
import me.surge.event.ClickEvent
import me.surge.event.KeyEvent
import me.surge.util.Button
import org.lwjgl.glfw.*
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL


// default GLFW window initialisation
class Window(
    private val title: String,
    var width: Int,
    var height: Int,
    private val titleBar: Boolean = true,
    private val transparentFramebuffer: Boolean = false
) {

    // The window handle
    var window: Long = 0
        private set

    var mouseX: Float = 0f
        private set
    var mouseY: Float = 0f
        private set

    fun run(init: Runnable, render: Runnable): Window {
        init()
        loop(init, render)

        // Free the window callbacks and destroy the window
        glfwSetWindowShouldClose(window, true)
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        return this
    }

    private fun setMousePos(x: Float, y: Float) {
        this.mouseX = x
        this.mouseY = y
    }

    private fun init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        check(glfwInit()) { "Unable to initialize GLFW" }

        // Configure GLFW
        glfwDefaultWindowHints() // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE) // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE) // the window will be resizable
        glfwWindowHint(GLFW_DECORATED, titleBar.int)
        glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, transparentFramebuffer.int)

        // Create the window
        window = glfwCreateWindow(width, height, title, NULL, NULL)

        if (window == NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        glfwSetCursorPosCallback(window) { handle: Long, xpos: Double, ypos: Double ->
            setMousePos(xpos.toFloat(), ypos.toFloat())
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window) { window: Long, key: Int, scancode: Int, action: Int, mods: Int ->
            if (action == GLFW_PRESS) {
                Main.bus.post(KeyEvent(key))
            }
        }

        glfwSetCharCallback(window) { _, char ->
            Main.bus.post(CharPressedEvent(Char(char)))
        }

        glfwSetMouseButtonCallback(window) { _, button, action, mods ->
            if (action == GLFW_RELEASE) {
                Main.bus.post(ClickEvent(mouseX, mouseY, Button.from(button)))
            }
        }

        glfwSetFramebufferSizeCallback(window) { _, width, height ->
            glViewport(0, 0, width, height)

            this.width = width
            this.height = height
        }

        stackPush().use { stack ->
            val pWidth = stack.mallocInt(1) // int*
            val pHeight = stack.mallocInt(1) // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight)

            // Get the resolution of the primary monitor
            val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

            // Center the window
            glfwSetWindowPos(
                window,
                (vidmode!!.width() - pWidth[0]) / 2,
                (vidmode.height() - pHeight[0]) / 2
            )
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window)

        // Enable v-sync
        glfwSwapInterval(1)

        // Make the window visible
        glfwShowWindow(window)
    }

    private fun loop(init: Runnable, render: Runnable) {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities()

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

        init.run()

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            render.run()

            glfwSwapBuffers(window)
            glfwPollEvents()
        }
    }

    fun terminate(block: () -> Unit) {
        block()

        // Terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null)!!.free()
    }

}