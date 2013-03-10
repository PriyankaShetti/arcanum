package app.arcanum;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import app.arcanum.contacts.ArcanumContact;
import app.arcanum.contacts.ArcanumContactManager;
import app.arcanum.ui.adapters.ArcanumContactAdapter;

public class ContactsActivity extends Activity {
	private ListView _contactsView;	
	
	private ArcanumContactManager _manager;
	private ArcanumContactAdapter _contactsAdapter;
	private ArrayList<ArcanumContact> _contacts;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		
		if(!AppSettings.isInitialized)
			AppSettings.init(this);
		_manager = AppSettings.getContactManager();
			
		// Initialize contacts
		ContactClickListener clickListener = new ContactClickListener();
		_contacts = new ArrayList<ArcanumContact>();
		_contactsAdapter = new ArcanumContactAdapter(this, android.R.layout.simple_list_item_1, _contacts);
		_contactsView = (ListView)findViewById(R.id.listContacts);
		_contactsView.setAdapter(_contactsAdapter);
		_contactsView.setOnItemClickListener(clickListener);
		_contactsView.setOnItemLongClickListener(clickListener);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		refreshContactList();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_contacts, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
	    switch (item.getItemId()) {
	    	case R.id.menu_contacts_sync:
	    		_manager.Sync();
	    		refreshContactList();
	    		return true;
		    case R.id.menu_settings:
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
	
	public void refreshContactList() {
		_contacts.clear();		
		for(ArcanumContact c : _manager.getAll()) {
			_contacts.add(c);
			_contactsAdapter.notifyDataSetChanged();
		}
	}
		
	private class ContactClickListener implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Object item = _contactsView.getItemAtPosition(position);
			if(item != null && item instanceof ArcanumContact) {
				ArcanumContact contact = (ArcanumContact)item;
				Intent intent = new Intent(getBaseContext(), MessageActivity.class);
				intent.putExtra("contact", contact);
				startActivity(intent);
				finish();
			}
		}
		
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			return false;
		}
	}
}
