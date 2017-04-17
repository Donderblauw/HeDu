package lung.hedu;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
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
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class camera_photo
{

    public static void start_camera_take_picture(final ImageView viewer_photo, final String file_name)
    {
        final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback()
        {
            @Override
            public void onOpened(CameraDevice camera)
            {
                try
                {
                    camera_photo.auto_focus(camera, viewer_photo, file_name);
                } catch (CameraAccessException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(CameraDevice camera)
            {
                camera.close();
            }

            @Override
            public void onError(CameraDevice camera, int error)
            {
                camera.close();
                camera = null;
            }
        };
        try
        {
            camera_photo.openCamera(stateCallback);
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void openCamera(CameraDevice.StateCallback stateCallback) throws CameraAccessException
    {
        Context context_holder = ApplicationContextProvider.getContext();
        CameraManager manager = (CameraManager) context_holder.getSystemService(Context.CAMERA_SERVICE);

        try
        {
            String cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            manager.openCamera(cameraId, stateCallback, null);

        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
        try
        {
            Thread.sleep(100);
        } catch (InterruptedException e)
        {
        }
        ;
        Log.e("camera", "Sleep, re-try");
    }

    public static void auto_focus(final CameraDevice cameraDevice, final ImageView imageviewer_new_picture, final String file_name) throws CameraAccessException
    {

        Log.e("camera", "start");

        SurfaceTexture mDummyPreview = new SurfaceTexture(1);
        Surface mDummySurface = new Surface(mDummyPreview);

        List<Surface> outputSurfaces = new ArrayList<Surface>(2);
        outputSurfaces.add(mDummySurface);

        final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        captureRequestBuilder.addTarget(mDummySurface);

        // Log.e("camera", "config capture");
        cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback()
        {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session)
            {
                try
                {
                    session.capture(captureRequestBuilder.build(), null, null);
                } catch (CameraAccessException e)
                {
                    e.printStackTrace();
                }
                // return_session = session;
                // try{ Thread.sleep(1000); }catch(InterruptedException e){ };
                // Log.e("camera", "start auto_focus");
                captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                try
                {
                    session.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                } catch (CameraAccessException e)
                {
                    e.printStackTrace();
                }

                try{ Thread.sleep(500); }catch(InterruptedException e){ };
                // Log.e("camera", "start new foto");
                take_photo_new(cameraDevice, imageviewer_new_picture, file_name);

            }

            @Override
            public void onConfigureFailed(CameraCaptureSession session)
            {
            }
        }, null);

    }

    private static void closeCamera(CameraDevice cameraDevice)
    {
        if (null != cameraDevice)
        {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    private static void take_photo_new(CameraDevice cameraDevice, ImageView imageviewer_new_picture, String file_name)
    {
        Context context_holder = ApplicationContextProvider.getContext();
        CameraManager manager = (CameraManager) context_holder.getSystemService(Context.CAMERA_SERVICE);

        HandlerThread mBackgroundThread;
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        final Handler mBackgroundHandler = new Handler(mBackgroundThread.getLooper());

        try
        {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());

            Size[] jpegSizes = null;
            if (characteristics != null)
            {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length)
            {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            ImageReader reader = ImageReader.newInstance((width + 1), (height + 1), ImageFormat.JPEG, 1);

            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            captureBuilder.set(CaptureRequest.COLOR_CORRECTION_MODE, CameraMetadata.COLOR_CORRECTION_MODE_FAST);
            captureBuilder.set(CaptureRequest.FLASH_MODE, CameraMetadata.FLASH_MODE_OFF);

            captureBuilder.set(CaptureRequest.JPEG_THUMBNAIL_QUALITY, null);

            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());

            captureBuilder.build();

            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), null, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);


            Log.e("camera", "take picture... sleep");

            try{ Thread.sleep(2000); }catch(InterruptedException e){ }

            byte[] bytes;
            Image image = null;
            try
            {
//                Log.e("camera", " reader"+reader.getHeight());
                image = reader.acquireLatestImage();
                image.getFormat();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                bytes = new byte[buffer.capacity()];
                buffer.get(bytes);
            }
            finally
            {
                if (image != null) {
                    image.close();
                }
            }

            // String file_name = "curent_photo";
            FileOutputStream out_put_stream = null;
            try
            {
                out_put_stream = ApplicationContextProvider.getContext().openFileOutput(file_name + ".jpg", ApplicationContextProvider.getContext().MODE_PRIVATE);
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
            try {
                out_put_stream.write(bytes);
                Log.e("camera", "saved picture! "+file_name);

                if(imageviewer_new_picture != null)
                {
                    ImageView cap_picture = (ImageView) imageviewer_new_picture;
                    Bitmap bitmap_found = FileIO.loadBMPPrivate(file_name, Bitmap.CompressFormat.JPEG);
                    cap_picture.setImageBitmap(bitmap_found);
                }

            } catch (IOException e)
            {
                e.printStackTrace();
            } finally {
                if (null != out_put_stream) {
                    try
                    {
                        out_put_stream.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                closeCamera(cameraDevice);
            }
        } catch (CameraAccessException e)
        {
            e.printStackTrace();
        }
    }
}
