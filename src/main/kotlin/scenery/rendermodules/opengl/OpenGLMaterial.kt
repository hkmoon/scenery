package scenery.rendermodules.opengl

import cleargl.GLProgram
import scenery.Material

/**
 * OpenGL Material class, extends [Material] by adding a GLProgram as property
 * to be used as a shader.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 */
class OpenGLMaterial : Material() {
    /** The shader program */
    var program: GLProgram? = null
}
