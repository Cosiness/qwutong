package com.borqs.qiupu.util;

import android.content.Context;
import android.net.Uri;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInfo {

    private FileInfo() {
    }

    /**
     * @param filename
     * @return
     */
    public static String mainName(String filename) {
        int start = filename.lastIndexOf("/");
        int stop = filename.lastIndexOf(".");
        if (stop < start)
            stop = filename.length();
        if (start >= 0) {
            return filename.substring(start + 1, stop);
        } else {
            return "";
        }
    }

    /**
     * @param filename
     * @return
     */
    private static String extension(String filename) {
        int start = filename.lastIndexOf("/");
        int stop = filename.lastIndexOf(".");
        if (stop < start || stop >= filename.length() - 1) {
            return "";
        } else {
            return filename.substring(stop + 1, filename.length());
        }
    }

    /**
     * @param filename
     * @return
     */
    public static String mimeType(String filename) {
        String ext = extension(filename);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return (mime == null) ? "*.*" : mime;
    }

    public static String mimeTypeByExtendedName(String ext) {
//        String ext = getFileType(context, uri);
        String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return (mime == null) ? "*.*" : mime;
    }

    /**
     * @param size
     * @return
     */
    public static String sizeString(long size) {
        if (size < 1024)
            return String.format("%d B", size);
        else if (size < 1024 * 1024)
            return String.format("%.2f KB", (double) size / 1024);
        else if (size < 1024 * 1024 * 1024)
            return String.format("%.2f MB", (double) size / (1024 * 1024));
        else if (size < 1024L * 1024 * 1024 * 1024)
            return String.format("%.2f GB", (double) size
                    / (1024 * 1024 * 1024));
        else
            return String.format("%.2f EB", (double) size
                    / (1024L * 1024 * 1024 * 1024));
    }

    /**
     * @param sizeString
     * @return
     * @throws ParseException
     */
    public static long stringToSize(String sizeString) throws ParseException {
        Pattern pattern = Pattern.compile("(-?\\d+\\.?\\d*)([\\w]{0,2})",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sizeString);
        if (matcher.matches()) {
            double baseSize = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            if (unit.equals("b") || unit.length() == 0) {
                return (long) baseSize;
            } else if (unit.equals("k") || unit.equals("kb")) {
                return (long) (baseSize * 1024);
            } else if (unit.equals("m") || unit.equals("mb")) {
                return (long) (baseSize * (1024 * 1024));
            } else if (unit.equals("g") || unit.equals("gb")) {
                return (long) (baseSize * (1024 * 1024 * 1024));
            } else if (unit.equals("e") || unit.equals("eb")) {
                return (long) (baseSize * (1024L * 1024 * 1024 * 1024));
            }
        }
        throw new ParseException(sizeString, 0);
    }

    /**
     * @param timeString
     * @return
     * @throws ParseException
     */
    public static long timespanToMillis(String timeString)
            throws ParseException {
        Pattern pattern = Pattern.compile("(-?\\d+\\.?\\d*)([\\w]{0,1})",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(timeString);
        if (matcher.matches()) {
            double baseMillis = Double.parseDouble(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();
            if (unit.equals("d") || unit.length() == 0) {
                return (long) (baseMillis * 1000 * 3600 * 24);
            } else if (unit.equals("h")) {
                return (long) (baseMillis * 1000 * 3600);
            } else if (unit.equals("w")) {
                return (long) (baseMillis * 1000 * 3600 * 24 * 7);
            } else if (unit.equals("m")) {
                return (long) (baseMillis * 1000 * 3600 * 24 * 30);
            } else if (unit.equals("y")) {
                return (long) (baseMillis * 1000 * 3600 * 24 * 360);
            }
        }
        throw new ParseException(timeString, 0);
    }

    /// extra file extension from file header begin
    public static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    // todo: fixme, why for png it only get "89504E" but not "89504E47",
    // investigate and fix it.
    static {
        //images
        mFileTypes.put("FFD8FF", "jpg");
        mFileTypes.put("89504E", "png");    // this is ugly change need to be investigated
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");
        //
        mFileTypes.put("41433130", "dwg"); //CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("7B5C727466", "rtf"); //日记本
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("44656C69766572792D646174653A", "eml"); //邮件
        mFileTypes.put("D0CF11E0", "doc");
        mFileTypes.put("5374616E64617264204A", "mdb");
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E", "pdf");
        mFileTypes.put("504B0304", "zip");
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("1F8B08", "gz");
        mFileTypes.put("", "");
    }

    public static String getFileType(Context context, Uri uri) {
        return mFileTypes.get(getFileHeader(context, uri));
    }
    //获取文件头信息
    public static String getFileHeader(Context context, Uri uri) {
        InputStream is = null;
        String value = null;
        try {
//            is = new FileInputStream(filePath);
            is = context.getContentResolver().openInputStream(uri);
            byte[] b = new byte[3];
            is.read(b, 0, b.length);
            value = bytesToHexString(b);
        } catch (Exception e) {
        } finally {
            if(null != is) {
                try {
                    is.close();
                } catch (IOException e) {}
            }
        }
        return value;
    }

    private static String bytesToHexString(byte[] src){
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        return builder.toString();
    }

    /// extra file extension from file header end
}
