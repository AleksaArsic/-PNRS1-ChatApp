// IMyBinder.aidl
package arsic.aleksa.chatapplication;

// Declare any non-default types here with import statements
import arsic.aleksa.chatapplication.IMyCallback;

// Client and service use AIDL interfaces

interface IMyBinder {

   boolean getNewMessages();

   void setCallback(in IMyCallback callback);


}
