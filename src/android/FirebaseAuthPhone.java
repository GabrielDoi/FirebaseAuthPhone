package cordova.plugin.firebaseauthphone;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth.AuthStateListener;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.FirebaseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * This class echoes a string called from JavaScript.
 */
public class FirebaseAuthPhone extends CordovaPlugin implements OnCompleteListener, AuthStateListener{
    private static final String TAG = "FirebaseAuthentication";

    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider phoneAuthProvider;
    private CallbackContext signinCallback;
    private CallbackContext authStateCallback;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        Log.d(TAG, "Starting Firebase Authentication plugin");

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.phoneAuthProvider = PhoneAuthProvider.getInstance();
    }
    
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        
        if(action.equals("add")) {

            this.add(args, callbackContext);
            return true;

        }else if(action.equals("substract")) {

            this.substract(Integer.parseInt(args.getJSONObject(0).getString("param1")), Integer.parseInt(args.getJSONObject(0).getString("param2")), callbackContext);
            return true;

        }else if(action.equals("getIdToken")) {

            this.getIdToken(Boolean.parseBoolean(args.getJSONObject(0).getString("forceRefresh")), callbackContext);
            return true;

        }else if(action.equals("verifyPhoneNumber")) {

            this.verifyPhoneNumber(args.getJSONObject(0).getString("phoneNumber"), Long.parseLong(args.getJSONObject(0).getString("timeoutMillis")), callbackContext);
            return true;

        }else if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }


        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void add(JSONArray args, CallbackContext callback) {

        if(args != null) {

            try{
                int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
                int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));

                callback.success(""+ (p1+p2) );

            }catch(Exception ex){
                callback.error("Alguma coisa de errado: "+ ex);
            }

        }else{
            callback.error("Porfavor nao passe valores null");
        }
    }

    private void substract(int p1, int p2, CallbackContext callback) {

        try{
            //int p1 = Integer.parseInt(args.getJSONObject(0).getString("param1"));
            //int p2 = Integer.parseInt(args.getJSONObject(0).getString("param2"));

            callback.success(""+ (p1-p2) );

        }catch(Exception ex){
            callback.error("Alguma coisa de errado: "+ ex);
        }

    
    }

    //===========================================================================================
    //medodos que deve colocar no executar
    private void getIdToken(boolean forceRefresh, CallbackContext callbackContext) {
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.getIdToken(forceRefresh)
                .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<GetTokenResult>() {
                    @Override
                    public void onComplete(Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            callbackContext.success(task.getResult().getToken());
                        } else {
                            callbackContext.error(task.getException().getMessage());
                        }
                    }
                });
        }
    }

    //medodos que deve colocar no executar
    private void createUserWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        this.firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    //medodos que deve colocar no executar
    private void sendEmailVerification(final CallbackContext callbackContext) {
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if (user == null) {
            callbackContext.error("User is not authorized");
        } else {
            user.sendEmailVerification()
                .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            callbackContext.success();
                        } else {
                            callbackContext.error(task.getException().getMessage());
                        }
                    }
                });
        }
    }

    //medodos que deve colocar no executar
    private void sendPasswordResetEmail(String email, final CallbackContext callbackContext) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(cordova.getActivity(), new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        callbackContext.success();
                    } else {
                        callbackContext.error(task.getException().getMessage());
                    }
                }
            });
    }

    //medodos que deve colocar no executar
    private void signInAnonymously(CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    //medodos que deve colocar no executar
    private void signInWithEmailAndPassword(String email, String password, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    //medodos que deve colocar no executar
    private void signInWithGoogle(String idToken, String accessToken, CallbackContext callbackContext) {
        signInWithCredential(GoogleAuthProvider.getCredential(idToken, accessToken), callbackContext);
    }

    //medodos que deve colocar no executar
    private void signInWithFacebook(String accessToken, CallbackContext callbackContext) {
        signInWithCredential(FacebookAuthProvider.getCredential(accessToken), callbackContext);
    }

    //medodos que deve colocar no executar
    private void signInWithTwitter(String token, String secret, CallbackContext callbackContext) {
        signInWithCredential(TwitterAuthProvider.getCredential(token, secret), callbackContext);
    }

    private void signInWithCredential(final AuthCredential credential, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(cordova.getActivity(), this);
    }

    //medodos que deve colocar no executar
    private void signInWithVerificationId(String verificationId, String code, CallbackContext callbackContext) {
        this.signinCallback = callbackContext;

        signInWithPhoneCredential(PhoneAuthProvider.getCredential(verificationId, code));
    }

    //medodos que deve colocar no executar
    private void verifyPhoneNumber(String phoneNumber, long timeoutMillis, final CallbackContext callbackContext) {
        try{
            this.phoneAuthProvider.verifyPhoneNumber(phoneNumber, timeoutMillis, MILLISECONDS, cordova.getActivity(),
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        signInWithPhoneCredential(credential);
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        callbackContext.success(verificationId);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        callbackContext.error(e.getMessage());
                    }
                }
            );
        }catch(Exception ex){
            callbackContext.error("Alguma coisa de errado: "+ ex);
        }
    }

    private void signInWithPhoneCredential(PhoneAuthCredential credential) {
        FirebaseUser user = this.firebaseAuth.getCurrentUser();

        if (user == null) {
            this.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(cordova.getActivity(), FirebaseAuthPhone.this);
        } else {
            user.updatePhoneNumber(credential)
                .addOnCompleteListener(cordova.getActivity(), FirebaseAuthPhone.this);
        }
    }

    //medodos que deve colocar no executar
    private void signOut(CallbackContext callbackContext) {
        firebaseAuth.signOut();

        callbackContext.success();
    }

    //medodos que deve colocar no executar
    private void setLanguageCode(String languageCode, CallbackContext callbackContext) {
        if (languageCode == null) {
            this.firebaseAuth.useAppLanguage();
        } else {
            this.firebaseAuth.setLanguageCode(languageCode);
        }

        callbackContext.success();
    }

    //medodos que deve colocar no executar
    private void setAuthStateChanged(boolean disable, CallbackContext callbackContext) {
        this.authStateCallback = disable ? null : callbackContext;

        if (disable) {
            firebaseAuth.removeAuthStateListener(this);
        } else {
            firebaseAuth.addAuthStateListener(this);
        }
    }

    @Override
    public void onComplete(Task task) {
        if (this.signinCallback != null) {
            if (task.isSuccessful()) {
                this.signinCallback.success(getProfileData(firebaseAuth.getCurrentUser()));
            } else {
                this.signinCallback.error(task.getException().getMessage());
            }

            this.signinCallback = null;
        }
    }

    @Override
    public void onAuthStateChanged(FirebaseAuth auth) {
        if (this.authStateCallback != null) {
            PluginResult pluginResult;
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                pluginResult = new PluginResult(PluginResult.Status.OK, getProfileData(user));
            } else {
                pluginResult = new PluginResult(PluginResult.Status.OK, "");
            }

            pluginResult.setKeepCallback(true);
            this.authStateCallback.sendPluginResult(pluginResult);
        }
    }

    private static JSONObject getProfileData(FirebaseUser user) {
        JSONObject result = new JSONObject();

        try {
            result.put("uid", user.getUid());
            result.put("displayName", user.getDisplayName());
            result.put("email", user.getEmail());
            result.put("phoneNumber", user.getPhoneNumber());
            result.put("photoURL", user.getPhotoUrl());
            result.put("providerId", user.getProviderId());
        } catch (JSONException e) {
            Log.e(TAG, "Fail to process getProfileData", e);
        }

        return result;
    }
    //============================================================================================
}
