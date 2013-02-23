package app.arcanum;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import app.arcanum.contacts.ArcanumContact;
import app.arcanum.crypto.exceptions.MessageProtocolException;
import app.arcanum.tasks.HttpSendMessageTask;

public class MessageActivity extends Activity {
	private final LayoutParams msg_layout_params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	private final LayoutParams msg_layout_params_own = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message);
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        msg_layout_params.setMargins(0, 0, 50, 0);
        msg_layout_params_own.setMargins(50, 0, 0, 0);
        
        // Get Controls
        final EditText txtMessage = (EditText)findViewById(R.id.txtMessage);
        final LinearLayout listOfMessages = (LinearLayout)findViewById(R.id.listMessages);
        final Button button = (Button) findViewById(R.id.btnSend);
        
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	final String sendText = txtMessage.getText().toString();
				final EditText myMessage = build_MessageControl(sendText, true);
				listOfMessages.addView(myMessage);
				
				try {
					ArcanumContact to 	= new ArcanumContact();
					to.Token			= AppSettings.getPhoneNumber();
					
					byte[] sendBytes 	= AppSettings.getCrypto().create_message(to, sendText);
					String sendString 	= Base64.encodeToString(sendBytes, Base64.DEFAULT);
					
					builder
						.setTitle("Attention")
						.setMessage(String.format("You will send: \"%1$s\"", sendString))
						.setPositiveButton("OK", dialogClickListener)
					    .show();
					
					HttpSendMessageTask task = new HttpSendMessageTask();
					task.execute(sendBytes);
				} catch (MessageProtocolException ex) {
					builder
						.setTitle("ERROR")
						.setMessage(String.format("ERROR:\n%1$s", ex.getMessage()))
						.setPositiveButton("OK", dialogClickListener)
					    .show();
				}
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.activity_message, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
	    switch (item.getItemId()) {
		    case R.id.menu_load_server_key:
		    	//_arcanumCrypto.get_rsa().load_serverPublicKey();
		        return true;
		    case R.id.menu_contacts:
		    	Intent myIntent = new Intent(getBaseContext(), ContactsActivity.class);
                startActivity(myIntent);
		    	return true;
		    case R.id.menu_settings:
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

	private EditText build_MessageControl(final String msg, final boolean isOwnMessage) {
		EditText txt = new EditText(this);
		txt.setLayoutParams(isOwnMessage ? msg_layout_params_own : msg_layout_params);
		txt.setEms(10);
		txt.setText(msg);
		txt.setEnabled(false);
				
		return txt;
	}
	
	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		            break;
	
		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
	        }
	    }
	};
}
