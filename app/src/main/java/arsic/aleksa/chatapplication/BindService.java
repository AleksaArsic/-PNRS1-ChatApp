package arsic.aleksa.chatapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * A Service is an application component representing either an application's desire
 * to perform a longer-running operation while not interacting with the user
 * or to supply functionality for other applications to use.
 */
public class BindService extends Service {

    private MyBinder mBinder = null;

    @Override
    public IBinder onBind(Intent intent) {

        if(mBinder == null) mBinder = new MyBinder();
        return mBinder;

    }

    @Override
    public boolean onUnbind(Intent intent) {
        mBinder.stop();
        return super.onUnbind(intent);
    }
}
