package com.allyouneedapp.palpicandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.allyouneedapp.palpicandroid.adapters.FilterAdapter;
import com.allyouneedapp.palpicandroid.listeners.RecyclerOnItemClickListener;
import com.allyouneedapp.palpicandroid.models.FilterData;

import java.util.ArrayList;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageAlphaBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageChromaKeyBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHazeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageLinearBurnBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSketchFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageToonFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageTransformFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageVignetteFilter;

public class FilterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnNext;
    private Button btnBack;

    private RecyclerView recyclerView;
    private ImageView gpuImageView;

    private FilterAdapter adapter;
    private ArrayList<FilterData> filters = new ArrayList<>();
    private String filePath;
    public static Bitmap tempBitmap;
    public static Bitmap editBitmap;
    private boolean isFromFilter = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        initAllViews();
        getSupportActionBar().hide();
        getEditBitmapFromFinish();
    }

    private void initAllViews(){
        btnNext = (Button) findViewById(R.id.btn_next_filter);
        btnNext.setOnClickListener(this);
        btnBack = (Button) findViewById(R.id.btn_back_filter);
        btnBack.setOnClickListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.rc_filter);
        gpuImageView = (ImageView) findViewById(R.id.gpu_imageView_filter);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerOnItemClickListener(this, new RecyclerOnItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                GPUImage image = new GPUImage(FilterActivity.this);
                image.setFilter(filters.get(position).filterType);
                editBitmap = image.getBitmapWithFilterApplied(tempBitmap);
                gpuImageView.setImageBitmap(editBitmap);
            }
        }));

        filters.add(new FilterData(new GPUImageFilter(),"Original"));
        filters.add(new FilterData(new GPUImageAlphaBlendFilter() , "Instant"));
        filters.add(new FilterData(new GPUImageTransformFilter() , "Transfer"));
        filters.add(new FilterData(new GPUImageExposureFilter() , "Fade"));
        filters.add(new FilterData(new GPUImageChromaKeyBlendFilter() , "Chrome"));
        filters.add(new FilterData(new GPUImageGammaFilter() , "Process"));
        filters.add(new FilterData(new GPUImageVignetteFilter() , "Vignette"));
        filters.add(new FilterData(new GPUImageToneCurveFilter() , "Curve"));
        filters.add(new FilterData(new GPUImageSepiaFilter() , "Sepia"));
        filters.add(new FilterData(new GPUImageHazeFilter() , "Haze"));
        filters.add(new FilterData(new GPUImageMonochromeFilter() , "Mono"));
        filters.add(new FilterData(new GPUImageGrayscaleFilter() , "Light Gray"));
        filters.add(new FilterData(new GPUImageEmbossFilter() , "Emboss"));
        filters.add(new FilterData(new GPUImageToonFilter() , "Toon"));
        filters.add(new FilterData(new GPUImageLinearBurnBlendFilter() , "Linear"));
        filters.add(new FilterData(new GPUImageSketchFilter() , "Sketch"));

        editBitmap = tempBitmap;
    }

    private void getEditBitmapFromFinish(){
        if (getIntent().hasExtra("filePath")) {
            this.filePath = getIntent().getExtras().getString("filePath");
            isFromFilter = false;
            getIntent().removeExtra("filePath");
        } else if (getIntent().hasExtra("filter")){
            isFromFilter = true;
            getIntent().removeExtra("filter");
        }
        gpuImageView.setImageBitmap(tempBitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter = new FilterAdapter(this, tempBitmap, filters);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_filter:
                if (isFromFilter){
                    FinishUpActivity.bitmap = editBitmap;
                    finish();
                } else {
                    Intent intent = new Intent(this, FinishUpActivity.class);
                    FinishUpActivity.bitmap = editBitmap;
                    intent.putExtra("filePath",filePath);
                    startActivity(intent);
                }
                break;
            case R.id.btn_back_filter:
                this.finish();
                break;
            default:
                break;
        }
    }


}
