package com.allyouneedapp.palpicandroid.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.allyouneedapp.palpicandroid.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Date;

/**
 * Created by Soul on 11/2/2016.
 */

public class AppUtils {
    public static final String KEY_LOGIN = "login";
    public static final String KEY_REGISTER = "register";
    private FirebaseAuth mAuth;
    private Context context;
    public AppUtils(Context context) {
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
    }

    public void loginToFirebase(String email ,String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                appUtilsListener.onSuccess(authResult, KEY_LOGIN);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                appUtilsListener.onFailed(e, KEY_LOGIN);
            }
        });
    }

    public void registeWithEmail(String email ,String password ) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                appUtilsListener.onSuccess(authResult, KEY_REGISTER);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                appUtilsListener.onFailed(e, KEY_REGISTER);
            }
        });
    }

    public boolean isSignIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return false;
        } else {
            return true;
        }
    }

    public AppUtilsListener appUtilsListener;

    public void setAppUtilsListener(AppUtilsListener listener){ this.appUtilsListener = listener;}

    public interface AppUtilsListener {
        public void onSuccess(AuthResult authResult,String status);
        public void onFailed(Exception e, String status);
    }

    /**
     * Instagram photo share methods
     */
    public void sharePhotoWithInstagram(){
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        final ContentResolver cr = context.getContentResolver();
        final String[] p1 = new String[] {
                MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.TITLE, MediaStore.Images.ImageColumns.DATE_TAKEN
        };
        Cursor c1 = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, p1, null, null, p1[1] + " DESC");

        if (c1.moveToFirst() ) {
            Log.i("Teste", "last picture (" + c1.getString(1) + ") taken on: " + new Date(c1.getLong(2)));
        }

        Log.i("Caminho download imagem", "file://"+Environment.getExternalStorageDirectory()+ "/Tubagram/"  + c1.getString(1) + ".png");

        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ Environment.getExternalStorageDirectory()+ "/Tubagram/" + c1.getString(1)+".png"));
        shareIntent.setPackage("com.instagram.android");

        c1.close();

        context.startActivity(shareIntent);
    }

    public boolean isInstalledInstagramApp(){
        boolean installed = false;

        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.instagram.android", 0);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public void createInstagramIntent(String type, String mediaPath){

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        context.startActivity(Intent.createChooser(share, "Share to"));
    }

    public void alertWithTitle(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


    public void shareImageWithEmail(String filePath){
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
//        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{strEmail});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+filePath));
        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

}
