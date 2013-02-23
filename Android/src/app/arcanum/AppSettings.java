package app.arcanum;

import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

import android.content.Context;
import android.telephony.TelephonyManager;
import app.arcanum.crypto.ArcanumCrypto;

public class AppSettings {
	private static boolean isInitialized = false;
	private static Context _context;
	private static ArcanumCrypto _crypto;
	private static String _phone;
	
	public static void init(Context context) {
		_context = context;
		
		// One time initialization
		if(isInitialized)
			return;
		_crypto = new ArcanumCrypto(context);
		
		isInitialized = true;
	}
	
	public static final String APP_NAME = "app.arcanum";
		
	public static final Charset ENCODING = Charset.forName("UTF-8");
	public static final String SERVER_URL = "http://arcanum-app.appspot.com/";
	public static final byte[] SERVER_URL_BYTES = SERVER_URL.getBytes(ENCODING);
	
	public static final String MESSAGE_CONTENT_TYPE 	= "application/octet-stream";
	public static final String MESSAGE_CONTENT_ENCODING	= "base64";
	
	public static class Methods {
		public static final String REGISTER 		= "auth";
		public static final String SERVER_PUBKEY 	= "auth";
		public static final String SEND_MESSAGE 	= "msg";
		public static final String SYNC_CONTACTS 	= "contacts";
	}
	
	public static class GCM {
		public static final String PROJECT_ID = "695397584872";
		public static String REGISTRATION_ID = null;
	}
	
	public static ArcanumCrypto getCrypto() {
		if(_crypto == null) {
			_crypto = new ArcanumCrypto(_context);
		}
		return _crypto;
	}
	
	public static String getPhoneNumber() {
		if(StringUtils.isBlank(_phone)) {
			TelephonyManager tMgr =(TelephonyManager)_context.getSystemService(Context.TELEPHONY_SERVICE);
			try {
		    	_phone = tMgr.getLine1Number();
		    } catch(NullPointerException ex) {}

		    if(StringUtils.isBlank(_phone)) 
		    	_phone = tMgr.getSubscriberId();
		}
		return _phone.trim();
	}
}