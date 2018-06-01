package arsic.aleksa.chatapplication;

import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;
import org.json.JSONException;

import java.io.IOException;

/*
 *  'Stub' is a class that implements the remote interface in a way that you can use it as if it were a local one.
 */
public class MyBinder extends IMyBinder.Stub {

    private static final String LOG_TAG = "FromBinder";

    /* Callback will be called when other context decide to call it */
    private IMyCallback mCallback;
    private CallbackCaller mCaller;
    private HttpHelper httpHelper = null;
    private String sessionID = null;

    @Override
    public boolean getNewMessages() throws RemoteException {
        if(httpHelper == null) httpHelper = new HttpHelper();
        if(sessionID == null){
            sessionID = ContactsActivity.sessionId;
        }

        Boolean result = null;

        try{
            result = httpHelper.getNewMessageBooleanFromURLService(MainActivity.SERVER_URL + MainActivity.FROMSERVICE, sessionID);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /* Remote exception - Parent exception for all Binder remote-invocation errors
    *  setCallback is a method from IMyCallback interface
    * */
    @Override
    public void setCallback(IMyCallback callback) throws RemoteException {
        mCallback = callback;
        mCaller = new CallbackCaller();
        mCaller.start();
    }

    public void stop(){
        mCaller.stop();
    }

    private class CallbackCaller implements Runnable{

        private static final long PERIOD = 5000L;

        private Handler handler = null;
        private boolean mRun = true;

        public void start(){
            /*  Looper
             *  Class used to run a message loop for a thread.
             *  Returns the application's main looper, which lives in the main thread of the application
             */
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(this, PERIOD);
        }

        public void stop(){
            mRun = false;
            handler.removeCallbacks(this);
        }

        @Override
        public void run() {

            if(!mRun) return;

            try{
                mCallback.onCallbackCall();
            }catch (NullPointerException e){

            }catch (RemoteException e){
                Log.e(LOG_TAG, "onCallbackCall failed", e);
            }

            handler.postDelayed(this, PERIOD);
        }
    }

}
