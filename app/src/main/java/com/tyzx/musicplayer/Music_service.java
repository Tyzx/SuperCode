package com.tyzx.musicplayer;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
public class Music_service extends Service {
    private MediaPlayer media_music=new MediaPlayer();
    private ManageBinder mBinder=new ManageBinder();
    class ManageBinder extends Binder{
        public void fun_play(){
            media_music.seekTo(0);
            media_music.start();
        }
        public void fun_pause(){ media_music.pause(); }
        public int getDuration(){ return media_music.getDuration();}//获取歌曲长度/ms
        public int getCurrentPosition(){return media_music.getCurrentPosition(); }//获取当前播放时间
        public void fun_continue(){//继续在当前位置播放
            if(media_music.isPlaying())
                return;
            else media_music.start();
        }
        public void seekTo(int m){
            media_music.seekTo(m);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    @Override
    public void onCreate(){//服务被创建时调用
        super.onCreate();
        try{
            media_music.setDataSource("/mnt/user/0/primary/Download/music.mp3");
            media_music.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){//服务启动时调用
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){//服务被销毁时调用
        super.onDestroy();
        if(media_music!=null){
            media_music.stop();
            media_music.release();
        }
    }
}
