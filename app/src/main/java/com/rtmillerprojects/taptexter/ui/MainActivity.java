package com.rtmillerprojects.taptexter.ui;

/**
 * Created by Ryan on 5/10/2016.
 */
import android.Manifest;
import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rtmillerprojects.taptexter.R;
import com.rtmillerprojects.taptexter.model.DatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, FragmentDialog.Communicator{

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 2;
    private static final int RESULT_PICK_CONTACT = 100;
    private ListView l;
    private ArrayList<String> dataSource = new ArrayList<>();
    private String[] defaultList = {"Hey, what's up?", "I'm on my way", "Good morning!", "Goodnight!", "Where are you?", "What are you up to?"};
    private ArrayAdapter<String> adapter;
    private DatabaseHelper tapTexterDb;

    /** Called when the activity is first created. */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        tapTexterDb = new DatabaseHelper(this);
        l = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataSource);
        l.setAdapter(adapter);
        l.setOnItemClickListener(this);
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter adapter = (ArrayAdapter)l.getAdapter();
                String deletedItem = dataSource.get(position);
                dataSource.remove(position); // you need to implement this method
                adapter.notifyDataSetChanged();
                boolean success = tapTexterDb.deleteData(deletedItem);
                Toast.makeText(MainActivity.this,"You have successfully deleted: "+deletedItem,Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        Cursor res = tapTexterDb.getData();
        if(res.getCount()==0){
            for(int i=0;i<defaultList.length;i++){
                dataSource.add(defaultList[i]);
                boolean success = tapTexterDb.insertData(defaultList[i]);
                //Toast.makeText(MainActivity.this,""+success,Toast.LENGTH_SHORT).show();
            }
            adapter.notifyDataSetChanged();
        }
        else{
            //populate datasource
            while(res.moveToNext()){
                dataSource.add(res.getString(1));
            }
        }


        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setHint("Type a message");
        //EditText editSmsRecipient = (EditText) findViewById(R.id.editSmsRecipient);
        //editSmsRecipient.setText("6023451108");

        ImageButton addBtn = (ImageButton) findViewById(R.id.btn_addMsg);
        addBtn.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {
                showDialog(view);
            }
        });

        ImageButton pickContact = (ImageButton) findViewById(R.id.btn_pickContact);
        pickContact.setOnClickListener(new View.OnClickListener(){
            @Override public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(in,RESULT_PICK_CONTACT);

            }
        });


        Button send = (Button) findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                boolean success;
                String msg = ((EditText) findViewById(R.id.editText)).getText().toString();
                String smsRecipient = ((EditText) findViewById(R.id.editSmsRecipient)).getText().toString();
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.SEND_SMS},MY_PERMISSIONS_REQUEST_SEND_SMS);

                }
                else{
                    success = sendSms(msg, smsRecipient);
                    if(success==false) {
                        Toast.makeText(MainActivity.this,"Need to enter a valid ph# & text before you can send",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        ((EditText) findViewById(R.id.editText)).setText("");
                    }
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Permissions are now obtained - try your message again",Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this,"Contacts permissions are now obtained",Toast.LENGTH_SHORT).show();
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
        EditText smsText = (EditText) findViewById(R.id.editText);
        smsText.setText(temp.getText().toString());
    }

    public void showDialog(View v){
        FragmentManager manager = getFragmentManager();
        FragmentDialog addDialog = new FragmentDialog();
        addDialog.show(manager, "addDialog");
    }

    @Override
    public void onDialogMessage(String message) {
        dataSource.add(message);
        adapter.notifyDataSetChanged();
        boolean success = tapTexterDb.insertData(message);
        Toast.makeText(this,"New message has been added.",Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null ;
            String name = null;
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
            // Set the value to the textviews
            EditText smsRecipient = (EditText) findViewById(R.id.editSmsRecipient);
            smsRecipient.setText(phoneNo);
            //textView2.setText(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean sendSms(String msg, String number){
        if(msg.length()<1 || number.length()<8){
            return false;
        }
        else{
            String smsBody = msg;
            SmsManager smsManager = SmsManager.getDefault();
            try{
                smsManager.sendTextMessage(number, null, smsBody, null, null);
                Toast.makeText(MainActivity.this,"Message has been sent",Toast.LENGTH_SHORT).show();
                return true;
            }
            catch(Error err){
                return false;
            }

        }
    }
}
