package es.udc.psi1718.project.util;

/**
 * Some constants used in our application
 */
public final class Constants {
	/**
	 * Private empty constructor to prevent instantiation
	 */
	private Constants() {
	}

	// Broadcast receiver
	public final static String ACTION_USB_PERMISSION = "es.udc.psi1718.project.USB_PERMISSION";

	// Slider Controller
	public final static long MAX_DELAY_TIME_SLIDER = 50;

	// Intent communication
	public final static String INTENTCOMM_CONTACTIV_LAUNCHEDFROMBR = "intentcomm_launchedfrombroadcastreceiver";
	public final static String INTENTCOMM_PANELID = "intentcomm_panelid";
	public final static String INTENTCOMM_PANELNAME = "intentcomm_panelname";
	public final static int INTENTCOMM_DONT_RECREATE = 999;
}
