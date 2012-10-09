package name.zurell.kirk.apps.android.rhetolog.test;

import java.util.UUID;

import name.zurell.kirk.apps.android.rhetolog.RhetologContentProvider;
import name.zurell.kirk.apps.android.rhetolog.RhetologContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

public class ContentProviderTest extends
		ProviderTestCase2<RhetologContentProvider> {

	private static MockContentResolver mockContentResolver;
	
	public ContentProviderTest() {
		super(
				RhetologContentProvider.class,
				name.zurell.kirk.apps.android.rhetolog.RhetologContract.AUTHORITY
				);
	}

	/* (non-Javadoc)
	 * @see android.test.ProviderTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		mockContentResolver = getMockContentResolver();
		
		// Isolated context.
		setContext(getMockContext());
		
		//preConditions();
		
		
	}
	
//	private void preConditions() {
//		
//	}
	
	
	public void testTypicalUse1() {
		
		
		String testFallacy = "aasdf";
		int testTimestamp = 12341234;
		
		// Insert session
		ContentValues sessionValues = new ContentValues();
		sessionValues.put(RhetologContract.SessionsColumns.TITLE, "Fake session title");
		sessionValues.put(RhetologContract.SessionsColumns.UUID, UUID.randomUUID().toString());
		
		Uri insertedSession = mockContentResolver.insert(RhetologContract.SESSIONS_URI, sessionValues);
		
		int sessionId = Integer.valueOf(insertedSession.getLastPathSegment());
		
		// Insert participant
		ContentValues participantValues = new ContentValues();
		participantValues.put(RhetologContract.ParticipantsColumns.LOOKUP, "fakelookup");
		participantValues.put(RhetologContract.ParticipantsColumns.NAME, "Test User Name");
		participantValues.put(RhetologContract.ParticipantsColumns.PHOTO, "fake");
		participantValues.put(RhetologContract.ParticipantsColumns.SESSION, sessionId);
		
		Uri participantUri = mockContentResolver.insert(RhetologContract.PARTICIPANTS_URI, participantValues);
		
		int participantId = Integer.valueOf(participantUri.getLastPathSegment());
		
		ContentValues eventValues = new ContentValues();
		eventValues.put(RhetologContract.EventsColumns.SESSION, sessionId);
		eventValues.put(RhetologContract.EventsColumns.PARTICIPANT, participantId);
		eventValues.put(RhetologContract.EventsColumns.FALLACY, testFallacy);
		eventValues.put(RhetologContract.EventsColumns.TIMESTAMP, testTimestamp);
		
		Uri eventUri = mockContentResolver.insert(RhetologContract.EVENTS_URI, eventValues);
		
		int eventId = Integer.valueOf(eventUri.getLastPathSegment());
		
		// Test for positive result
		String[] projection = {
				RhetologContract.EventsColumns.FALLACY,	
				RhetologContract.EventsColumns.PARTICIPANT,	
				RhetologContract.EventsColumns.SESSION,	
				RhetologContract.EventsColumns.TIMESTAMP,	
		};
		Uri eventList = Uri.withAppendedPath(RhetologContract.SESSIONSPARTICIPANTSEVENTS_URI, 
				Integer.toString(sessionId) + "/" + Integer.toString(participantId));
		Cursor c = mockContentResolver.query(eventList, projection, null, null, null);
	 
		assertNotNull(c);
		assertTrue(c.moveToFirst());
		assertTrue(c.getCount() == 1);
		
		
		assertTrue(c.getString(c.getColumnIndex(RhetologContract.EventsColumns.FALLACY)).contentEquals(testFallacy));
		assertTrue(c.getInt(c.getColumnIndex(RhetologContract.EventsColumns.TIMESTAMP)) == testTimestamp);
		assertTrue(c.getInt(c.getColumnIndex(RhetologContract.EventsColumns.SESSION)) == sessionId);
		assertTrue(c.getInt(c.getColumnIndex(RhetologContract.EventsColumns.PARTICIPANT)) == participantId);
		
	}
	
	
	
	
	/* Test URI for session count */
	
	public void testEventCountQuery() {
		
		ContentValues values = new ContentValues();
		
		values.put(RhetologContract.EventsColumns.FALLACY, "asdf");
		values.put(RhetologContract.EventsColumns.TIMESTAMP, 23);
		values.put(RhetologContract.EventsColumns.SESSION, 3);
		values.put(RhetologContract.EventsColumns.PARTICIPANT, 3);
		
		mockContentResolver.insert(RhetologContract.EVENTS_URI, values);
		
		Uri sessTest = Uri.withAppendedPath(RhetologContract.EVENTSCOUNTSESSION_URI, "3");
		
		Cursor c = mockContentResolver.query(sessTest, null, null, null, null);
		c.moveToFirst();
		int countCol = c.getColumnIndex(RhetologContract.EventsCountSessionColumns.EVENTCOUNT);
		int eventCount = c.getInt(countCol);
		
		assertTrue(eventCount == 1);
		
	}
	
	
	public void testCurrentSessionSet() {
		
		
		final String tTitle = "to become default session";
		
		ContentValues cv = new ContentValues();
		cv.put(RhetologContract.SessionsColumns.TITLE, tTitle);
		cv.put(RhetologContract.SessionsColumns.UUID, UUID.randomUUID().toString());
		Uri newSessionUri = mockContentResolver.insert(RhetologContract.SESSIONS_URI, cv);
		
		String newSessionNumStr = newSessionUri.getLastPathSegment();
		long newSession = Long.valueOf(newSessionNumStr);
		
		mockContentResolver.call(RhetologContract.PROVIDER_URI, "setCurrentSession", newSessionNumStr, null);
		
		String[] tCurrentProjection = {
				RhetologContract.SessionsColumns.TITLE,
				RhetologContract.SessionsColumns._ID
		};
		Cursor c = mockContentResolver.query(RhetologContract.SESSIONSCURRENT_URI, tCurrentProjection, null, null, null);
		assertNotNull(c);
		assertTrue(c.moveToFirst());
		int cTitleCol = c.getColumnIndex(RhetologContract.SessionsColumns.TITLE);
		String cTitle = c.getString(cTitleCol);
		assertEquals(tTitle, cTitle);
		int cIdCol = c.getColumnIndex(RhetologContract.SessionsColumns._ID);
		long cId = c.getLong(cIdCol);
		assertEquals(newSession, cId);
		
		
	}
	
}
