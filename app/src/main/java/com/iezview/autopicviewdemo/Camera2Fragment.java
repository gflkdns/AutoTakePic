package com.iezview.autopicviewdemo;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.ImageReader;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera2Fragment extends Fragment {
    private static final String TAG = "Camera2Fragment";
    private static final int SETIMAGE = 1;
    private static float currentangle;
    private static int currentindex;


    AutoPicView picView;
    TextView show_tishi;

    TextureView mTextureView;
    Button mButton;
    Handler mHandler;
    Handler mUIHandler;
    ImageReader mImageReader;
    CaptureRequest.Builder mPreViewBuidler;
    CameraCaptureSession mCameraSession;
    CameraCharacteristics mCameraCharacteristics;
    Ringtone ringtone;
    //相机会话的监听器，通过他得到mCameraSession对象，这个对象可以用来发送预览和拍照请求
    private CameraCaptureSession.StateCallback mSessionStateCallBack = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession cameraCaptureSession) {
            try {
                mCameraSession = cameraCaptureSession;
                cameraCaptureSession.setRepeatingRequest(mPreViewBuidler.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

        }
    };
    //打开相机时候的监听器，通过他可以得到相机实例，这个实例可以创建请求建造者
    private CameraDevice.StateCallback cameraOpenCallBack = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice cameraDevice) {
            Log.d(TAG, "相机已经打开");
            try {
                mPreViewBuidler = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                SurfaceTexture texture = mTextureView.getSurfaceTexture();
                texture.setDefaultBufferSize(mPreViewSize.getWidth(), mPreViewSize.getHeight());
                Surface surface = new Surface(texture);
                mPreViewBuidler.addTarget(surface);
                cameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), mSessionStateCallBack, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            Log.d(TAG, "相机连接断开");
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {
            Log.d(TAG, "相机打开失败");
        }
    };
    private ImageReader.OnImageAvailableListener onImageAvaiableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            mHandler.post(new ImageSaver(imageReader.acquireNextImage()));
        }
    };
    private Size mPreViewSize;
    //预览图显示控件的监听器，可以监听这个surface的状态
    private TextureView.SurfaceTextureListener mSurfacetextlistener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
            HandlerThread thread = new HandlerThread("Ceamera3");
            thread.start();
            mHandler = new Handler(thread.getLooper());
            CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
            String cameraid = CameraCharacteristics.LENS_FACING_FRONT + "";
            try {
                mCameraCharacteristics = manager.getCameraCharacteristics(cameraid);
                StreamConfigurationMap map = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizeByArea());
                mPreViewSize = map.getOutputSizes(SurfaceTexture.class)[1];
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 5);
                mImageReader.setOnImageAvailableListener(onImageAvaiableListener, mHandler);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                manager.openCamera(cameraid, cameraOpenCallBack, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };
    private View.OnClickListener picOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                shootSound();
                Log.d(TAG, "正在拍照");
                CaptureRequest.Builder builder = mCameraSession.getDevice().createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(mImageReader.getSurface());
                builder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_AUTO);
                builder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                        CameraMetadata.CONTROL_AF_TRIGGER_START);
                builder.set(CaptureRequest.JPEG_ORIENTATION, 90);
                mCameraSession.capture(builder.build(), null, mHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    };
    private int uploadindex;

    @Event(value = {R.id.button_upload, R.id.button_restart, R.id.button_pause}, type = View.OnClickListener.class)
    private void onclick(final View view) {
        String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getParent() + File.separator + "IEZCamera";
        File dir = new File(dirpath);
        final File[] imgs = dir.listFiles();
        if (view.getId() == R.id.button_upload) {
            uploadindex = 0;
            if (imgs.length < 20) {
                Toast.makeText(getActivity(), "请完成拍摄之后上传！", Toast.LENGTH_SHORT).show();
                return;
            }
//            String url = "http://192.168.11.15/api//uploadAll/";
            String url = "http://192.168.11.15/api/upload";
            Log.d(TAG, "imgs.length" + imgs.length);
            String id = System.currentTimeMillis() + "";
            uploadimg((Button) view, imgs, url, id);
        } else if (view.getId() == R.id.button_restart) {
            Log.d(TAG, "重新开始！");
            for (File file :
                    imgs) {
                if (file.isFile()) {
                    file.delete();
                }
            }
            Log.d(TAG, "删除文件成功");
            picView.reStart();
        } else if (view.getId() == R.id.button_pause) {
            Button button = (Button) getActivity().findViewById(R.id.button_pause);
            if (picView.pause()) {
                button.setText("暂停");
            } else {
                button.setText("继续");
            }
        }
    }

    private void uploadimg(final Button view, final File[] imgs, final String url, final String id) {
        if (uploadindex >= imgs.length) {
            Button button = view;
            button.setText("完成");
            button.setText("完成");
            for (File file :
                    imgs) {
                if (file.isFile()) {
                    file.delete();
                }
            }
            return;
        }
        RequestParams parme = new RequestParams(url);
        parme.addBodyParameter("id", id);
        parme.addBodyParameter("file", imgs[uploadindex]);
        x.http().post(parme, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Button button = view;
                button.setText((++uploadindex) + "/" + imgs.length);
                Log.d(TAG, "onSuccess");
                uploadimg(view, imgs, url, id);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(getActivity(), "onError", Toast.LENGTH_SHORT).show();
                Log.d(TAG, ex.toString());
                view.setText("中断");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled");
            }

            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished");
            }

            @Override
            public void onWaiting() {
                Log.d(TAG, "onWaiting");
            }

            @Override
            public void onStarted() {
                Log.d(TAG, "onStarted");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.d(TAG, "onLoading");
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_camera2, null);
        x.view().inject(this, v);
        findview(v);
        //初始化拍照的声音
        ringtone = RingtoneManager.getRingtone(getActivity(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
        AudioAttributes.Builder attr = new AudioAttributes.Builder();
        attr.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION);
        ringtone.setAudioAttributes(attr.build());
        //初始化相机布局
        mTextureView.setSurfaceTextureListener(mSurfacetextlistener);
        //设置点击拍照的监听
        picView = (AutoPicView) v.findViewById(R.id.autopicview);
        show_tishi = (TextView) v.findViewById(R.id.show_tishi);
        show_tishi.setText("请调整");
        picView.start(new AutoPicView.TakePicListener() {
            @Override
            public void canTakePic(float angle, int index) {
                Camera2Fragment.currentangle = angle;
                Camera2Fragment.currentindex = index;
                picOnClickListener.onClick(null);
            }

            @Override
            public void yourPhonePerfect() {
                show_tishi.setVisibility(View.GONE);
            }

            @Override
            public void placeAdjustYourPhone() {
                show_tishi.setVisibility(View.VISIBLE);
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mCameraSession != null) {
            mCameraSession.getDevice().close();
            mCameraSession.close();
        }
    }

    private void findview(View v) {
        mTextureView = (TextureView) v.findViewById(R.id.tv_textview);
    }

    /**
     * 播放系统的拍照的声音
     */
    public void shootSound() {
        ringtone.stop();
        ringtone.play();
    }

    private class ImageSaver implements Runnable {
        Image reader;

        public ImageSaver(Image reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            Log.d(TAG, "正在保存图片");
            String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getParent() + File.separator + "IEZCamera";
            File dir = new File(dirpath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, currentindex + "_" + currentangle + ".jpg");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                ByteBuffer buffer = reader.getPlanes()[0].getBuffer();
                byte[] buff = new byte[buffer.remaining()];
                buffer.get(buff);
                outputStream.write(buff);
                Log.d(TAG, "保存图片完成");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.close();
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
