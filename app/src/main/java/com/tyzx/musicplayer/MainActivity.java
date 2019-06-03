package com.tyzx.musicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton play,pause,_continue;
    private SeekBar Music_bar;
    private TextView all,current;
    private static final int UPDATE_PROGRESS=0;//更新进度条标志
    private Music_service.ManageBinder musicBinder;//用于操作服务
    private ServiceConnection connection =new ServiceConnection() {//绑定服务时调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
        @Override
        public void onServiceConnected(ComponentName name,IBinder service) {
            musicBinder=(Music_service.ManageBinder) service;
            Music_bar.setMax(musicBinder.getDuration());//进度条时间
            Music_bar.setProgress(musicBinder.getCurrentPosition());//进度条初始位置
            all.setText(String.valueOf(musicBinder.getDuration()/60000)+":00");//歌曲总时间
            current.setText("0:00");
        }
    };
    private Handler handler=new Handler(){//主线程更新UI
        @Override
        public void handleMessage(Message msg){
            switch(msg.what){
                case UPDATE_PROGRESS: updateProgress();break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play=findViewById(R.id.play);
        pause=findViewById(R.id.pause);
        _continue=findViewById(R.id._continue);
        Music_bar=findViewById(R.id.music_bar);
        current=findViewById(R.id.current_time);
        all=findViewById(R.id.all_time);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        _continue.setOnClickListener(this);
        Intent start_service=new Intent(this,Music_service.class);
        startService(start_service);//开启服务,调用onCreat和onStart方法
        bindService(start_service,connection,BIND_AUTO_CREATE);//绑定服务
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        Music_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser){
                if(fromUser)
                    musicBinder.seekTo(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){//开始触摸

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){//停止触摸

            }
        });
    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.play:musicBinder.fun_play();
            handler.sendEmptyMessage(UPDATE_PROGRESS);
            break;
            case R.id.pause:musicBinder.fun_pause();
            handler.sendEmptyMessage(UPDATE_PROGRESS);
            break;//调用暂停的方法
            case R.id._continue:musicBinder.fun_continue();
            handler.sendEmptyMessage(UPDATE_PROGRESS);
            break;//调用继续的方法
        }
    }
    private void updateProgress(){//更新进度条
        int currentPosition=musicBinder.getCurrentPosition();
        Music_bar.setProgress(currentPosition);
        float i=currentPosition/60000.0f;
        float _i=i*100;
        int __i=(int)_i;
        int min=__i/100;
        float sec=(__i-min*100)*0.6f;
        int _sec=(int) sec;
        current.setText(String.valueOf(min)+":"+String.valueOf(_sec));
        handler.sendEmptyMessageDelayed(UPDATE_PROGRESS,500);//500ms更新一次
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(musicBinder!=null) handler.sendEmptyMessage(UPDATE_PROGRESS);//进入界面后更新进度条
    }
    @Override
    protected void onStop(){
        super.onStop();
        handler.removeCallbacksAndMessages(null);
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        //关闭服务
        unbindService(connection);//解绑
    }
}
