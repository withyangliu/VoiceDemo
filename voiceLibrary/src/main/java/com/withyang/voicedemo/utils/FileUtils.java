package com.withyang.voicedemo.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by yunwen on 2017/12/25 0025.
 */

public class FileUtils {

    /**
     * 人脸识别文件
     */
    public static final String FACE_IMAGE = "face.jpg";

    /**
     * 文件根目录
     *
     * @return
     */
    public static String getRootPath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + "faqrobot";
    }

    /**
     * 人脸识别目录
     *
     * @return
     */
    public static String getFaceRecoPath() {
        return getRootPath() + File.separator + "face";
    }

    /**
     * 人脸签到目录
     *
     * @return
     */
    public static String getSignInPath() {
        return getRootPath() + File.separator + "signin";
    }

    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return availableBlocks * blockSize;
        } else {
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        }
    }

    public static String prettySize(long size) {
        int unitNum = 1024;
        String result = null;
        if (size < unitNum) {
            return size + "B";
        }
        float newSize = size;
        String[] units = {"KB", "MB", "GB", "TB"};
        for (String unit : units) {
            newSize = newSize / (float) unitNum;
            if (newSize < unitNum) {
                result = String.format(Locale.getDefault(), "%.02f", newSize) + unit;
                break;
            }
        }
        return result;
    }

    public static boolean isFileExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    public static Uri getHeadUri() {
        return Uri.fromFile(FileUtils.getNewFile(getRootPath() + File.separator + "head.jpeg"));
    }

    public static boolean isFileExist(File file) {
        if (null != file && file.exists()) {
            return true;
        }
        return false;
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    public static boolean makeFile(String path) {
        File file = new File(path);
        if (path.endsWith(File.separator)) {
            return false;
        }
        // 判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            // 如果目标文件所在的目录不存在，则创建父目录
            if (!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        // 创建目标文件
        try {
            if (file.exists()) {
                file.delete();
            }
            if (file.createNewFile()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean makeDir(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.mkdirs();
        return isFileExist(path);
    }

    public static File getNewFile(String path) {
        if (makeFile(path)) {
            return new File(path);
        }
        return null;
    }

    public static String savePic(String fileName, Bitmap bitmap) {
        String filePath = getSignInPath() + File.separator + fileName;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        FileOutputStream fos = null;
        try {
            makeFile(filePath);
            File file = new File(filePath);
            fos = new FileOutputStream(file);
            fos.write(bytes);
        } catch (Exception e) {
            filePath = "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    filePath = "";
                }
            }
        }
        return filePath;
    }

    public static final String getBase64(File file) {
        FileInputStream inputFile = null;
        String base = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            base = Base64.encodeToString(buffer, Base64.NO_WRAP);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base;
    }

    /**
     * base64字符串转文件
     *
     * @param base64
     * @return
     */
    public static File base64ToFile(String base64, File file) {
        FileOutputStream out = null;
        try {
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Bitmap转File
     *
     * @param filePath
     * @param fileName
     * @param bitmap
     * @return
     */
    public static String bitmapToFile(String filePath, String fileName, Bitmap bitmap) {
        byte[] bytes = bitmapToBytes(bitmap);
        return saveFile(filePath, fileName, bytes);
    }

    public static byte[] bitmapToBytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static String saveFile(String filePath, String fileName, byte[] bytes) {
        String fileFullName;
        FileOutputStream fos = null;
        try {
            String suffix = "";
            if (filePath == null || filePath.trim().length() == 0) {
                return null;
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            File fullFile = new File(filePath, fileName + suffix);
            fileFullName = fullFile.getPath();
            fos = new FileOutputStream(new File(filePath, fileName + suffix));
            fos.write(bytes);
        } catch (Exception e) {
            fileFullName = null;
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    fileFullName = null;
                }
            }
        }
        return fileFullName;
    }
}
