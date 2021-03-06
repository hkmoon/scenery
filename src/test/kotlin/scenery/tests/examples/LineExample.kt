package scenery.tests.examples

import cleargl.GLVector
import com.jogamp.opengl.GLAutoDrawable
import org.junit.Test
import org.scijava.ui.behaviour.ClickBehaviour
import scenery.*
import scenery.controls.ClearGLInputHandler
import scenery.controls.behaviours.ArcballCameraControl
import scenery.controls.behaviours.FPSCameraControl
import scenery.rendermodules.opengl.DeferredLightingRenderer
import kotlin.concurrent.thread

/**
 * Simple example to demonstrate the drawing of 3D lines.
 *
 * This example will draw a nicely illuminated bundle of lines using
 * the [Line] class. The line's width will oscillate while 3 lights
 * circle around the scene.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
class LineExample : SceneryDefaultApplication("LineExample") {
    protected var lineAnimating = true

    override fun init(pDrawable: GLAutoDrawable) {
        super.init(pDrawable)
        deferredRenderer = DeferredLightingRenderer(pDrawable.gl.gL4, glWindow!!.width, glWindow!!.height)
        hub.add(SceneryElement.RENDERER, deferredRenderer!!)

        var hull = Box(GLVector(50.0f, 50.0f, 50.0f))
        var hullmaterial = Material()
        hullmaterial.diffuse = GLVector(0.2f, 0.2f, 0.2f)
        hullmaterial.doubleSided = true
        hull.material = hullmaterial
        scene.addChild(hull)

        var linematerial = Material()
        linematerial.ambient = GLVector(1.0f, 0.0f, 0.0f)
        linematerial.diffuse = GLVector(0.0f, 1.0f, 0.0f)
        linematerial.specular = GLVector(1.0f, 1.0f, 1.0f)

        var line = Line()
        line.addPoint(GLVector(-1.0f, -1.0f, -1.0f))
        line.addPoint(GLVector(2.0f, 0.0f, 2.0f))
        line.material = linematerial
        line.position = GLVector(0.0f, 0.0f, 0.0f)

        scene.addChild(line)

        var colors = arrayOf(
            GLVector(1.0f, 0.0f, 0.0f),
            GLVector(0.0f, 1.0f, 0.0f),
            GLVector(0.0f, 0.0f, 1.0f)
        )

        var lights = (0..2).map {
            val l = PointLight()
            l.intensity = 200.0f
            l.emissionColor = colors[it]

            scene.addChild(l)
            l
        }

        val cam: Camera = DetachedHeadCamera()
        cam.position = GLVector(0.0f, 0.0f, 15.0f)
        cam.perspectiveCamera(50.0f, 1.0f*glWindow!!.width, 1.0f*glWindow!!.height)
        cam.active = true

        scene.addChild(cam)

        deferredRenderer?.initializeScene(scene)

        thread {
            var t = 0
            while(true) {
                if(lineAnimating) {
                    line.addPoint(GLVector(10.0f * Math.random().toFloat() - 5.0f, 10.0f * Math.random().toFloat() - 5.0f, 10.0f * Math.random().toFloat() - 5.0f))
                    line.edgeWidth = 0.03f * Math.sin(t * Math.PI / 50).toFloat() + 0.004f
                }

                Thread.sleep(100)

                lights.forEachIndexed { i, pointLight ->
                    pointLight.position = GLVector(0.0f, 15.0f*Math.sin(2*i*Math.PI/3.0f+t*Math.PI/50).toFloat(), -15.0f*Math.cos(2*i*Math.PI/3.0f+t*Math.PI/50).toFloat())
                }

                t++
            }
        }
    }

    override fun inputSetup() {
        val target = GLVector(0.0f, 0.0f, 0.0f)
        val inputHandler = (hub.get(SceneryElement.INPUT) as ClearGLInputHandler)
        val targetArcball = ArcballCameraControl("mouse_control", scene.findObserver(), glWindow!!.width, glWindow!!.height, target)
        val fpsControl = FPSCameraControl("mouse_control", scene.findObserver(), glWindow!!.width, glWindow!!.height)

        val toggleControlMode = object : ClickBehaviour {
            var currentMode = "fps"

            override fun click(x: Int, y: Int) {
                if (currentMode.startsWith("fps")) {
                    targetArcball.target = target

                    inputHandler.addBehaviour("mouse_control", targetArcball)
                    inputHandler.addBehaviour("scroll_arcball", targetArcball)
                    inputHandler.addKeyBinding("scroll_arcball", "scroll")

                    currentMode = "arcball"
                } else {
                    inputHandler.addBehaviour("mouse_control", fpsControl)
                    inputHandler.removeBehaviour("scroll_arcball")

                    currentMode = "fps"
                }

                System.out.println("Switched to $currentMode control")
            }
        }

        val toggleLineAnimation = object : ClickBehaviour {
            override fun click(x: Int, y: Int) {
                lineAnimating = !lineAnimating
            }
        }

        inputHandler.addBehaviour("toggle_control_mode", toggleControlMode)
        inputHandler.addKeyBinding("toggle_control_mode", "C")

        inputHandler.addBehaviour("toggle_line_animating", toggleLineAnimation)
        inputHandler.addKeyBinding("toggle_line_animating", "L")
    }

    @Test override fun main() {
        super.main()
    }
}
