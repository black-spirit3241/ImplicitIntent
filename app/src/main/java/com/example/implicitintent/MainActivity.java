package com.example.implicitintent;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public static final int VIDEO_REQUEST_CODE=100;
    public static final int CONTACT_REQUEST_CODE=200;
    VideoView videoView;
    Button btnChooseVideo,btnChooseContact,btnAddEvent,btnSearch,btnSetTimer;
    Uri videoPathUri;

    TextView txtContactInfo;
    EditText searchQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView=findViewById(R.id.videoView);
        btnChooseVideo=findViewById(R.id.btnChooseVideo);
        btnChooseVideo.setOnClickListener(this);

        btnChooseContact=findViewById(R.id.btnChooseContact);
        txtContactInfo=findViewById(R.id.txtContactInfo);
        btnChooseContact.setOnClickListener(this);

        btnAddEvent=findViewById(R.id.btnAddEvent);
        btnAddEvent.setOnClickListener(this);

        searchQuery=findViewById(R.id.searchEdt);
        btnSearch=findViewById(R.id.btnWebSearch);
        btnSearch.setOnClickListener(this);

        btnSetTimer=findViewById(R.id.btnSetTimer);
        btnSetTimer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnChooseVideo:
                Intent intent=new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(intent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(intent,VIDEO_REQUEST_CODE);
                }
                break;
            case R.id.btnChooseContact:
                Intent contact=new Intent(Intent.ACTION_PICK);
                contact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                if(contact.resolveActivity(getPackageManager()) !=null){
                    startActivityForResult(contact,CONTACT_REQUEST_CODE);
                }
                break;
            case R.id.btnAddEvent:
                Intent eventIntent=new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.Events.TITLE,"Example Event")
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, Calendar.getInstance().getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME,Calendar.getInstance().getTimeInMillis()+(10 * 60 * 1000))
                        .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY,true);
                startActivity(eventIntent);
                break;
            case R.id.btnWebSearch:
                Intent webIntent=new Intent(Intent.ACTION_WEB_SEARCH);
                webIntent.putExtra(SearchManager.QUERY,searchQuery.getText().toString());
                if(webIntent.resolveActivity(getPackageManager()) !=null ){
                    startActivity(webIntent);
                }
                break;
            case R.id.btnSetTimer:
                Intent timer = new Intent(AlarmClock.ACTION_SET_TIMER)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, "Timer")
                        .putExtra(AlarmClock.EXTRA_LENGTH, 60)
                        .putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                if (timer.resolveActivity(getPackageManager()) != null) {
                    startActivity(timer);
                }
                break;
        }
    }

    public void PlayVideo(){
        videoView.setVideoURI(videoPathUri);
        videoView.setMediaController(new MediaController(this));
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==VIDEO_REQUEST_CODE && resultCode==RESULT_OK){
            videoPathUri=data.getData();
            PlayVideo();
        }
        else if(requestCode==CONTACT_REQUEST_CODE && resultCode == RESULT_OK){
            Uri contactUri=data.getData();
            String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};
            Cursor cursor=getContentResolver().query(contactUri,projection,null,null,null);
            if(cursor != null && cursor.moveToFirst()){
                String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                txtContactInfo.setText(name + " - " + phone);
            }
        }
    }
}
