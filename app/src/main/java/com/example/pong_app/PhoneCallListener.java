package com.example.pong_app;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneCallListener extends PhoneStateListener {

    private GameView gameView;

    public PhoneCallListener(GameView gv){
        this.gameView = gv;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
            gameView.pause();
            //gameView.endGame();
        }

        /*
        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {


        }

        if (TelephonyManager.CALL_STATE_IDLE == state) {
            gameView.resume();

        }*/
    }

}
