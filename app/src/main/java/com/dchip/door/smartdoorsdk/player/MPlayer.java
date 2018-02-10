/*
 *
 * MPlayer.java
 * 
 * Created by Wuwang on 2016/9/29
 * Copyright © 2016年 深圳哎吖科技. All rights reserved.
 */
package com.dchip.door.smartdoorsdk.player;

import android.app.Service;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.dchip.door.smartdoorsdk.s;
import com.dchip.door.smartdoorsdk.utils.LogUtil;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.http.Url;

/**
 * Description:
 */
public class MPlayer implements IMPlayer,MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,MediaPlayer.OnVideoSizeChangedListener,
        MediaPlayer.OnPreparedListener,MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnErrorListener,SurfaceHolder.Callback{

    private static final String TAG = "MPlayer";
    private MediaPlayer player;

    private String source;
//    private IMDisplay display;
    private SurfaceView mSurfaceView;
    private int mIndex = -1;
    private int mUrlsize = -1;
    private List<String> mUrls;
    private boolean isVideoSizeMeasured=false;  //视频宽高是否已获取，且不为0
    private boolean isMediaPrepared=false;      //视频资源是否准备完成
    private boolean isSurfaceCreated=false;     //Surface是否被创建
    private boolean isUserWantToPlay=false;     //使用者是否打算播放
    private boolean isResumed=false;            //是否在Resume状态

    private boolean mIsCrop=false;

    private IMPlayListener mPlayListener;

    private int currentVideoWidth;              //当前视频宽度
    private int currentVideoHeight;             //当前视频高度


    private static final Object lock = new Object();
    private static volatile MPlayer instance;

    public static void registerInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MPlayer();
                }
            }
        }
        s.Ext.setImPlayerManager(instance);
    }


    private void createPlayerIfNeed(){
        if(null==player){
            player=new MediaPlayer();
            player.setScreenOnWhilePlaying(true);
            player.setOnBufferingUpdateListener(this);
            player.setOnVideoSizeChangedListener(this);
            player.setOnCompletionListener(this);
            player.setOnPreparedListener(this);
            player.setOnSeekCompleteListener(this);
            player.setOnErrorListener(this);
        }
    }

    private void playStart(){
        if(player != null&&isVideoSizeMeasured&&isMediaPrepared&&isSurfaceCreated&&isUserWantToPlay&&isResumed){
            player.start();
            log("视频开始播放");
//            display.onStart(this);
            if(mPlayListener!=null){
                mPlayListener.onStart(this);
            }
        }
    }

    private void playPause(){
        if(player!=null&&player.isPlaying()){
            player.pause();
//            display.onPause(this);
            if(mPlayListener!=null){
                mPlayListener.onPause(this);
            }
        }
    }

    private boolean checkPlay(){
        if(source==null|| source.length()==0){
            return false;
        }
        return true;
    }
    @Override
    public void setPlayListener(IMPlayListener listener){
        this.mPlayListener=listener;
    }

    /**
     * 设置是否裁剪视频，若裁剪，则视频按照DisplayView的父布局大小显示。
     * 若不裁剪，视频居中于DisplayView的父布局显示
     * @param isCrop 是否裁剪视频
     */
    public void setCrop(boolean isCrop){
        this.mIsCrop=isCrop;
        if(mSurfaceView!=null&&currentVideoWidth>0&&currentVideoHeight>0){
            tryResetSurfaceSize(mSurfaceView,currentVideoWidth,currentVideoHeight);
        }
    }

    public boolean isCrop(){
        return mIsCrop;
    }

    /**
     * 视频状态
     * @return 视频是否正在播放
     */
    public boolean isPlaying(){
        return player!=null&&player.isPlaying();
    }

    //根据设置和视频尺寸，调整视频播放区域的大小
    private void tryResetSurfaceSize(final View view, int videoWidth, int videoHeight){
        ViewGroup parent= (ViewGroup) view.getParent();
        int width=parent.getWidth();
        int height=parent.getHeight();
        if(width>0&&height>0){
            final FrameLayout.LayoutParams params= (FrameLayout.LayoutParams) view.getLayoutParams();
            if(mIsCrop){
                float scaleVideo=videoWidth/(float)videoHeight;
                float scaleSurface=width/(float)height;
                if(scaleVideo<scaleSurface){
                    params.width=width;
                    params.height= (int) (width/scaleVideo);
                    params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                }else{
                    params.height=height;
                    params.width= (int) (height*scaleVideo);
                    params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                }
            }else{
                if(videoWidth>width||videoHeight>height){
                    float scaleVideo=videoWidth/(float)videoHeight;
                    float scaleSurface=width/height;
                    if(scaleVideo>scaleSurface){
                        params.width=width;
                        params.height= (int) (width/scaleVideo);
                        params.setMargins(0,(height-params.height)/2,0,(height-params.height)/2);
                    }else{
                        params.height=height;
                        params.width= (int) (height*scaleVideo);
                        params.setMargins((width-params.width)/2,0,(width-params.width)/2,0);
                    }
                }
            }
            view.setLayoutParams(params);
        }
    }

    @Override
    public void setUp(List<String> Urls, SurfaceView view) {
       if(Urls != null && checkUrls(Urls) && view != null){
           if(view.getHolder()!=null){
               view.getHolder().removeCallback(this);
           }
           this.mSurfaceView=view;
           this.mUrls = Urls;
           mSurfaceView.getHolder().addCallback(this);
           mIndex = 0;
           mUrlsize = Urls.size();
           mSurfaceView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if(mPlayListener != null){
                       mPlayListener.onClick();
                   }
               }
           });
          // playNext(mIndex);

       }
    }

    private void setSource(String url)throws MPlayerException{
        this.source=url;
        createPlayerIfNeed();
        isMediaPrepared=false;
        isVideoSizeMeasured=false;
        currentVideoWidth=0;
        currentVideoHeight=0;
        player.setDisplay(null);
        player.reset();
        try {
            player.setDataSource(url);
            player.setDisplay(mSurfaceView.getHolder());
            player.prepareAsync();
//            log("异步准备视频");
        } catch (IOException e) {
            CrashReport.postCatchedException(e);
            throw new MPlayerException("set source error",e);
        }
    }

    private boolean checkUrls(List<String> Urls){

        for(String str:Urls){
            if(TextUtils.isEmpty(str)){
                return false;
            }
        }
        return true;
    }


    @Override
    public void setUp(String Url, SurfaceView view) {
        List<String> urls = new ArrayList<String>();
        urls.add(Url);
        setUp(urls,view);
    }

    @Override
    public void updateUrl(List<String> Urls) {
        mUrls = Urls;
        mUrlsize = Urls.size();
    }


    private void playNext(){
        try{
            if(mIndex >= mUrlsize){
                mIndex = 0;
            }
            setSource(mUrls.get(mIndex));
            mIndex++;
        }catch (Exception e){
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }

    }
    @Override
    public void CloseVolume(){
        if (player==null){
            LogUtil.e(TAG,"CloseVolume player==null");
            return;
        }
        player.setVolume(0, 0);
    }

    @Override
    public void OpenVolume(){
        if (player==null){
            LogUtil.e(TAG,"CloseVolume player==null");
            return;
        }
        AudioManager audioManager=(AudioManager)s.app().getApplicationContext().getSystemService(Service.AUDIO_SERVICE);
        player.setAudioStreamType(AudioManager.STREAM_SYSTEM);
        player.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM), audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM));
        player.start();
    }

    @Override
    public void play() throws MPlayerException {
        if(!checkPlay()){
            throw new MPlayerException("Please setSource");
        }
        if (isUserWantToPlay == false) {
            createPlayerIfNeed();
            isUserWantToPlay = true;
            playStart();
        }
    }

    @Override
    public void pause() {
        isUserWantToPlay=false;
        playPause();
    }

    @Override
    public void onPause() {
        isResumed=false;
        playPause();
    }

    @Override
    public void onResume() {
        isResumed=true;

        playStart();
    }

    @Override
    public void onDestroy() {
        if(player!=null){
            player.release();
            player = null;
        }
        if(mSurfaceView != null){
            mSurfaceView = null;
        }

        if(mPlayListener != null){
            mPlayListener = null;
        }

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //if(mUrlsize > 1){
            playNext();
       // }
        if(mPlayListener!=null){
            mPlayListener.onComplete(this);
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        log("视频准备完成");
        isMediaPrepared=true;
        playStart();
        if(mPlayListener!=null){
            mPlayListener.onPrepared();
        }
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        log("视频大小被改变->"+width+"/"+height);
        if(width>0&&height>0){
            this.currentVideoWidth=width;
            this.currentVideoHeight=height;
            tryResetSurfaceSize(mSurfaceView,width,height);
            isVideoSizeMeasured=true;
            playStart();
        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if(mPlayListener!=null){
            mPlayListener.onError();
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(mSurfaceView!=null&&holder==mSurfaceView.getHolder()){
            isSurfaceCreated=true;
            //此举保证以下操作下，不会黑屏。（或许还是会有手机黑屏）
            //暂停，然后切入后台，再切到前台，保持暂停状态
            if(player!=null){
               // player.setDisplay(holder);
                //不加此句360f4不会黑屏、小米note1会黑屏，其他机型未测
                player.seekTo(player.getCurrentPosition());
            }
            log("surface被创建");
            playNext();
//            playStart();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        log("surface大小改变");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if(mSurfaceView!=null&&holder==mSurfaceView.getHolder()){
            log("surface被销毁");
            isSurfaceCreated=false;
        }
    }

    private void log(String content){
        Log.e("MPlayer",content);
    }
}
