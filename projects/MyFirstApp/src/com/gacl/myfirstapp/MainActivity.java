package com.gacl.myfirstapp;

import java.util.Calendar;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class MainActivity extends FragmentActivity {

	public final static String EXTRA_MESSAGE = "com.gacl.myfirstapp.MESSAGE";
	private static final int PICK_CONTACT_REQUEST = 0;
	protected ActionMode mActionMode;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //layout
        setContentView(R.layout.activity_main);
        
        //context menu
        findViewById(R.id.edit_message).setOnLongClickListener(new View.OnLongClickListener() {
            // Called when the user long-clicks on someView
            public boolean onLongClick(View view) {
                if (mActionMode != null) {
                    return false;
                }

                // Start the CAB using the ActionMode.Callback defined
                mActionMode = ((Activity) view.getContext()).startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.edit_text_context, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.create_new:
            	//Toast.makeText(getBaseContext(), "Primera opción...", Toast.LENGTH_LONG).show();
                return true;
            case R.id.open:
            	//Toast.makeText(getBaseContext(), "Segunda opción...", Toast.LENGTH_LONG).show();
                return true;
            default:
            	return false;
                //return super.onContextItemSelected(item);
        }
    }
    */
    
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            getMenuInflater().inflate(R.menu.edit_text_context, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
            	case R.id.create_new:
            		Toast.makeText(getBaseContext(), "Primera opción...", Toast.LENGTH_LONG).show();
            		return true;
            	case R.id.open:
            		Toast.makeText(getBaseContext(), "Segunda opción...", Toast.LENGTH_LONG).show();
            		return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
    
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        // Do something in response to button
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Event button */
    public void insertEvent(View view) {
	    Calendar beginTime = Calendar.getInstance();
	    beginTime.set(2013, 3, 25, 9, 45);
	    Calendar endTime = Calendar.getInstance();
	    endTime.set(2013, 3, 25, 14, 00);
	    Intent intent = new Intent(Intent.ACTION_INSERT)
	            .setData(Events.CONTENT_URI)
	            .putExtra(CalendarContract.EXTRA_CUSTOM_APP_URI, CalendarContract.ACCOUNT_TYPE_LOCAL)
	            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
	            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
	            .putExtra(Events.TITLE, "Tourneé por Ronda")
	            .putExtra(Events.DESCRIPTION, "Boss and company")
	            .putExtra(Events.EVENT_LOCATION, "The office")
	            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY)
	            .putExtra(Intent.EXTRA_EMAIL, "gustavocortesl@gmail.com,gustavo_cortes@hotmail.com");
	    startActivity(intent);
    }
    
    /** Called when the user clicks the Contact button */
    public void pickContact(View view) {
        // Create an intent to "pick" a contact, as defined by the content provider URI
        Intent intent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
		startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request went well (OK) and the request was PICK_CONTACT_REQUEST
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            // Perform a query to the contact's content provider for the contact's name
        	Log.d("CAGADA", "_ID = " + data.getData().getLastPathSegment());
        	
        	try {
           	 	Cursor cursor = getContentResolver().query(Data.CONTENT_URI,
       	          new String[] {Data._ID,Data.DISPLAY_NAME,Phone.NUMBER,Data.CONTACT_ID,Phone.TYPE, Phone.LABEL},
       	          Data._ID + "=? AND " + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
       	          new String[] {"" + data.getData().getLastPathSegment()}, 
       	          Data.DISPLAY_NAME);
           	 Log.d("CAGADA", "Count: " + cursor.getCount());
        		//Cursor cursor = getContentResolver().query(data.getData(),
	        	//	new String[] {Contacts.DISPLAY_NAME, Contacts.HAS_PHONE_NUMBER}, null, null, null);        	
	        	if (cursor.moveToFirst()) { // True if the cursor is not empty
	                int columnIndex = cursor.getColumnIndex(Contacts.DISPLAY_NAME);
	                String name = cursor.getString(columnIndex);
	                //columnIndex = cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER);
	                columnIndex = cursor.getColumnIndex(Phone.NUMBER);
	                String phone = cursor.getString(columnIndex);
	                // Do something with the selected contact's name...
	            	Intent intent = new Intent(this, DisplayMessageActivity.class);
	            	intent.putExtra(EXTRA_MESSAGE, name + " " + phone);
	            	startActivity(intent);
	        	}
            }        	
        	catch (Exception e) {
        		e.printStackTrace();
				Toast.makeText(this, "algo ha petado de mala manera...", Toast.LENGTH_LONG).show();					
			}        	
        }
    }

    public void onMenuSettingsClick(MenuItem item) {
        sendMessage(null);
    }

    public void onMenuViewClick(MenuItem item) {
    	pickContact(null);
    }
    
	public void startDialog(View v) {
        Intent intent = new Intent(MainActivity.this, DialogActivity.class);
        intent.putExtra(EXTRA_MESSAGE, getPhoneNumber());
        startActivity(intent);
    }
	
	private String getPhoneNumber(){
		TelephonyManager mTelephonyManager;
		mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE); 
		return mTelephonyManager.getLine1Number();
	}
	
	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getFragmentManager(), "datePicker");
	}
	
	//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getFragmentManager(), "timePicker");
	}
	
	public static class TimePickerFragment extends DialogFragment
					implements TimePickerDialog.OnTimeSetListener {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
			EditText time = (EditText) getActivity().findViewById(R.id.txt_time);
			time.setText(hourOfDay + ":" + minute);
		}
	}
	
	public static class DatePickerFragment extends DialogFragment
					implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			EditText date = (EditText) getActivity().findViewById(R.id.txt_date);
			date.setText(year + "-" + month + "-" + day);
		}
	}
	
}
