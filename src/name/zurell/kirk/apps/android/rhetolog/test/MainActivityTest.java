package name.zurell.kirk.apps.android.rhetolog.test;

import name.zurell.kirk.apps.android.rhetolog.MainActivity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ToggleButton;

public class MainActivityTest extends
		ActivityInstrumentationTestCase2<MainActivity> {
	
	
	private MainActivity mActivity;
	private ToggleButton sessionToggle;
	
	public MainActivityTest() {
		super("name.zurell.kirk.apps.android.rhetolog", MainActivity.class);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(false);
		
		mActivity = getActivity();
		sessionToggle = (ToggleButton) mActivity.findViewById(name.zurell.kirk.apps.android.rhetolog.R.id.sessionToggle);
		
		preConditions();
		
	}
	
	
	public void preConditions() {
		assertTrue(sessionToggle.isChecked() == false);
		
	}
	

	public void testToggleSessionClock() {
		
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				sessionToggle.requestFocus();
				sessionToggle.toggle();
				assertTrue(sessionToggle.isChecked() == true);
			}
		});
	}
	
}
