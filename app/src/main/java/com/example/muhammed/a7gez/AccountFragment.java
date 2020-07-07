package com.example.muhammed.a7gez;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountFragment extends Fragment {


    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String EMAIL_USER = "EmailUser";
    private static final String FIRST_NAME_USER = "FirstNameUser";
    private static final String LAST_NAME_USER = "LastNameUser";
    private static final int RC_SIGN_IN = 99;
    private View view;
    private Context context;
    private Toolbar toolbar;
    private String toolbarTitle, email, phoneNumber, mVerificationId, mVerificationCode;
    private LinearLayout linearLayoutFaceBookAccount_EmailAddress, linearLayoutEmailAddress, linearLayoutNewAccount,
            linearLayoutPhoneNumber, linearLayoutVerificationCode;
    private ProgressBar progressBar;
    private EditText editTextEmail, editTextPassword, editTextVerifyPhoneNumber, editTextCode, editTextNewEmail, editTextNewPassword,
            editTextConfirmNewPassword, editTextFirstName, editTextLastName;
    private Button buttonLogin, buttonSendCode, buttonVerify, buttonSignUp;
    private LoginButton loginButtonFacebook;
    private CallbackManager mCallbackManager;
    private CountryCodePicker countryCodePicker;
    private TextView textViewMessage, textViewResendCode, textViewForgetPassword, textViewCreateAccount;
    private SignInButton signInGoogleButton;
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount googleSignInAccount;
    private Preferences preferences;

    private View.OnClickListener onClickListener;
    private FragmentAccountConnection connection;

    private FirebaseAuth firebaseAuth = null;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthCredential credential;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater.inflate(R.layout.fragment_account, container, false);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // initiate all views
         initControls();
    }


    private void initControls() {

        try {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(toolbarTitle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        linearLayoutFaceBookAccount_EmailAddress = (LinearLayout) view.findViewById(R.id.facebookAccount_emailAddress);
        linearLayoutEmailAddress = (LinearLayout) view.findViewById(R.id.emailAddress);
        linearLayoutNewAccount = (LinearLayout) view.findViewById(R.id.newAccount);
        linearLayoutPhoneNumber = (LinearLayout) view.findViewById(R.id.phoneNumber);
        linearLayoutVerificationCode = (LinearLayout) view.findViewById(R.id.verificationCode);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        editTextEmail = (EditText) view.findViewById(R.id.email);
        editTextPassword = (EditText) view.findViewById(R.id.password);
        buttonLogin = (Button)view.findViewById(R.id.login);
        loginButtonFacebook = (LoginButton) view.findViewById(R.id.login_facebook);
        signInGoogleButton = view.findViewById(R.id.sign_in_google);
        textViewForgetPassword = (TextView) view.findViewById(R.id.forgetPassword);
        textViewCreateAccount = (TextView) view.findViewById(R.id.createAccount);

        editTextNewEmail = (EditText) view.findViewById(R.id.newEmail);
        editTextNewPassword = (EditText) view.findViewById(R.id.newPassword);
        editTextConfirmNewPassword = (EditText) view.findViewById(R.id.confirmNewPassword);
        editTextFirstName = (EditText) view.findViewById(R.id.firstName);
        editTextLastName = (EditText) view.findViewById(R.id.lastName);
        buttonSignUp = (Button) view.findViewById(R.id.signUp);

        editTextVerifyPhoneNumber = (EditText) view.findViewById(R.id.verifyPhoneNumber);
        countryCodePicker = (CountryCodePicker) view.findViewById(R.id.countryCodePicker);
        buttonSendCode = (Button)view.findViewById(R.id.sendCode);
        textViewMessage = (TextView) view.findViewById(R.id.message);
        buttonVerify = (Button)view.findViewById(R.id.verify);
        textViewResendCode = (TextView) view.findViewById(R.id.resendCode);
        editTextCode = (EditText) view.findViewById(R.id.code);


        countryCodePicker.registerCarrierNumberEditText(editTextVerifyPhoneNumber);

        // create a callbackManager to handle login responses
        mCallbackManager  = CallbackManager.Factory.create();

        // connect to Authentication of firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Access a Cloud FireStore instance from your Activity
        firebaseFirestore = FirebaseFirestore.getInstance();

        connection = (FragmentAccountConnection) getActivity();

        preferences = new Preferences(context, PREFERENCES_DATA_USER);

        if(context.getClass().getSimpleName().equals(AccountActivity.class.getSimpleName())){
            toolbar.setVisibility(View.GONE);
        }

        // Set the dimensions of the sign-in button.
        signInGoogleButton.setSize(SignInButton.SIZE_STANDARD);

        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        loginButtonFacebook.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButtonFacebook.setFragment(this);
        loginButtonFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                progressBar.setVisibility(View.VISIBLE);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }
            @Override
            public void onCancel() {
                Toast.makeText(context, "cancel", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.sign_in_google:
                        signInWithGoogle();
                        break;

                    case R.id.login:
                        login();
                        break;

                    case R.id.forgetPassword:
                        resetPassword();
                        break;

                    case R.id.createAccount:
                        showLayoutRegistration();
                        break;

                    case R.id.sendCode:
                        sendVerificationCode();
                        break;

                    case R.id.verify:
                        setCredential();
                        break;

                    case R.id.resendCode:
                        resendVerificationCode();
                        break;

                    case R.id.signUp:
                        attemptSignUp();
                        break;
                    default:
                        break;
                }
            }
        };

        signInGoogleButton.setOnClickListener(onClickListener);
        buttonLogin.setOnClickListener(onClickListener);
        buttonSendCode.setOnClickListener(onClickListener);
        buttonVerify.setOnClickListener(onClickListener);
        textViewResendCode.setOnClickListener(onClickListener);
        textViewForgetPassword.setOnClickListener(onClickListener);
        textViewCreateAccount.setOnClickListener(onClickListener);
        buttonSignUp.setOnClickListener(onClickListener);
    }


    public void setActivityData(Context context, String toolbarTitle) {
        this.context = context;
        this.toolbarTitle = toolbarTitle;
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {

        this.googleSignInAccount = acct;
        mGoogleSignInClient.signOut();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
                            firebaseUser = task.getResult().getUser();
                            getGoogleDataUser(googleSignInAccount, firebaseUser);
                        }
                        else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            firebaseAuth.signOut();
                        }
                    }
                });
    }

    private void getGoogleDataUser(GoogleSignInAccount googleSignInAccount, final FirebaseUser firebaseUser){
        String UID = firebaseUser.getUid();
        String phoneNumber = firebaseUser.getPhoneNumber();
        String displayName = googleSignInAccount.getDisplayName();
        String fName = displayName.substring(0,displayName.indexOf(" "));
        String lName = displayName.substring(displayName.indexOf(" ")+1);
        String email = firebaseUser.getEmail();
        String imageProfilePath = null;
        try {
            imageProfilePath = googleSignInAccount.getPhotoUrl().getPath();
        } catch (Exception e) {
            Log.d("imagg",e.toString());
        }

        getUser(UID, email, fName, lName, imageProfilePath, phoneNumber);
    }

    private void handleFacebookAccessToken(final AccessToken token) {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = task.getResult().getUser();
                            Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
                            getFacebookDataUser(token, firebaseUser);

                        } else {
                            Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void getFacebookDataUser(AccessToken token, final FirebaseUser firebaseUser){

        GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {

                    String id = object.getString("id");
                    String imageProfilePath = new URL("https://graph.facebook.com/" + object.getString("id") +
                            "/picture?width=360&height=360").toString();
                    String fName = object.getString("first_name");
                    String lName = object.getString("last_name");
                    String email = object.getString("email");
                    String password = " ";
                    String imageProfileName = " ";

//                    Toast.makeText(context, "Receiving data user", Toast.LENGTH_LONG).show();

                    String UID = firebaseUser.getUid();
//                    String displayName = firebaseUser.getDisplayName();
                    String phoneNumber = firebaseUser.getPhoneNumber();
//                    fName = displayName.substring(0,displayName.indexOf(" "));
//                    lName = displayName.substring(displayName.indexOf(" ")+1);
//                    email = firebaseUser.getEmail();
//                    imageProfilePath = firebaseUser.getPhotoUrl().getPath();

                    // check user exist in firestore or not if yes call setPreferencesData method, if no call addUser method
                    // after adding the user will call setPreferencesData method
                    getUser(UID, email, fName, lName, imageProfilePath, phoneNumber);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                LoginManager.getInstance().logOut();
            }
        });

        Bundle bundleParameters = new Bundle();
        bundleParameters.putString("fields","id,email,first_name,last_name");
        graphRequest.setParameters(bundleParameters);
        graphRequest.executeAsync();


    }

    private void login(){

        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        // login by an email and password
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
//                    getAccountDataUser(task.getResult().getUser());
                    verifyEmailAddress();
                } else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    firebaseAuth.signOut();
                }
            }
        });

    }

    private void verifyEmailAddress(){
        // check status of your email is verified or not
        firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser.isEmailVerified()){
            Toast.makeText(context, "Login Successfully", Toast.LENGTH_LONG).show();
            getAccountDataUser(firebaseUser);
        }
        else {
            Toast.makeText(context, "Please verify your account !!", Toast.LENGTH_LONG).show();
            firebaseAuth.signOut();
        }
    }

    private void resetPassword(){
        // reset password by send message to your email and contain on link to click on it and write new password
        String email = editTextEmail.getText().toString().trim();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Check your email to reset password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void getAccountDataUser(final FirebaseUser firebaseUser){
        String UID = firebaseUser.getUid();
        String phoneNumber = firebaseUser.getPhoneNumber();
        String displayName = firebaseUser.getDisplayName();
        String fName = displayName.substring(0,displayName.indexOf(" "));
        String lName = displayName.substring(displayName.indexOf(" ")+1);
        String email = firebaseUser.getEmail();
        String imageProfilePath = "";
        try {
            imageProfilePath = firebaseUser.getPhotoUrl().getPath();
        } catch (Exception e) {
            Log.d("imagg",e.toString());
        }

        setPreferencesData(UID, email, fName, lName, imageProfilePath, phoneNumber);
    }

    private void getUser(final String id, final String email, final String fName, final String lName, final String imagePath, final String phoneNumber){

        firebaseFirestore.collection("users")
                .document(id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            if(task.getResult().getData() == null){
                                addUser(id, email, fName, lName, imagePath, phoneNumber);
                            }else {
                                setPreferencesData(id, email, fName, lName, imagePath, phoneNumber);
                            }

                        }else {
                            Toast.makeText(context, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    private void showLayoutRegistration(){
        linearLayoutFaceBookAccount_EmailAddress.setVisibility(View.GONE);
        linearLayoutNewAccount.setVisibility(View.VISIBLE);

    }

    private void attemptSignUp() {
        // Reset errors.
        editTextNewEmail.setError(null);
        editTextNewPassword.setError(null);
        editTextConfirmNewPassword.setError(null);
        editTextFirstName.setError(null);
        editTextLastName.setError(null);

        // Store values at the time of the login attempt.
        String email = editTextNewEmail.getText().toString().trim();
        String password = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmNewPassword.getText().toString().trim();
        String fName = editTextFirstName.getText().toString().trim();
        String lName = editTextLastName.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            editTextNewEmail.setError(getString(R.string.error_field_required));
            focusView = editTextNewEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            editTextNewEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextNewEmail;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            editTextNewPassword.setError(getString(R.string.error_field_required));
            focusView = editTextNewPassword;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            editTextNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = editTextNewPassword;
            cancel = true;
        }

        // Check for a valid confirm password, if the user entered one.
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextConfirmNewPassword.setError(getString(R.string.error_field_required));
            focusView = editTextConfirmNewPassword;
            cancel = true;
        }
        else if (!isPasswordValid(confirmPassword)) {
            editTextConfirmNewPassword.setError(getString(R.string.error_invalid_password));
            focusView = editTextConfirmNewPassword;
            cancel = true;
        }
        else if (!isPasswordConfirmValid(password, confirmPassword)) {
            editTextConfirmNewPassword.setError(getString(R.string.error_match_password));
            focusView = editTextConfirmNewPassword;
            cancel = true;
        }

        // Check for a valid first name.
        if (TextUtils.isEmpty(fName)) {
            editTextFirstName.setError(getString(R.string.error_field_required));
            focusView = editTextFirstName;
            cancel = true;
        }


        // Check for a valid last name.
        if (TextUtils.isEmpty(lName)) {
            editTextLastName.setError(getString(R.string.error_field_required));
            focusView = editTextLastName;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            createAccount(email, password, fName, lName);

        }


    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.length() > 10;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    private boolean isPasswordConfirmValid(String password, String passwordConfirm) {
        return passwordConfirm.equals(password);
    }

    private void createAccount(final String email, String password, final String fName, final String lName){
        // create account by an email and a password
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // verify your email by send Verification message contain on link to click on it
                    sendEmailVerification(email, fName, lName);
                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendEmailVerification(final String email, final String fName, final String lName){
        // define language of Verification message
        firebaseAuth.setLanguageCode("ar");
        firebaseUser = firebaseAuth.getCurrentUser();
        // send message to your email
        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Created Account", Toast.LENGTH_LONG).show();

                    // change your display name and profile picture
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(fName + " " + lName)
                            .build();

                    firebaseUser.updateProfile(profileUpdates)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String id = firebaseUser.getUid();
                                        addUser(id, email, fName, lName, "", null);
                                    }
                                }
                            });
                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void addUser(final String id, final String email, final String fName, final String lName, final String imagePath, final String phoneNumber){

        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("firstName", fName);
        user.put("lastName", lName);
        user.put("email", email);
        user.put("imagePath", imagePath);
        user.put("phoneNumber", phoneNumber);

        firebaseFirestore.collection("users")
                .document(id)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            setPreferencesData(id, email, fName, lName, imagePath, phoneNumber);
                        }
                    }
                });

//        db.document("users/" + lastNameView.getText().toString().trim())
//                .set(user)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(CloudFirestoreActivity.this, "User Added", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
    }

    private void setPreferencesData(String id, String email, String fName, String lName, String imagePath, String phoneNumber){
        progressBar.setVisibility(View.GONE);
        
        // can replace all these data to firebaseUser
        preferences.setDataUser(id, email, fName, lName, imagePath);
        preferences.setDataUserExist(true);

        if(phoneNumber == null){
            linearLayoutFaceBookAccount_EmailAddress.setVisibility(View.GONE);
            linearLayoutNewAccount.setVisibility(View.GONE);
            linearLayoutPhoneNumber.setVisibility(View.VISIBLE);
        }else {
            preferences.setPhoneNumber(phoneNumber);
        }

        connection.setDataUser(email, fName, lName, imagePath, phoneNumber);

    }

    private void sendVerificationCode(){
        phoneNumber = countryCodePicker.getFullNumberWithPlus();

        firebaseAuth.setLanguageCode("ar");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks);

    }

    private void resendVerificationCode(){
        phoneNumber = countryCodePicker.getFullNumberWithPlus();

        firebaseAuth.setLanguageCode("ar");

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                mCallbacks,
                mResendToken);
    }

    private void addPhoneNumberUser(PhoneAuthCredential credential){
        firebaseUser.updatePhoneNumber(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_LONG).show();
                    preferences.setPhoneNumber(phoneNumber);
                    connection.closeFragment();
                }else {
                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void showLayoutVerification(){
        textViewMessage.setText("We've sent you an SMS code to " + phoneNumber + " to verify and complete your account.");

        linearLayoutPhoneNumber.setVisibility(View.GONE);
        linearLayoutVerificationCode.setVisibility(View.VISIBLE);
    }

    private void setCredential(){
        credential = PhoneAuthProvider.getCredential(mVerificationId, editTextCode.getText().toString().trim());
        addPhoneNumberUser(credential);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                fireBaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
//                Toast.makeText(context, "Verification Completed", Toast.LENGTH_LONG).show();

                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

//                alertDialog2.dismiss();
                // update current user by set your phone number
                addPhoneNumberUser(phoneAuthCredential);

                mVerificationCode = phoneAuthCredential.getSmsCode();

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(context, "Verification Failed\n" + e, Toast.LENGTH_LONG).show();

                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.

                Toast.makeText(context, "Code Sent", Toast.LENGTH_LONG).show();

                // Save verification ID and resending token so we can use them later
                mVerificationId = s;
                // add this line to save the resend token
                // If users fail to receive the verification code SMS, they will want to resend the verification SMS.
                // To implement this, we need to call the verifyPhoneNumber method again
                mResendToken = forceResendingToken;
                showLayoutVerification();
//                alertDialog.dismiss();
//                alertDialog2.show();
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(context, "Code Auto Retrieval TimeOut", Toast.LENGTH_LONG).show();
            }
        };

    }
}
