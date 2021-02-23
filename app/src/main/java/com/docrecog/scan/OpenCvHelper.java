package com.docrecog.scan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class OpenCvHelper {

    private Mat template;
    private String TAG = "OpenCvHelper";
    private Mat outMat;
    private String newMessage;

    private OCRCallback faceMatchCallBack;

    public void setTemplate(Mat outMat) {
        if (outMat != null) {
            template = outMat;
        }
    }

    public enum AccuraOCR {
        SUCCESS,
        FAIL,
        NONE

    }

    public ImageOpencv nativeCheckCardIsInFrame(final Context context, Bitmap bmp, boolean doblurcheck) {
        if (context instanceof OCRCallback) {
            this.faceMatchCallBack = (OCRCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement com.docrecog.scan.OCRCallback");
        }

        ImageOpencv i = new ImageOpencv();
        if (template == null && !template.empty()) {
            faceMatchCallBack.onUpdateProcess("please set template first");
            Log.e("OpenCvHelper", "please set template first");
            try {
                bmp.recycle();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return i;
        }
        Mat clone = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, clone);
        if (outMat != null) {
            outMat.release();
        }
        outMat = new Mat();

//        i = RecogEngine.checkCardInFrames(template.getNativeObjAddr(), clone.getNativeObjAddr(), outMat.getNativeObjAddr(), context/*faceMatchCallBack*/, context.getAssets(), file != null ? file.getAbsolutePath() : "", doblurcheck);
        i = RecogEngine.checkCardInFrames(template.getNativeObjAddr(), clone.getNativeObjAddr(), outMat.getNativeObjAddr(), doblurcheck);
        if (i != null) {
            i.accuraOCR = i.isSucess ? AccuraOCR.SUCCESS : AccuraOCR.FAIL;
//                return i;
            if (i.isChangeCard && i.cardPos > 0) {
                faceMatchCallBack.onUpdateProcess(i.message);
//                return i.isSucess;
            } else {
                newMessage = i.message;
                faceMatchCallBack.onUpdateProcess(i.message);
            }
            if (i.isSucess) {
                i.croppedPick = outMat;
            }
            ImageOpencv finalI = i;
            final Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        if (newMessage.equals(finalI.message) || !finalI.message.contains("Process")) {
                            faceMatchCallBack.onUpdateProcess("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Runnable runnable1 = new Runnable() {
                public void run() {
                    new Handler().postDelayed(runnable, 1000);
                }
            };
            ((Activity) context).runOnUiThread(runnable1);

        } else {
            i = new ImageOpencv();
            i.accuraOCR = AccuraOCR.NONE;
        }
        try {
            clone.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            bmp.recycle();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return i;
    }

    public Bitmap getCroppedCard() {
        if (outMat != null && outMat.width() > 0 && outMat.height() > 0) {
            Bitmap bmp = Bitmap.createBitmap(outMat.width(), outMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outMat, bmp);
            return bmp;
        }
        return null;
    }

}
