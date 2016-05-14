package com.rtmillerprojects.taptexter.ui;

/**
 * Created by Ryan on 5/10/2016.
 */
import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rtmillerprojects.taptexter.R;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    ListView l;
    String[] dataSource = {"Happy Birthday!", "Happy Anniversary!", "Merry Christmas!", "Happy New Year!", "Thursday", "Friday", "Saturday","Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        l = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataSource);
        l.setAdapter(adapter);
        l.setOnItemClickListener(this);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setHint("Type a message");
        EditText editSmsRecipient = (EditText) findViewById(R.id.editSmsRecipient);
        editSmsRecipient.setText("6023451108");

        //check if we have permission
            //if no, prompt permission - no
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_CONTACTS},MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }


        String myText = editText.getText().toString();
        Button send = (Button) findViewById(R.id.btn_send);
        final Context _this = this;
        send.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(_this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) _this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                String msg = ((EditText) findViewById(R.id.editText)).getText().toString();
                String smsRecipient = ((EditText) findViewById(R.id.editSmsRecipient)).getText().toString();;
                if(msg.length()>0) {
                    boolean smsSuccess = iHavePermissions(msg, smsRecipient, _this);
                    if(smsSuccess){((EditText) findViewById(R.id.editText)).setText("");}
                }else{
                    Toast.makeText(_this,"Need to enter text before you can send",Toast.LENGTH_SHORT).show();
                }
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

    private boolean iHavePermissions(String msgText, String smsRecipient, Context context){
        String smsBody = msgText;
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS

        // iterate through all Contact's Birthdays and print in log
        Cursor cursor = getContactsBirthdays();
        int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
        int phoneName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int phoneNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        while (cursor.moveToNext()) {
            String bDay = cursor.getString(bDayColumn);
            Log.d("MYTAG!", "Birthday: " + bDay);
            Log.d("MYTAG!", "Name: " + phoneName);
            Log.d("MYTAG!", "Number: " + phoneNumber);
        }
        try{
            smsManager.sendTextMessage(smsRecipient, null, smsBody, null, null);
            Toast.makeText(context,"Message has been sent",Toast.LENGTH_SHORT).show();
            return true;
        }
        catch(Error err){
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("MYTAG!", "Birthday: NONE");
                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView temp = (TextView) view;
        Toast.makeText(this,temp.getText().toString()+" "+position,Toast.LENGTH_SHORT).show();
        // /Toast.makeText(this,temp.getText(),Toast.LENGTH_SHORT).show();
    }
}
