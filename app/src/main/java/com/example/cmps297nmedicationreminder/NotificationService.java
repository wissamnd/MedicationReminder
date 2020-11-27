package com.example.cmps297nmedicationreminder;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.cmps297nmedicationreminder.logic.DailyMedicationItem;
import com.example.cmps297nmedicationreminder.logic.Helper;
import com.example.cmps297nmedicationreminder.logic.LocalStorage;
import com.example.cmps297nmedicationreminder.logic.MedicationItem;
import com.example.cmps297nmedicationreminder.logic.OnceMedicationItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    Timer timer ;
    TimerTask timerTask ;
    String TAG = "Timers" ;
    int Your_X_SECS = 59 ;
    @Override
    public IBinder onBind (Intent arg0) {
        return null;
    }
    @Override
    public int onStartCommand (Intent intent , int flags , int startId) {
        Log. e ( TAG , "onStartCommand" ) ;
        super .onStartCommand(intent , flags , startId) ;
        startTimer() ;
        return START_STICKY ;
    }
    @Override
    public void onCreate () {
        Log.e ( TAG , "onCreate" ) ;
    }
    @Override
    public void onDestroy () {
        Log.e ( TAG , "onDestroy" ) ;
        stopTimerTask() ;
        super .onDestroy() ;
    }
    //we are going to use a handler to be able to run in our TimerTask
    final Handler handler = new Handler() ;
    public void startTimer () {
        timer = new Timer() ;
        initializeTimerTask() ;
        timer.schedule( timerTask , 1000 , Your_X_SECS * 1000 ) ; //
    }
    public void stopTimerTask () {
        if ( timer != null ) {
            timer.cancel() ;
            timer = null;
        }
    }
    public void initializeTimerTask () {
        timerTask = new TimerTask() {
            public void run () {
                handler .post( new Runnable() {
                    public void run () {
                        createNotification() ;
                    }
                }) ;
            }
        } ;
    }
    private void createNotification () {

        ArrayList<MedicationItem> medicationItems = getAllMedicationTheShouldBeTakenNow();

        for (int i = 0; i< medicationItems.size();i++){
            MedicationItem medicationItem = medicationItems.get(i);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE ) ;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext() , default_notification_channel_id ) ;
            mBuilder.setContentTitle( "Reminder" ) ;

            String body = "Take "+ medicationItem.numberOfPills +" Pill(s) of "+medicationItem.name;
            if(medicationItem.getInstruction().length() >0){
                body += " "+ medicationItem.getInstruction();
            }
            mBuilder.setContentText(body) ;
            mBuilder.setTicker(body) ;
            mBuilder.setSmallIcon(R.drawable. ic_launcher_foreground ) ;
            mBuilder.setAutoCancel( true ) ;
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
                int importance = NotificationManager.IMPORTANCE_HIGH ;
                NotificationChannel notificationChannel = new NotificationChannel( NOTIFICATION_CHANNEL_ID , "NOTIFICATION_CHANNEL_NAME" , importance) ;
                mBuilder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
                assert mNotificationManager != null;
                mNotificationManager.createNotificationChannel(notificationChannel) ;
            }
            assert mNotificationManager != null;
            mNotificationManager.notify(( int ) System.currentTimeMillis () , mBuilder.build()) ;

        }

    }

    // adds medications for the current selected date
    private ArrayList<MedicationItem> getAllMedicationTheShouldBeTakenNow(){
        Date currentDate = new Date();
        ArrayList<MedicationItem> medications_toTake = new ArrayList<>();
        ArrayList<MedicationItem> allMedications = new ArrayList<>();
        allMedications.addAll(LocalStorage.MEDICATION_ITEMS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);
        Collections.sort(allMedications);



        for (int i = 0 ; i < allMedications.size(); i++){
            MedicationItem medicationItem = allMedications.get(i);
            if(medicationItem instanceof OnceMedicationItem){
                OnceMedicationItem onceMedicationItem = (OnceMedicationItem) medicationItem;
                if(Helper.getDateDiffInMinutes(onceMedicationItem.date,currentDate) >= 0 && Helper.getDateDiffInMinutes(onceMedicationItem.date,currentDate) <= 1 && !onceMedicationItem.isSatisfied){
                    medications_toTake.add(onceMedicationItem);
                }
            }else if(medicationItem instanceof DailyMedicationItem) {
                DailyMedicationItem dailyMedicationItem = (DailyMedicationItem) medicationItem;

                if(dailyMedicationItem.hour == calendar.get(Calendar.HOUR) && (dailyMedicationItem.minutes - calendar.get(Calendar.MINUTE) > 0) && (dailyMedicationItem.minutes - calendar.get(Calendar.MINUTE) <= 1)&& !medicationItem.isSatisfied){
                    if(dailyMedicationItem.continuousTreatment && (Helper.getDateDiff(currentDate,dailyMedicationItem.startDate)< 24)){
                        medications_toTake.add(dailyMedicationItem);
                    }else if (!dailyMedicationItem.continuousTreatment && (Helper.getDateDiff(currentDate,dailyMedicationItem.startDate)< 24)
                            && (Helper.getDateDiff(currentDate,dailyMedicationItem.endDate) >= 0)){
                        medications_toTake.add(dailyMedicationItem);
                    }
                }
            }


        }
        System.out.println(medications_toTake);
        return medications_toTake;
    }
}
