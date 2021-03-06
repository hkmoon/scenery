package scenery.controls.behaviours

import org.scijava.ui.behaviour.ClickBehaviour

/**
 * Toggle command class. Enables to call a parameter-free method of an instance
 * by the press of a button.
 *
 * @author Ulrik Günther <hello@ulrik.is>
 * @property[name] The name of the behaviour.
 * @property[receiver] The receiving object
 * @property[method] The name of the method to invoke
 */
class ToggleCommand(private val name: String, private val receiver: Any, private val method: String) : ClickBehaviour {

    /**
     * This function is called upon arrival of an event that concerns
     * this behaviour. It will execute the given method on the given object instance.
     *
     * @param[x] x position in window (unused)
     * @param[y] y position in window (unused)
     */
    override fun click(x: Int, y: Int) {
        val m = receiver.javaClass.getMethod(method)
        m.invoke(receiver)
    }
}
