package com.allyouneedapp.palpicandroid;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allyouneedapp.palpicandroid.utils.AppUtils;
import com.baoyz.actionsheet.ActionSheet;
import com.allyouneedapp.palpicandroid.adapters.AlbumAdapter;
import com.allyouneedapp.palpicandroid.models.Album;
import com.allyouneedapp.palpicandroid.models.ImageData;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.Firebase;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


import cn.jarlen.photoedit.utils.FileUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.CAMERA_WITH_DATA;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_EMAIL;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_GOOGLE_SIGN_IN;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_NAME;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_PHOTO;
import static com.allyouneedapp.palpicandroid.utils.Constants.KEY_UID;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_CAMERA;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE;
import static com.allyouneedapp.palpicandroid.utils.Constants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener, ActionSheet.ActionSheetListener , AppUtils.AppUtilsListener{

    public static final String CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera";
    public static final String CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME);

    //ImageButton
    private ImageButton btnAllbums;
    private ImageButton btnCamera;
    private ImageButton btnMore;
    private ImageButton btnContactUs;

    //ContactUs View
    private ImageButton btnSuggestSticker;
    private ImageButton btnFanpage;
    private ImageButton btnEmail;
    private ImageButton btnFollowUs;

    //More view
    private ImageButton btnTellFriend;
    private ImageButton btnSignOut;
    private ImageButton btnRate;

    //Album List
    private ListView listviewAlbums;

    //GridView
    private GridView gridView;

    //dim_image
    private ImageView dimBackground;

    //Layouts
    private RelativeLayout layoutMore;
    private RelativeLayout layoutContact;

    //register layout
    private RelativeLayout layoutRegister;
    private EditText tVEmail;
    private EditText tVPassword;
    private Button btnRegister;
    private Button btnLogin;
    private Button btnCancel;
    private ImageButton btnFaceBook;
    private ImageButton btnGoogle;

    //suggest sticker layout
    private RelativeLayout layoutSuggest;
    private EditText textSuggestTitle;
    private Button btnSuggest;
    private Button btnCancelSuggest;

    //camera pic
    private String photoPath = null, tempPhotoPath, camera_path;
    private File mCurrentPhotoFile;

    private int currentViewId = 0;

    //Albums Name and count of content
    ArrayList<Album> mAlbumsList;
    AlbumAdapter albumAdapter;
    ArrayList<String> filePaths = new ArrayList<>();

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        FacebookSdk.sdkInitialize(this);
        getSupportActionBar().hide();
        initViews();
        resetAllViews();
        initAllValiables();

        /**Getting permission in 23+ SDK version*/
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
            } else {
                getAlbumNames();
            }

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            } else {

            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            } else {

            }
        } else {
            getAlbumNames();
        }
        Collections.sort(mAlbumsList, new Comparator<Album>() {
            @Override
            public int compare(Album o1, Album o2) {
                int i = 0;
                i = o2.count - o1.count;
                return i;
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // get instance of firebase
        mAuth = FirebaseAuth.getInstance();
        getUserInformationFromFirebase();
    }

    private void initViews() {
        btnAllbums = (ImageButton) findViewById(R.id.main_bar_btn_album);
        btnAllbums.setOnClickListener(this);
        btnCamera = (ImageButton) findViewById(R.id.main_bar_btn_camera);
        btnCamera.setOnClickListener(this);
        btnMore = (ImageButton) findViewById(R.id.main_bar_btn_more);
        btnMore.setOnClickListener(this);
        btnContactUs = (ImageButton) findViewById(R.id.main_bar_btn_contact);
        btnContactUs.setOnClickListener(this);
        //contact us views
        btnSuggestSticker = (ImageButton) findViewById(R.id.btn_suggestSticker_main);
        btnSuggestSticker.setOnClickListener(this);
        btnFanpage = (ImageButton) findViewById(R.id.btn_fanpage);
        btnFanpage.setOnClickListener(this);
        btnEmail = (ImageButton) findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(this);
        btnFollowUs = (ImageButton) findViewById(R.id.btn_follow_us);
        btnFollowUs.setOnClickListener(this);
        //More views
        btnTellFriend = (ImageButton) findViewById(R.id.btn_tell_friend);
        btnTellFriend.setOnClickListener(this);
        btnSignOut = (ImageButton) findViewById(R.id.btn_sign_out);
        btnSignOut.setOnClickListener(this);
        btnRate = (ImageButton) findViewById(R.id.btn_rate);
        btnRate.setOnClickListener(this);
        //Album List
        listviewAlbums = (ListView) findViewById(R.id.listView_albumName);
        listviewAlbums.setOnItemClickListener(this);
        //
        gridView = (GridView) findViewById(R.id.gridView_album);
        gridView.setOnItemClickListener(this);
        //
        dimBackground = (ImageView) findViewById(R.id.dim_back);
        dimBackground.setOnClickListener(this);

        layoutMore = (RelativeLayout) findViewById(R.id.layout_more);
        layoutMore.setOnClickListener(this);
        layoutContact = (RelativeLayout) findViewById(R.id.layout_contact_us);
        layoutContact.setOnClickListener(this);

        //register layout
        layoutRegister = (RelativeLayout) findViewById(R.id.layout_register_login_main);
        tVEmail = (EditText) findViewById(R.id.text_email_main);
        tVPassword = (EditText) findViewById(R.id.text_password_main);
        btnRegister = (Button) findViewById(R.id.btn_register_main);
        btnRegister.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login_main);
        btnLogin.setOnClickListener(this);
        btnRegister = (Button) findViewById(R.id.btn_cancel_main);
        btnRegister.setOnClickListener(this);

        btnFaceBook = (ImageButton) findViewById(R.id.btn_fb_login_main);
//         Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>(){

                    @Override
                    public void onSuccess(LoginResult loginResult) {

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });

        btnGoogle = (ImageButton) findViewById(R.id.btn_google_main);
        btnGoogle.setOnClickListener(this);

        //suggest sticker layout
        layoutSuggest = (RelativeLayout) findViewById(R.id.layout_suggest_sticker_logged_main);
        textSuggestTitle = (EditText) findViewById(R.id.edit_text_suggest_sticker_main);

        btnSuggest = (Button) findViewById(R.id.btn_suggest_sticker_main);
        btnSuggest.setOnClickListener(this);
        btnCancelSuggest = (Button) findViewById(R.id.btn_suggest_sticker_cancel_main);
        btnCancelSuggest.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            } else {
                filePaths = getAllThumbPathWithAlbumId(CAMERA_IMAGE_BUCKET_ID);
                gridView.setAdapter(new ImageAdapter());
                resetAllViews();
            }
        } else {
            filePaths = getAllThumbPathWithAlbumId(CAMERA_IMAGE_BUCKET_ID);
            gridView.setAdapter(new ImageAdapter());
            resetAllViews();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

        //set auth of firebase listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void resetAllViews() {
        listviewAlbums.setVisibility(View.INVISIBLE);

        dimBackground.setVisibility(View.INVISIBLE);

        layoutContact.setVisibility(View.INVISIBLE);
        layoutMore.setVisibility(View.INVISIBLE);
        layoutRegister.setVisibility(View.INVISIBLE);
        layoutSuggest.setVisibility(View.GONE);

        btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
        btnMore.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
        btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
        currentViewId = 0;
    }

    private void initAllValiables() {
        mAlbumsList = new ArrayList<>();
    }


    @Override
    public void onClick(View v) {
        String email = tVEmail.getText().toString();
        String password = tVPassword.getText().toString();
        AppUtils utils = new AppUtils(this);
        utils.setAppUtilsListener(this);
        switch (v.getId()) {
            //tab bar
            case R.id.main_bar_btn_album:
                albumAdapter = new AlbumAdapter(this, this.mAlbumsList);
                this.listviewAlbums.setAdapter(albumAdapter);
                albumAdapter.notifyDataSetChanged();
                break;
            case R.id.main_bar_btn_camera:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        } else {
                            ActivityCompat.requestPermissions(this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    } else {
                        getPictureFormCamera();
                    }
                } else {
                    getPictureFormCamera();
                }
                break;
            case R.id.main_bar_btn_more:
                break;
            case R.id.main_bar_btn_contact:
                break;
            //contact us views
            case R.id.btn_suggestSticker_main:
                if ( utils.isSignIn()) {
                // User is signed in
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    // No user is signed in
                    layoutRegister.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_fanpage:
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
            case R.id.btn_email:
                /* Create the Intent */
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            case R.id.btn_follow_us:
                Uri uri = Uri.parse("http://instagram.com/user/username=palpic");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/")));
                }
                break;
            //more views
            case R.id.btn_tell_friend:
                //Action sheet -> FaceBook, Twitter, Email, cancel
                ActionSheet.createBuilder(this, getSupportFragmentManager())
                        .setCancelButtonTitle("Cancel")
                        .setOtherButtonTitles("FaceBook", "Twitter", "E-mail")
                        .setCancelableOnTouchOutside(true)
                        .setListener(this).show();
                break;
            case R.id.btn_sign_out:
                //Alert will be displayed.
                //check firebase sign in and show alert : success sign out or not sign-in yet.
                String _title = "PalPic";
                String _message = "You are not Sign-in";
                String _signOut = "Sign-Out success";
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in
                    mAuth.signOut();
                    alertWithTitle(_title,_signOut);
                } else {
                    // No user is signed in
                    alertWithTitle(_title, _message);
                }
                break;
            case R.id.btn_rate:
                //Alert will be displayed. -> SharePic
//                launchMarket();
                String title = "PalPic";
                String message = "If app is not yet on the store or is not intended for AppStore release then don't worry about this.";
                alertWithTitle(title, message);
                break;
            //register layout
            case R.id.btn_register_main:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    utils.registeWithEmail(email, password);
                }
                break;
            case R.id.btn_login_main:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    utils.loginToFirebase(email, password);
                }
                break;
            case R.id.btn_cancel_main:
                layoutRegister.setVisibility(View.GONE);
                break;
            case R.id.btn_fb_login_main:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.btn_google_main:
                //google login function
                // Configure Google Sign In
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();
                // Build a GoogleApiClient with access to the Google Sign-In API and the
                // options specified by gso.
                GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                        .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                            }
                        })
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, KEY_GOOGLE_SIGN_IN);LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
                break;
            case R.id.btn_suggest_sticker_main:
                //send message to firebase db.

                break;
            case R.id.btn_suggest_sticker_cancel_main:
                layoutSuggest.setVisibility(View.GONE);
                break;
            default:
                break;
        }
        setBottomBarStatus(v);
    }

    /**
     * get picture from camera
     */
    private void getPictureFormCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        tempPhotoPath = FileUtils.DCIMCamera_PATH + FileUtils.getNewFileName()
                + ".jpg";

        mCurrentPhotoFile = new File(tempPhotoPath);

        if (!mCurrentPhotoFile.exists()) {
            try {
                mCurrentPhotoFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(mCurrentPhotoFile));
        startActivityForResult(intent, CAMERA_WITH_DATA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case CAMERA_WITH_DATA:
                photoPath = tempPhotoPath;
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra("filePath", photoPath);
                startActivity(intent);
                break;
            case KEY_GOOGLE_SIGN_IN: //google login result
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    firebaseAuthWithGoogle(account);
                    layoutRegister.setVisibility(View.GONE);
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    /**
     * Bottom bar status setting
     **/
    private void setBottomBarStatus(View v) {
        if (v.getId() == currentViewId) {
            listviewAlbums.setVisibility(View.GONE);
            layoutContact.setVisibility(View.GONE);
            layoutMore.setVisibility(View.GONE);
            dimBackground.setVisibility(View.GONE);
            layoutRegister.setVisibility(View.GONE);

            btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
            btnMore.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
            btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
            currentViewId = 0;
            return;
        }

        btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
        btnMore.setBackgroundColor(getResources().getColor(R.color.colorBarMain));
        btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorBarMain));

        switch (v.getId()) {
            //tab bar
            case R.id.main_bar_btn_album:
                listviewAlbums.setVisibility(View.VISIBLE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.VISIBLE);

                btnAllbums.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            case R.id.main_bar_btn_camera:

                btnAllbums.setImageResource(R.drawable.albums);
                btnMore.setImageResource(R.drawable.more);
                btnContactUs.setImageResource(R.drawable.contactus);
                break;
            case R.id.main_bar_btn_more:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.VISIBLE);
                dimBackground.setVisibility(View.VISIBLE);

                btnMore.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            case R.id.main_bar_btn_contact:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.VISIBLE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.VISIBLE);

                btnContactUs.setBackgroundColor(getResources().getColor(R.color.colorBackgroundMain));
                break;
            default:
                listviewAlbums.setVisibility(View.GONE);
                layoutContact.setVisibility(View.GONE);
                layoutMore.setVisibility(View.GONE);
                dimBackground.setVisibility(View.GONE);
                break;
        }
        currentViewId = v.getId();
    }

    /**
     * Getting Album names from the device
     */
    private void getAlbumNames() {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ArrayList<String> ids = new ArrayList<String>();

        mAlbumsList.clear();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Album album = new Album();

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                album.id = cursor.getString(columnIndex);

                if (!ids.contains(album.id)) {
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
                    album.name = cursor.getString(columnIndex);

                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    album.thumbFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    album.coverID = cursor.getLong(columnIndex);

                    mAlbumsList.add(album);
                    ids.add(album.id);
                } else {
                    mAlbumsList.get(ids.indexOf(album.id)).count++;
                }
            }
            cursor.close();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAlbumNames();
                    filePaths = getAllThumbPathWithAlbumId(CAMERA_IMAGE_BUCKET_ID);
                    gridView.setAdapter(new ImageAdapter());
                    resetAllViews();
                } else {
                    String storageMessage = "App need to access to external storage.";
                    alertWithTitle("", storageMessage);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    String storageMessage = "App need to access to external storage to write data.";
                    alertWithTitle("", storageMessage);
                }
                break;
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    String storageMessage = "App need to access to camera.";
                    alertWithTitle("", storageMessage);
                }
                break;
        }
    }

    private ArrayList<String> getAllThumbPathWithAlbumId(String id) {
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        ArrayList<String> paths = new ArrayList<String>();

        if (cursor != null) {
            while (cursor.moveToNext()) {

                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
                String albumId = cursor.getString(columnIndex);

                if (id.equals(albumId)) {
                    String thumbFilePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
                    String filePaht = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    paths.add(filePaht);
                }
            }
            cursor.close();
        }
        return paths;
    }

    //GridView, ListView Item click listener.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gridView_album:
                Intent intent = new Intent(this, CropActivity.class);
                intent.putExtra("filePath", filePaths.get(position));
                startActivity(intent);
                break;
            case R.id.listView_albumName:
                filePaths = getAllThumbPathWithAlbumId(mAlbumsList.get(position).id);
                gridView.setAdapter(new ImageAdapter());
                resetAllViews();
                break;
            default:
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();

        //remove listener of firebase's auth
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Action sheet listener methods
     */
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        switch (index) {
            case 0: // Facebook
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                String facebookUrl = getFacebookPageURL(this);
                facebookIntent.setData(Uri.parse(facebookUrl));
                startActivity(facebookIntent);
                break;
            case 1: // Twitter
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    this.getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=USERID"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/palpic"));
                }
                startActivity(intent);
                break;
            case 2: // Email
                /* Create the Intent */
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);

                /* Fill it with Data */
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Text");

                /* Send it off to the Activity-Chooser */
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                break;
            default:
                break;
        }
    }

    public static String FACEBOOK_URL = "https://www.facebook.com/palpic";
    public static String FACEBOOK_PAGE_ID = "palpic";

    /***
     * method to get the right URL to use in the intent
     */
    public String getFacebookPageURL(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                return "fb://page/" + FACEBOOK_PAGE_ID;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }

    /**
     * To Rate this app on App store.
     **/
    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Alert Build
     */
    private void alertWithTitle(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    /**Log in function to firebase*/
    private void loginToFirebase(String email ,String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Sign-in failed",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registeWithEmail(String email ,String password ) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Sign-Up failed",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this,"Sign-Up Success",Toast.LENGTH_LONG).show();
                            tVEmail.setText(""); tVPassword.setText("");
                        }
                    }
                });
    }

    private void getUserInformationFromFirebase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            SharedPreferences.Editor pref = getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
            pref.putString(KEY_NAME,name);
            pref.putString(KEY_EMAIL,email);
//            pref.putString(KEY_PHOTO,photoUrl.toString());
            pref.putString(KEY_UID,uid);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Log-in success", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    /***********************************************/
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    /**Login status methods*/
    @Override
    public void onSuccess(AuthResult authResult,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            layoutSuggest.setVisibility(View.VISIBLE);
            layoutRegister.setVisibility(View.GONE);
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(MainActivity.this,"Sign-Up Success",Toast.LENGTH_LONG).show();
            tVEmail.setText(""); tVPassword.setText("");
        }
    }

    @Override
    public void onFailed(Exception e,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            Toast.makeText(MainActivity.this, "Sign-in failed"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(MainActivity.this,"Sign-Up failed" + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }
    /***********************************************/
    /**
     * Grid View Adapter for selected album list.
     */
    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return filePaths.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_grid_main, null);
                holder.imageview = (ImageView) convertView.findViewById(R.id.image_grid_main_item);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            Bitmap myBitmap = BitmapFactory.decodeFile(filePaths.get(position));
            holder.imageview.setImageBitmap(myBitmap);

            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
    }


}
