package com.docrecog.scan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;

import com.accurascan.accuraemirates.motiondetection.data.GlobalData;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class OpenCvHelper {

    private String TAG = "OpenCvHelper";
    private Mat outMat;
    private String newMessage = "";
    public final String msgMotionDetection = "Keep Document Steady";

    private OCRCallback ocrCallback;

    public void setTemplate(Mat outMat) {
    }

    public enum AccuraOCR {
        SUCCESS,
        FAIL,
        NONE

    }

    public OpenCvHelper(Context context) {
        if (context instanceof OCRCallback) {
            this.ocrCallback = (OCRCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must have to implement" + OCRCallback.class.getName());
        }
    }

    public ImageOpencv nativeCheckCardIsInFrame(Bitmap bmp, boolean doblurcheck) {
        Mat clone = new Mat(bmp.getHeight(), bmp.getWidth(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp, clone);
        if (outMat != null) {
            outMat.release();
        }
        outMat = new Mat();

        ImageOpencv i = RecogEngine.checkCardInFrames(clone.getNativeObjAddr(), outMat.getNativeObjAddr(), doblurcheck);
        if (i != null) {
            i.accuraOCR = i.isSucess ? AccuraOCR.SUCCESS : AccuraOCR.FAIL;
//                return i;
            if (i.isChangeCard && i.cardPos > 0) {
                ocrCallback.onUpdateProcess(i.message);
//                return i.isSucess;
            } else {
                newMessage = i.message;
                ocrCallback.onUpdateProcess(i.message);
            }
            if (i.isSucess) {
                ocrCallback.onUpdateProcess("Processing...");
                i.croppedPick = outMat;
            }
        } else {
            i = new ImageOpencv();
            i.accuraOCR = AccuraOCR.NONE;
        }
        try {
            clone.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return i;
    }

    int frameCount = 0;

    public int doCheckFrame(byte[] data, int width, int height) {
        frameCount++;
        if (GlobalData.isPhoneInMotion()) {
            if (frameCount % 5 == 0) {
                ocrCallback.onUpdateProcess(msgMotionDetection);
            }
            return 0;
        }
        int i = RecogEngine.doCheckData(data, width, height);
        if (ocrCallback != null) {
            if (i == 0) {
                if (frameCount % 2 == 0) ocrCallback.onUpdateProcess(msgMotionDetection);
            } else if (i > 0 && newMessage.equals("0")){
                ocrCallback.onUpdateProcess("");
            }
        }
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
