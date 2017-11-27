package es.udc.psi1718.project.view.customviews.controllersgrid;


/**
 * Interface that acts as listener for {@link ControllersGridLayout}
 */
public interface ControllersGridListener {

	/**
	 * Indicates that controller have change positions in the layout
	 *
	 * @param initialPosition initial position where the controller was
	 * @param finalPosition   new position to where the controller is moved
	 */
	void controllersPositionChanged(int initialPosition, int finalPosition);
}
