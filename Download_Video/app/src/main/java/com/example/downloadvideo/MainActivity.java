package com.example.downloadvideo;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC = 111;
    ImageView ivPlay, ivVideoPreView;
    VideoView vvVideoStram;
    MediaController mMediaController;
    ProgressDialog mProgressDialog;
    String extension = ".3gp";
    String videoUrl = "https://www.androidbegin.com/tutorial/AndroidCommercial.3gp";
    String videoName = "video_1" + extension;
    String downloadedVideoPath;
    CircleProgressView mCircleProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!checkPermission()) {
            requestPermission();

        }
        bindView();
        init();
        addListener();
    }

    private void addListener() {
        ivPlay.setOnClickListener(this);
    }


    private void bindView() {
        ivPlay = findViewById(R.id.ivPlay);
        ivVideoPreView = findViewById(R.id.ivVideoPreView);
        vvVideoStram = findViewById(R.id.vvVideoStram);
        mCircleProgressView = findViewById(R.id.downloadProgress);
    }

    private void init() {
        downloadedVideoPath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name) + "/" + videoName;
        mMediaController = new MediaController(MainActivity.this);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("Buffering...");

        Glide.with(getApplicationContext()).load(videoUrl).into(ivVideoPreView);
    }

    public void playVideo(String mVideoPath) {
        try {
            vvVideoStram.setVisibility(View.VISIBLE);
            Uri videoUri = Uri.parse(mVideoPath);

            if (!new File(downloadedVideoPath).exists()) {
                vvVideoStram.setVideoURI(videoUri);
            } else {
                ;
                vvVideoStram.setVideoURI(Uri.parse(downloadedVideoPath));
                Toast.makeText(this, "Video play from local storage ", Toast.LENGTH_SHORT).show();
            }

            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }

            mProgressDialog.show();
            vvVideoStram.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    vvVideoStram.setMediaController(mMediaController);
                    mMediaController.setAnchorView(vvVideoStram);

                    vvVideoStram.start();
                    mProgressDialog.dismiss();
                    if (!new File(downloadedVideoPath).exists()) {
                        ivPlay.setVisibility(View.GONE);
                        mCircleProgressView.setVisibility(View.VISIBLE);
                        new DownloadFromURL(videoUrl).execute();
                    }
                }

            });
            vvVideoStram.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    vvVideoStram.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivPlay:
                playVideo(videoUrl);
                break;
        }
    }


    class DownloadFromURL extends AsyncTask<String, String, String> {
        String savePath = Environment.getExternalStorageDirectory() + File.separator + getString(R.string.app_name);

        String urlString;

        public DownloadFromURL(String strUrl) {
            urlString = strUrl;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... fileUrl) {
            int count;
            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                int fileLength = urlConnection.getContentLength();
                InputStream inputStream = new BufferedInputStream(
                        url.openStream(), 8192);
                File dir = new File(savePath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir, videoName);
                OutputStream outputStream = new FileOutputStream(
                        file.getAbsoluteFile());
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;

                    publishProgress("" + (int) ((total * 100) / fileLength));
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            mCircleProgressView.setProgress(Integer.parseInt(progress[0]));
            if (Integer.parseInt(progress[0]) == 100) {

                ivPlay.setVisibility(View.VISIBLE);
                mCircleProgressView.setVisibility(View.GONE);
            }
        }

        @Override
        protected void onPostExecute(String file_url) {
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{new File(downloadedVideoPath).getAbsolutePath()},
                    new String[]{"video/*"}, null);
        }
    }


    protected boolean checkPermission() {
        if (
                (ContextCompat.checkSelfPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)

                ) {
            return false;
        }
        return true;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{

                WRITE_EXTERNAL_STORAGE
        }, RC);
    }
}
