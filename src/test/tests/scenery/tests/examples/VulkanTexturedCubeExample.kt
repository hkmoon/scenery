package scenery.tests.examples

import cleargl.GLVector
import com.jogamp.opengl.GLAutoDrawable
import org.junit.Test
import scenery.*
import scenery.backends.opengl.DeferredLightingRenderer
import scenery.backends.vulkan.VulkanRenderer
import scenery.repl.REPL
import kotlin.concurrent.thread

/**
 * <Description>
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
class VulkanTexturedCubeExample : SceneryDefaultApplication("VulkanTexturedCubeExample") {
    override fun init(pDrawable: GLAutoDrawable) {
        hub.add(SceneryElement.RENDERER, renderer!!)

        var boxmaterial = Material()
        with(boxmaterial) {
            ambient = GLVector(1.0f, 0.0f, 0.0f)
            diffuse = GLVector(0.0f, 1.0f, 0.0f)
            specular = GLVector(1.0f, 1.0f, 1.0f)
            textures.put("diffuse", TexturedCubeExample::class.java.getResource("textures/helix.png").file)
        }

        var box = Box(GLVector(1.0f, 1.0f, 1.0f))

        with(box) {
            box.material = boxmaterial
            scene.addChild(this)
        }

        var lights = (0..2).map {
            PointLight()
        }

        lights.mapIndexed { i, light ->
            light.position = GLVector(2.0f * i, 2.0f * i, 2.0f * i)
            light.emissionColor = GLVector(1.0f, 0.0f, 1.0f)
            light.intensity = 0.2f*(i+1);
            scene.addChild(light)
        }

        val cam: Camera = DetachedHeadCamera()
        with(cam) {
            position = GLVector(0.0f, 0.0f, 5.0f)
            perspectiveCamera(50.0f, 1.0f*glWindow!!.width, 1.0f*glWindow!!.height)
            active = true

            scene.addChild(this)
        }

        val bb = BoundingBox()
        bb.node = box

        thread {
            while (true) {
                box.rotation.rotateByAngleY(0.01f)
                box.needsUpdate = true

                Thread.sleep(20)
            }
        }

        renderer?.initializeScene(scene)

        repl = REPL(scene, renderer!!)
        repl?.start()
        repl?.showConsoleWindow()
    }

    @Test override fun main() {
        renderer = VulkanRenderer(1024, 1024)
        super.main()
    }
}