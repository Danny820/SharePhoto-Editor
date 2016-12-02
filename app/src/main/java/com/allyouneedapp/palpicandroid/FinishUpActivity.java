package com.allyouneedapp.palpicandroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allyouneedapp.palpicandroid.database.PalPicDBHandler;
import com.allyouneedapp.palpicandroid.utils.AppUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.google.firebase.auth.AuthResult;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.allyouneedapp.palpicandroid.adapters.ColorBarAdapter;
import com.allyouneedapp.palpicandroid.adapters.FontAdapter;
import com.allyouneedapp.palpicandroid.adapters.RecentStickerAdapter;
import com.allyouneedapp.palpicandroid.adapters.StickerGridViewAdapter;
import com.allyouneedapp.palpicandroid.adapters.StickerTitleAdapter;
import com.allyouneedapp.palpicandroid.listeners.RecyclerOnItemClickListener;
import com.allyouneedapp.palpicandroid.models.Sticker;
import com.allyouneedapp.palpicandroid.utils.StickerUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

import cn.jarlen.photoedit.operate.ImageObject;
import cn.jarlen.photoedit.operate.OperateUtils;
import cn.jarlen.photoedit.operate.OperateView;
import cn.jarlen.photoedit.operate.TextObject;
import cn.jarlen.photoedit.utils.FileUtils;

import static com.allyouneedapp.palpicandroid.utils.Constants.REQUEST_FINISH_FILTER;

public class FinishUpActivity extends AppCompatActivity implements View.OnClickListener , ListView.OnItemClickListener,AppUtils.AppUtilsListener {

    public static Bitmap bitmap;

    //views
    private Button btnBack;
    private Button btnSaveShare;
    private Button btnDismiss;

    private LinearLayout mLayout;

    //bottom bar
    private ImageButton btnFilter;
    private ImageButton btnAddSticker;
    private ImageButton btnAddWord;
    private ImageButton btnFont;

    private OperateView operateView;
    private OperateUtils operateUtils;

    //search bar
    private SearchView searchView;
    private ListView listView;
    private GridView gridView;

    private LinearLayout layoutList;
    private LinearLayout layoutFontColorBar;
    private LinearLayout layoutRecentSticker;

    private RecyclerView recyclerViewColor;
    private RecyclerView recyclerViewRecent;

    private ArrayList<Sticker> stickers;
    private ArrayList<Sticker> searchedStickers;
    private ArrayList<Sticker> recentStickers;
    private RecentStickerAdapter recentStickerAdapter;
    private StickerTitleAdapter stickerTitleAdapter;
    private ArrayList<Sticker> stickersforGrid;
    private StickerGridViewAdapter stickerGridViewAdapter;

    //input text layout
    private RelativeLayout layoutInputText;
    private Button btnInputTextOK;
    private EditText inputText;

    //register and login layout
    private RelativeLayout layoutRegisterLogin;
    private EditText textEmail;
    private EditText textPassword;
    private Button btnRegister;
    private Button btnLogin;
    private Button btnCancel;
    private ImageButton btnFBLogin;
    private ImageButton btnGoogleLogin;

    //suggest sticker layout
    private  RelativeLayout layoutSuggest;
    private EditText textSuggestTitle;
    private Button btnSuggest;
    private Button btnCancelSuggest;

    //save and share layout
    private RelativeLayout layoutSaveAndShare;
    private ImageButton btnFB;
    private ImageButton btnInstar;
    private ImageButton btnEmail;
    private ImageButton btnSuggestSticker;
    private ImageButton btnStartOver;

    private int[] colorItems = {
            R.color.colorItem1,R.color.colorItem2,R.color.colorItem3,R.color.colorItem4,
            R.color.colorItem5,R.color.colorItem6,R.color.colorItem7,R.color.colorItem8,
            R.color.colorItem9,R.color.colorItem10,R.color.colorItem11,R.color.colorItem12,
            R.color.colorItem13,R.color.colorItem14,R.color.colorItem15,R.color.colorItem16,
            R.color.colorItem17,R.color.colorItem18,R.color.colorItem19,R.color.colorItem20,
            R.color.colorItem21,R.color.colorItem22,R.color.colorItem23,R.color.colorItem24,
            R.color.colorItem25,R.color.colorItem26,R.color.colorItem27,R.color.colorItem28,
            R.color.colorItem29,R.color.colorItem30,R.color.colorItem31,R.color.colorItem32,
            R.color.colorItem33,R.color.colorItem34,R.color.colorItem35,R.color.colorItem36,
            R.color.colorItem37,R.color.colorItem38,R.color.colorItem39,R.color.colorItem40,
            R.color.colorItem41,R.color.colorItem42,R.color.colorItem43,R.color.colorItem44,
            R.color.colorItem45,R.color.colorItem46,R.color.colorItem47,R.color.colorItem48,
            R.color.colorItem49,R.color.colorItem50,R.color.colorItem51,R.color.colorItem52,
            R.color.colorItem53,R.color.colorItem54,R.color.colorItem55,R.color.colorItem56,
            R.color.colorItem57,R.color.colorItem58,R.color.colorItem59,R.color.colorItem60,
            R.color.colorItem61};
    private ArrayList<String> fontNames = new ArrayList<>();
    private ArrayList<Typeface> typefaces = new ArrayList<>();

    private ColorBarAdapter colorBarAdapter;
    private FontAdapter fontAdapter;
    private int selectedColor = R.color.colorItem31;
    private String selectedFont = "Guseul";
    private Typeface selectedTypeface = Typeface.DEFAULT;

    // to edit sticker and textobject to operateview.
    private TextObject selectedTextObject;
    private ImageObject selectedImageObject;
    private boolean isTextEdit = false;
    private boolean isImageSticker = false;

    //to control the bottom bar status
    private int currentSelectedBtnId = 0;

    public String filePath;

    private ArrayList<ImageObject> addedImageObjects = new ArrayList<>();

    //database
    private PalPicDBHandler db;

    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //progress hud
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_up);
        getSupportActionBar().hide();
        filePath = getIntent().getExtras().getString("filePath");
        initAllViews();
        fillContent();
        fontNames.add("Bandal");
        fontNames.add("Bangwool");
        fontNames.add("drfont_daraehand_Basic");
        fontNames.add("Eunjin");
        fontNames.add("EunjinNakseo");
        fontNames.add("Guseul");
        fontNames.add("TH Baijam Bold");
        fontNames.add("TH Chakra Petch Bold");
        fontNames.add("TH Charm of AU");
        fontNames.add("TH Charmonman Bold");
        fontNames.add("TH Fahkwang Bold");
        fontNames.add("TH K2D July8 Bold");
        fontNames.add("TH Kodchasal Bold");
        fontNames.add("TH Krub Bold");
        fontNames.add("TH Mali Grade6 Bold");
        fontNames.add("TH Niramit AS Bold");
        fontNames.add("TH Srisakdi Bold");
        fontNames.add("THSarabun Bold");
        fontNames.add("YOzFonNL");

        //Facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        hud = new KProgressHUD(this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Optimizing App")
                .setCancellable(false)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f);
        hud.show();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                loadAllStickers();
                hud.dismiss();
            }
        });

        setViewsStatus(0); //init views status

        colorBarAdapter = new ColorBarAdapter(this, colorItems);
        recyclerViewColor.setAdapter(colorBarAdapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerViewColor.setLayoutManager(gridLayoutManager);

        recyclerViewColor.addOnItemTouchListener(new RecyclerOnItemClickListener(this, new RecyclerOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedColor = colorItems[position];
                int color = ContextCompat.getColor(FinishUpActivity.this , selectedColor);
                if (isImageSticker) {
                    Bitmap src = selectedImageObject.getSrcBm();
                    Bitmap tintBitmap = tintImage(src,color);
                    selectedImageObject.setSrcBm(tintBitmap);
                } else {
                    selectedTextObject.setColor(color);
                    selectedTextObject.commit();
                    selectedTextObject.regenerateBitmap();
                }
                operateView.invalidate();
            }
        }));

        StaggeredGridLayoutManager recentgridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL);
        recyclerViewRecent.setLayoutManager(recentgridLayoutManager);
        recyclerViewRecent.addOnItemTouchListener(new RecyclerOnItemClickListener(this, new RecyclerOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                gridView.setVisibility(View.GONE);
                Bitmap bitmap = StickerUtil.getBitmapFromAsset(FinishUpActivity.this, recentStickers.get(position).filePath);
                addpic(bitmap);
                // save to recent data
                db.insertToRecent(recentStickers.get(position));
                recentStickers.clear();
                recyclerViewRecent.removeAllViews();
                recentStickers = db.getAllRecent();
                recentStickerAdapter = new RecentStickerAdapter(FinishUpActivity.this, recentStickers);
                recyclerViewRecent.setAdapter(recentStickerAdapter);
                recyclerViewRecent.invalidate();
                recentStickerAdapter.notifyDataSetChanged();
            }
        }));

         typefaces = getFontTypesFromAsset();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**get all views from xml*/
    private void initAllViews(){
        btnBack = (Button) findViewById(R.id.btn_back_finish);
        btnBack.setOnClickListener(this);
        btnSaveShare = (Button) findViewById(R.id.btn_save_share_finish);
        btnSaveShare.setOnClickListener(this);
        btnDismiss = (Button) findViewById(R.id.btn_dismiss_finish);
        btnDismiss.setOnClickListener(this);

        mLayout = (LinearLayout) findViewById(R.id.layout_edit_finish);

        btnFilter = (ImageButton) findViewById(R.id.btn_filter_finish);
        btnFilter.setOnClickListener(this);
        btnAddSticker = (ImageButton) findViewById(R.id.btn_addsticker_finish);
        btnAddSticker.setOnClickListener(this);
        btnAddWord = (ImageButton) findViewById(R.id.btn_add_word_finish);
        btnAddWord.setOnClickListener(this);
        btnFont = (ImageButton) findViewById(R.id.btn_bar_font_finish);
        btnFont.setOnClickListener(this);

        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Here");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchedStickers.clear();
                for (Sticker item : stickers) {
                    if (item.title.contains(newText)){
                        searchedStickers.add(item);
                    }
                }
                stickerTitleAdapter = new StickerTitleAdapter(FinishUpActivity.this,searchedStickers);
                listView.setAdapter(stickerTitleAdapter);
                return false;
            }
        });




        listView = (ListView) findViewById(R.id.listView_font);
        listView.setOnItemClickListener(this);
        gridView = (GridView) findViewById(R.id.gridView_sticker);
        gridView.setOnItemClickListener(this);

        layoutList = (LinearLayout) findViewById(R.id.layout_list);
        layoutFontColorBar = (LinearLayout) findViewById(R.id.layout_font_color_finish);
        layoutRecentSticker = (LinearLayout) findViewById(R.id.layout_recent_finish);

        recyclerViewColor = (RecyclerView) findViewById(R.id.bar_color_finish);
        recyclerViewRecent = (RecyclerView) findViewById(R.id.bar_recent_finish);
        operateUtils = new OperateUtils(this);

        //input text layout
        layoutInputText = (RelativeLayout) findViewById(R.id.layout_input_text);
        inputText = (EditText) findViewById(R.id.text_input_text);
        inputText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    layoutInputText.setVisibility(View.GONE);
                    currentSelectedBtnId = 0;
                }
            }
        });
        inputText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (inputText.isFocused()) {
                        Rect outRect = new Rect();
                        inputText.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                            inputText.clearFocus();
                            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                            layoutInputText.setVisibility(View.GONE);
                            currentSelectedBtnId = 0;
                        }
                    }
                }
                return false;
            }
        });
        btnInputTextOK = (Button)findViewById(R.id.btn_input_text_ok);
        btnInputTextOK.setOnClickListener(this);

        //register and login layout
        layoutRegisterLogin = (RelativeLayout) findViewById(R.id.layout_register_login_finish);
        textEmail = (EditText) findViewById(R.id.text_email_finish);
        textPassword = (EditText)findViewById(R.id.text_password_finish);

        btnRegister = (Button) findViewById(R.id.btn_register_finish);
        btnRegister.setOnClickListener(this);
        btnLogin = (Button) findViewById(R.id.btn_login_finish);
        btnLogin.setOnClickListener(this);
        btnCancel = (Button) findViewById(R.id.btn_cancel_finish);
        btnCancel.setOnClickListener(this);
        btnFBLogin = (ImageButton) findViewById(R.id.btn_fb_login_finish);
        btnFBLogin.setOnClickListener(this);
        btnGoogleLogin = (ImageButton) findViewById(R.id.btn_google_finish);
        btnGoogleLogin.setOnClickListener(this);

        //suggest sticker layout
        layoutSuggest = (RelativeLayout) findViewById(R.id.layout_suggest_sticker_logged_finish);
        textSuggestTitle = (EditText) findViewById(R.id.edit_text_suggest_sticker_finish);

        btnSuggest = (Button) findViewById(R.id.btn_suggest_sticker_finish);
        btnSuggest.setOnClickListener(this);
        btnCancelSuggest = (Button) findViewById(R.id.btn_suggest_sticker_cancel_finish);
        btnCancelSuggest.setOnClickListener(this);

        //save and share layout
        layoutSaveAndShare = (RelativeLayout) findViewById(R.id.layout_save_share_finish);

        btnFB = (ImageButton) findViewById(R.id.btn_fb_finish);
        btnFB.setOnClickListener(this);
        btnInstar = (ImageButton) findViewById(R.id.btn_insta_finish);
        btnInstar.setOnClickListener(this);
        btnEmail = (ImageButton) findViewById(R.id.btn_email_finish);
        btnEmail.setOnClickListener(this);
        btnSuggestSticker = (ImageButton) findViewById(R.id.btn_suggestSticker_finish);
        btnSuggestSticker.setOnClickListener(this);
        btnStartOver = (ImageButton) findViewById(R.id.btn_start_over_finish);
        btnStartOver.setOnClickListener(this);


    }

    /**Load all stickers from json file and db*/
    private void loadAllStickers(){

        db = new PalPicDBHandler(this);
        stickers = new ArrayList<>();
        searchedStickers = new ArrayList<>();
        recentStickers = new ArrayList<>();
        stickersforGrid = new ArrayList<>();

        SharedPreferences prefs = getSharedPreferences("palpic", Context.MODE_PRIVATE);
        boolean check = prefs.getBoolean("isAdded", false);


        if (!check) {
            // get stickers from the asset
            String json = loadJSONFromAsset();
            stickers = jsonParser(json);
            searchedStickers = (ArrayList<Sticker>) stickers.clone();
            // save stickers to DB
            for (Sticker item : stickers) {
                db.insert(item);
            }

            SharedPreferences.Editor pref = getSharedPreferences("palpic", Context.MODE_PRIVATE).edit();
            pref.putBoolean("isAdded",true);
            pref.commit();
        } else {
            stickers = db.getAll();
            searchedStickers = (ArrayList<Sticker>) stickers.clone();
        }

        recentStickers = db.getAllRecent();
        if (!recentStickers.isEmpty()) {
            recentStickerAdapter = new RecentStickerAdapter(this, recentStickers);
            recyclerViewRecent.setAdapter(recentStickerAdapter);
        }
    }


    @Override
    public void onClick(View v) {

        String email = textEmail.getText().toString();
        String password = textPassword.getText().toString();
        AppUtils utils = new AppUtils(this);
        utils.setAppUtilsListener(this);

        switch (v.getId()) {
            case R.id.btn_back_finish:
                finish();
                break;
            case R.id.btn_save_share_finish:
                FileUtils.writeImage(getBitmapByView(operateView),getFilePathWithTimeStump(),100);
                setViewsStatus(0);
                layoutSaveAndShare.setVisibility(View.VISIBLE);
                btnSaveShare.setVisibility(View.GONE);
                btnDismiss.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_dismiss_finish:
                setViewsStatus(0);
                break;
            case R.id.btn_filter_finish:
                addedImageObjects.clear();
                addedImageObjects = (ArrayList<ImageObject>) operateView.imgLists;
                Intent intent = new Intent(this, FilterActivity.class);
                intent.putExtra("filter", true);
                startActivityForResult(intent, REQUEST_FINISH_FILTER);

                break;
            case R.id.btn_addsticker_finish:        // add sticker button
                if(currentSelectedBtnId == R.id.btn_addsticker_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                setViewsStatus(-1);
                stickerTitleAdapter = new StickerTitleAdapter(this,searchedStickers);
                listView.setAdapter(stickerTitleAdapter);
                searchView.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                layoutList.setVisibility(View.VISIBLE);

                break;
            case R.id.btn_add_word_finish:  //add word button
                if(currentSelectedBtnId == R.id.btn_add_word_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                isTextEdit = false;
                setViewsStatus(-1);
                layoutInputText.setVisibility(View.VISIBLE);
                inputText.setText("");
                break;
            case R.id.btn_bar_font_finish:
                if(currentSelectedBtnId == R.id.btn_bar_font_finish){
                    setViewsStatus(0);
                    currentSelectedBtnId = 0;
                    return;
                }
                setViewsStatus(-1);
                fontAdapter = new FontAdapter(this,selectedTextObject.getText(),typefaces);
                listView.setAdapter(fontAdapter);
                searchView.setVisibility(View.GONE);
                layoutList.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_input_text_ok:
                if (isTextEdit){
                    editText(selectedTextObject);
                } else {
                    addword();
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
                break;
            //register and login layout
            case R.id.btn_register_finish:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    utils.registeWithEmail(email, password);
                }
                break;
            case R.id.btn_login_finish:
                if (email.isEmpty()||password.isEmpty()){
                    Toast.makeText(this,"Insert E-mail and Password", Toast.LENGTH_LONG).show();
                } else {
                    utils.loginToFirebase(email, password);
                }
                break;
            case R.id.btn_cancel_finish:
                setViewsStatus(-1);
                break;
            case R.id.btn_fb_login_finish:

                break;
            case R.id.btn_google_finish:

                break;
            //suggest sticker layout
            case R.id.btn_suggest_sticker_finish:

                break;
            case R.id.btn_suggest_sticker_cancel_finish:

                setViewsStatus(-1);
                break;
            //Save and share layout
            case R.id.btn_fb_finish:
                SharePhoto sharePhoto2 = new SharePhoto.Builder()
                        .setBitmap(getBitmapByView(operateView))
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto2)
                        .build();
                if (ShareDialog.canShow(SharePhotoContent.class)) {
                    shareDialog.show(this,content);
                }
                break;
            case R.id.btn_insta_finish:
                String type = "image/*";
                if (utils.isInstalledInstagramApp()) {
                    utils.createInstagramIntent( type,getFilePathWithTimeStump());
                } else {
                    utils.alertWithTitle("Palpic","Couldn't find Instagram App");
                }
                break;
            case R.id.btn_email_finish:
                utils.shareImageWithEmail(getFilePathWithTimeStump());
                break;
            case R.id.btn_suggestSticker_finish:
                setViewsStatus(0);
                if (utils.isSignIn()){
                    layoutSuggest.setVisibility(View.VISIBLE);
                } else {
                    layoutRegisterLogin.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_start_over_finish:
                Intent startOver = new Intent(this, MainActivity.class);
                startOver.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startOver);
                break;
            default:
                setViewsStatus(0);
                break;
        }
        currentSelectedBtnId = v.getId();
    }


    /** Set init views status*/
    private void setViewsStatus(int status){

        listView.setVisibility(View.GONE);
        gridView.setVisibility(View.GONE);
        btnDismiss.setVisibility(View.GONE);
        layoutList.setVisibility(View.GONE);
        layoutFontColorBar.setVisibility(View.GONE);
        layoutInputText.setVisibility(View.GONE);
        layoutSaveAndShare.setVisibility(View.GONE);
        layoutSuggest.setVisibility(View.GONE);
        layoutRegisterLogin.setVisibility(View.GONE);
        btnSaveShare.setVisibility(View.VISIBLE);
        layoutRecentSticker.setVisibility(View.GONE);

        switch (status) {
            case 0: // init status
                btnSaveShare.setVisibility(View.VISIBLE);
                layoutRecentSticker.setVisibility(View.VISIBLE);
                break;
            case 1: //image editing
                layoutFontColorBar.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.GONE);
                break;
            case 2: //text object editing
                layoutFontColorBar.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.VISIBLE);
                layoutInputText.setVisibility(View.VISIBLE);
                btnFont.setVisibility(View.VISIBLE);
                break;
            case 3: // register login status
                layoutRegisterLogin.setVisibility(View.VISIBLE);
                break;
            case 4: // suggest status
                layoutSuggest.setVisibility(View.VISIBLE);
                break;
            case 5: //save and share status
                layoutSaveAndShare.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**Init operate view to start edit photo*/
    private void fillContent()
    {
        operateView = new OperateView(FinishUpActivity.this, bitmap);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                bitmap.getWidth(), bitmap.getHeight());
        operateView.setLayoutParams(layoutParams);
        mLayout.addView(operateView);
        operateView.setMultiAdd(true);
        operateView.setOnListener(new OperateView.MyListener()
        {
            public void onClick(TextObject tObject)
            {
                isTextEdit = true;
                isImageSticker = false;
                inputText.setText(tObject.getText());
                setViewsStatus(2);
                inputText.setFocusable(true);
                selectedTextObject = tObject;
            }

            @Override
            public void onOutSideClick(TextObject textObject) {
                if (selectedTextObject != null) {
                    if (textObject.equals(selectedTextObject)) {
                        isTextEdit = false;
                        textObject.setSelected(false);
                        selectedTextObject = null;
                        setViewsStatus(0);
                    }
                }
            }

            @Override
            public void onImageObjectClick(ImageObject imageObject) {
                isImageSticker = true;
                isTextEdit = false;
                selectedImageObject = imageObject;
                setViewsStatus(1);
            }

            @Override
            public void onImageObjectOutSideClick(ImageObject imageObject) {
                if (selectedImageObject != null) {
                    if (selectedImageObject.equals(imageObject)) {
                        setViewsStatus(0);
                        selectedImageObject = null;
                    }
                }
            }
        });
    }

    /**To add text sticker*/
    private void addword()
    {
        String string = inputText.getText().toString();
        if (!"".equals(string)) {
            final TextObject textObj = operateUtils.getTextObject(string,
                    operateView, 5, 150, 100);
            textObj.commit();
            operateView.addItem(textObj);
            selectedTextObject = textObj;
        }
        layoutInputText.setVisibility(View.GONE);
    }
    /**To edit text sticker*/
    private void editText(final TextObject tObject)
    {
        String string = inputText.getText().toString();
        tObject.setText(string);
        tObject.commit();

        layoutInputText.setVisibility(View.GONE);
    }

    /**To Add image sticker*/
    private void addpic(Bitmap bitmap)
    {

        ImageObject imgObject = operateUtils.getImageObject(bitmap, operateView,
                5, 150, 100);
        operateView.addItem(imgObject);
        selectedImageObject = imgObject;

    }

    /** to Change the image sticker color**/
    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    /** All button's click listener*/

    /** get stickers from Asset*/
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("stickers/stickers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<Sticker> jsonParser(String jsonBuffer){
        ArrayList<Sticker> stickers = new ArrayList<>();
        try {
            JSONObject json = (JSONObject) new JSONTokener(jsonBuffer).nextValue();
            Log.d("","json : "+json);
            Iterator<String> keys = json.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject stickerObj = json.getJSONObject(String.valueOf(key));
                String title = stickerObj.getString("title");
                stickers.add(new Sticker(key,title,"stickers/"+key+".png"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stickers;
    }
    /**Grid view and list view click listener*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.listView_font:
                if (listView.getAdapter().equals(stickerTitleAdapter)) {
                    stickersforGrid.clear();
                    stickersforGrid = db.getStickersWithTile(searchedStickers.get(position).title);
                    stickerGridViewAdapter = new StickerGridViewAdapter(this,stickersforGrid);
                    gridView.setAdapter(stickerGridViewAdapter);
                    stickerGridViewAdapter.notifyDataSetChanged();
                    setViewsStatus(-1);
                    gridView.setVisibility(View.VISIBLE);

                } else if (listView.getAdapter().equals(fontAdapter)) {
                    selectedTextObject.setTypeface(fontNames.get(position));
                    selectedTextObject.commit();
                    operateView.invalidate();

                   setViewsStatus(2);
                }

                break;
            case R.id.gridView_sticker:
                gridView.setVisibility(View.GONE);
                Bitmap bitmap = StickerUtil.getBitmapFromAsset(this,stickersforGrid.get(position).filePath);
                addpic(bitmap);
                // save to recent data
                recentStickers.clear();
                db.insertToRecent(stickersforGrid.get(position));
                recentStickers = db.getAllRecent();

                recentStickerAdapter = new RecentStickerAdapter(this, recentStickers);
                recyclerViewRecent.setAdapter(recentStickerAdapter);

                setViewsStatus(1);
                recentStickerAdapter.notifyDataSetChanged();
                recyclerViewRecent.invalidate();
                break;
        }
    }

    //get Bitmap image by operate view
    public Bitmap getBitmapByView(View v)
    {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }
    /** get file path to save edited new image file**/
    private String getFilePathWithTimeStump(){
        int countPath = filePath.split("/").length;
        String newfilePath = "";
        for (int i = 0; i < countPath -1 ; i ++) {
            newfilePath = newfilePath + filePath.split("/")[i]+"/";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String currentDateTimeString = dateFormat.format(new Date());
        newfilePath = newfilePath+currentDateTimeString + ".png";
        return  newfilePath;
    }
    /** get font type from asset*/
    private ArrayList<Typeface> getFontTypesFromAsset()  {
        ArrayList<Typeface> fontTypes = new ArrayList<>();
        for (String name: fontNames) {
            Typeface tf = Typeface.createFromAsset(this.getAssets(),
                    "fonts/"+name+".ttf");
            fontTypes.add(tf);

        }
        return fontTypes;
    }
    /** to receive filter result on operate view*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_FINISH_FILTER) {
            operateView.bgBmp = bitmap;
            operateView.invalidate();
        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**Sign in and Signup status methods*/
    @Override
    public void onSuccess(AuthResult authResult,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            layoutSuggest.setVisibility(View.VISIBLE);
            layoutRegisterLogin.setVisibility(View.GONE);
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(this,"Sign-Up Success",Toast.LENGTH_LONG).show();
            textEmail.setText(""); textPassword.setText("");
        }
    }

    @Override
    public void onFailed(Exception e,String status) {
        if (AppUtils.KEY_LOGIN.equals(status)){
            Toast.makeText(this, "Sign-in failed"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
        } else if (AppUtils.KEY_REGISTER.equals(status)){
            Toast.makeText(this,"Sign-Up failed" + e.getLocalizedMessage(),Toast.LENGTH_LONG).show();
        }
    }

    /**Log in function to firebase*/

}
