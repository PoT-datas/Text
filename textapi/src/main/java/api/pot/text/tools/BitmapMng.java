package api.pot.text.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;

import static android.content.Context.PRINT_SERVICE;

public class BitmapMng {


    public static void galleryAddPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static void galleryAddPic(Context context, String path) {
        galleryAddPic(context, new File(path));
    }

    public static int[] getImageSize(File file) {
        int[] size = {0, 0};
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            size[0] = options.outWidth;
            size[1] = options.outHeight;
            //String imageType = options.outMimeType; // return FileType/Extension  Ex: Image/jpeg
        } catch (Exception e) {}
        return size;//[0]=width, [0]=height
    }

    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap clone(Bitmap bitmap){
        try {
            Bitmap outputBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outputBmp);
            canvas.drawBitmap(bitmap, 0, 0, null);
            return outputBmp;
        }catch (Exception e){return null;}
    }

    public static Bitmap getBlackAndWhite(Bitmap bitmap){
        Bitmap bwBitmap = Bitmap.createBitmap( bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565 );
        float[] hsv = new float[ 3 ];
        for( int col = 0; col < bitmap.getWidth(); col++ ) {
            for( int row = 0; row < bitmap.getHeight(); row++ ) {
                Color.colorToHSV( bitmap.getPixel( col, row ), hsv );
                if( hsv[ 2 ] > 0.5f ) {
                    bwBitmap.setPixel( col, row, 0xffffffff );
                } else {
                    bwBitmap.setPixel( col, row, 0xff000000 );
                }
            }
        }
        return bwBitmap;
    }







    private static int REQUEST_CODE = 123;
    private static Context mContext = null;
    private static String mName;
    private static String mPath;
    private static Bitmap mBitmap;
    //
    private String caheDir = "";
    private String internal_storage = "";
    private String external_storage = "";

    //*******************************others***********************************

    public static boolean copy() {

        File dirOri = new File(BitmapMng.getInternalStoragePath() + "/town.jpg");
        File dirDest = new File(BitmapMng.getInternalStoragePath() + "/Tester" + "/town.jpg");

        FileChannel src = null;
        FileChannel dst = null;

        long blockSize;

        try {
            src = new FileInputStream(dirOri).getChannel();
            dst = new FileOutputStream(dirDest).getChannel();
            // Transfer file in 256MB blocks
            blockSize = Math.min(268435456, src.size());
            long position = 0;
            while (dst.transferFrom(src, position, blockSize) > 0) {
                position += blockSize;
            }
            //
            src.close();
            dst.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
    }


    //**************************size********************************************

    public static String totalMemoryFormated(String path) {
        return bytesToHuman(totalMemory(path));
    }

    public static long totalMemory(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize(), totalBlocks = statFs.getBlockCount();
        long total = (totalBlocks * blockSize);
        return total;
    }

    public static String freeMemoryFormated(String path) {
        return bytesToHuman(freeMemory(path));
    }

    public static long freeMemory(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize(), availableBlocks = statFs.getAvailableBlocks();
        long free = (availableBlocks * blockSize);
        return free;
    }

    public static String busyMemoryFormated(String path) {
        return bytesToHuman(busyMemory(path));
    }

    public static long busyMemory(String path) {
        return (totalMemory(path) - freeMemory(path));
    }

    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " Kb";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " Mb";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " Gb";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " Tb";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " Pb";
        if (size >= Eb) return floatForm((double) size / Eb) + " Eb";

        return "???";
    }

    public static String getInternalStoragePath() { return Environment.getExternalStorageDirectory().getPath();}

    public static String getExternalStoragePath() {
        File storage = new File("/storage");
        String external_storage_path = "";
        String size = "";

        if (storage.exists()) {
            File[] files = storage.listFiles();

            for (File file : files) {
                if (file.exists()) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (Environment.isExternalStorageRemovable(file)) {
                                // storage is removable
                                external_storage_path = file.getAbsolutePath();
                                //break;
                                return external_storage_path;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("TAG", e.toString());
                    }
                }
            }
        }
        return external_storage_path;
    }

    //*********************************deleting**************************************

    public static void deleteFile(File file){
        if(file.exists()) {
            if(file.isFile())
                file.delete();
            else if(file.isDirectory()){
                for(File child : file.listFiles())
                    deleteFile(child);
                file.delete();
            }
        }
    }


    //*********************************reading****************************************

    public static Bitmap readImage(String name, String path){
        File imgFile = new File(path, name);
        if(!imgFile.exists()) return null;
        InputStream is;
        Bitmap bitmap = null;
        try {
            is = new FileInputStream(imgFile);
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {}
        return bitmap;
    }


    //********************************writing*************************************

    public static void saveImage(String name, String dir, Bitmap bitmap){
        saveImage(name, dir, bitmap, 100);
    }

    public static void saveImage(String name, String dir, Bitmap bitmap, int quality){
        if (bitmap!=null && checkDir(dir)){
            FileOutputStream output;
            File myImageFile = new File(dir, name);
            try {
                output = new FileOutputStream(myImageFile,false);
                bitmap.compress(getCompressFormat(name), quality, output);
                /*quality=[0, 100]% means level of compressing of a image
                 * if initial image size=500kb, we want that the new size=300kb, so set quality=60.
                 * cause 500kb * 60% = 300kb*/
            } catch (Exception e) {}
        }
    }

    private static Bitmap.CompressFormat getCompressFormat(String name) {
        if(name.indexOf(".")==-1 || name.indexOf(".")==name.length()-1) return Bitmap.CompressFormat.PNG;
        String extension = (name.substring(name.indexOf(".")+1)).toLowerCase();
        if(extension.equals("jpg") || extension.equals("jpeg")) return Bitmap.CompressFormat.JPEG;
        else if(extension.equals("webp")) return Bitmap.CompressFormat.WEBP;
        else return Bitmap.CompressFormat.PNG;
    }

    public static boolean checkDir(String path) {
        while(path.charAt(path.length()-1)=='/'){
            path = path.substring(0, path.length()-1);
        }
        //
        File mainDir = new File(path);
        if(!mainDir.exists()) mainDir.mkdir();
        if (!mainDir.exists()) {
            int index = -1;
            String c_path = "", allPath = "";
            File c_dir;
            while ( (index=path.indexOf(File.separator))!=-1 ){
                if(index!=path.length()-1) {
                    if(index!=0) {
                        allPath += (allPath.length()==0 || allPath.charAt(allPath.length()-1)=='/' ? "" : "/") + path.substring(0, index);
                        c_dir = new File(allPath);
                        path = path.substring(index + 1);
                        if (!c_dir.exists()) c_dir.mkdir();
                    }else {
                        allPath += "/";
                        path = path.substring(index + 1);
                    }
                }else path = null;
            }
            if(path!=null){
                allPath += "/"+path;
                c_dir = new File(allPath);
                if(!c_dir.exists()) c_dir.mkdir();
            }
            if(mainDir.exists()) return true;
        }else return true;
        return false;
    }



    private Intent mShareIntent;
    private static OutputStream os;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void saveAsPDF(Context context, View view, String dir, String name, int width, int height) {

        // Create a shiny new (but blank) PDF document in memory
        // We want it to optionally be printable, so add PrintAttributes
        // and use a PrintedPdfDocument. Simpler: new PdfDocument().
        PrintAttributes printAttrs = new PrintAttributes.Builder().
                setColorMode(PrintAttributes.COLOR_MODE_COLOR).
                setMediaSize(PrintAttributes.MediaSize.NA_LETTER).
                setResolution(new PrintAttributes.Resolution("zooey", PRINT_SERVICE, width, height)).
                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
                build();
        PdfDocument document = new PrintedPdfDocument(context, printAttrs);

        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();

        // create a new page from the PageInfo
        PdfDocument.Page page = document.startPage(pageInfo);

        // repaint the user's text into the page
        view.draw(page.getCanvas());

        // do final processing of the page
        document.finishPage(page);

        // Here you could add more pages in a longer doc app, but you'd have
        // to handle page-breaking yourself in e.g., write your own word processor...

        // Now write the PDF document to a file; it actually needs to be a file
        // since the Share mechanism can't accept a byte[]. though it can
        // accept a String/CharSequence. Meh.
        try {
            File pdfDirPath = new File(new File(dir), "pdfs");
            pdfDirPath.mkdirs();
            File file = new File(pdfDirPath, name);
            //Uri contentUri = FileProvider.getUriForFile(context, "com.PoT.Xom", file);
            os = new FileOutputStream(file);
            document.writeTo(os);
            document.close();
            os.close();

            //shareDocument(contentUri, activity);
        } catch (IOException e) {
            throw new RuntimeException("Error generating file", e);
        }
    }

    private void shareDocument(Uri uri, Activity activity) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("application/pdf");
        // Assuming it may go via eMail:
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend");
        // Attach the PDf as a Uri, since Android can't take it as bytes yet.
        mShareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        activity.startActivity(mShareIntent);
        return;
    }

}
