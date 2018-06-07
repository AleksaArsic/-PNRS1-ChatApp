package arsic.aleksa.chatapplication;

public class MyNDK {

    static {
        System.loadLibrary("encryptDecryptData");
    }

    public native String encryptDecryptMessage(String message);

}
