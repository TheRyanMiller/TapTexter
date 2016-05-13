package com.rtmillerprojects.taptexter.ui;

/**
 * Created by Ryan on 5/10/2016.
 */
import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rtmillerprojects.taptexter.R;

public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;

    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setHint("Type a message");
        EditText editSmsRecipient = (EditText) findViewById(R.id.editSmsRecipient);
        editSmsRecipient.setText("6023451108");

        Log.d("MYTAG!", "Birthday: ONCREATE");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED ) {

            Log.d("MYTAG!", "Birthday: CHECKSELFPERMISSIONS");

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                Log.d("MYTAG!", "Birthday: SHOULD SHOW PERMISSION RATIONALE");
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                Log.d("MYTAG!", "REQUEST PERMISSIONS");
            }
        }
        String myText = editText.getText().toString();
        Button send = (Button) findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                String msg = ((EditText) findViewById(R.id.editText)).getText().toString();
                String smsRecipient = ((EditText) findViewById(R.id.editSmsRecipient)).getText().toString();;
                iHavePermissions(msg, smsRecipient);
            }
        });
    }
    // method to get name, contact id, and birthday
    private Cursor getContactsBirthdays() {
        Uri uri = ContactsContract.Data.CONTENT_URI;

        String[] projection = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Event.CONTACT_ID,
                ContactsContract.CommonDataKinds.Event.START_DATE
        };

        String where =
                ContactsContract.Data.MIMETYPE + "= ? AND " +
                        ContactsContract.CommonDataKinds.Event.TYPE + "=" +
                        ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
        String[] selectionArgs = new String[] {
                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE
        };
        String sortOrder = null;
        return managedQuery(uri, projection, where, selectionArgs, sortOrder);
    }

    private void iHavePermissions(String msgText, String smsRecipient){
        String smsBody = msgText;
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS
        smsManager.sendTextMessage(smsRecipient, null, smsBody, null, null);
        // iterate through all Contact's Birthdays and print in log
        Cursor cursor = getContactsBirthdays();
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        while (cursor.moveToNext()) {
            String bDay = cursor.getString(bDayColumn);
            Log.d("MYTAG!", "Birthday: " + bDay);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // SMS Send


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("MYTAG!", "Birthday: NONE");
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // iterate through all Contact's Birthdays and print in log
                    Cursor cursor = getContactsBirthdays();
                    int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
                    while (cursor.moveToNext()) {
                        String bDay = cursor.getString(bDayColumn);
                        Log.d("MYTAG!", "Birthday: INTO THE READ CONNTACTS CASE");
                    }
                    return;
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request

            default: {
                break;
            }
            //checkSelfPermission();
            //requestPermissions();


        }

    }
    /** Called when the activity is about to become visible. */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /** Called when the activity has become visible. */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /** Called when another activity is taking focus. */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /** Called when the activity is no longer visible. */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /** Called just before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
