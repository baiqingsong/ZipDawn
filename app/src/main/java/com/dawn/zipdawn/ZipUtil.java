package com.dawn.zipdawn;

import android.util.Log;

import com.dawn.zipdawn.rar.Archive;
import com.dawn.zipdawn.rar.rarfile.FileHeader;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by 90449 on 2017/7/10.
 */

public class ZipUtil {


    /**
     * 解压一个压缩文档 到指定位置
     * @param zipFileString 压缩包的名字
     * @param outPathString 指定的路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString)throws Exception {
        Log.i("UnZipFolder", "zipFileString = " + zipFileString + " outPathString = " + outPathString);
        java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
        java.util.zip.ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();

            if (zipEntry.isDirectory()) {

                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                java.io.File folder = new java.io.File(outPathString + java.io.File.separator + szName);
                folder.mkdirs();

            } else {
                java.io.File file = new java.io.File(outPathString + java.io.File.separator + szName);
                file.createNewFile();
                // get the output stream of the file
                java.io.FileOutputStream out = new java.io.FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }//end of while

        inZip.close();

    }//end of func


    /**
     * 压缩文件,文件夹
     * @param srcFileString 要压缩的文件/文件夹名字
     * @param zipFileString 指定压缩的目的和名字
     * @throws Exception
     */
    public static void ZipFolder(String srcFileString, String zipFileString)throws Exception {
        Log.i("ZipFolder", "srcFileString = " + srcFileString + " zipFileString = " + zipFileString);

        //创建Zip包
        java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFileString));

        //打开要输出的文件
        java.io.File file = new java.io.File(srcFileString);

        //压缩
        ZipFiles(file.getParent()+java.io.File.separator, file.getName(), outZip);

        //完成,关闭
        outZip.finish();
        outZip.close();

    }//end of func

    /**
     * 压缩文件
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, java.util.zip.ZipOutputStream zipOutputSteam)throws Exception{
        Log.i("ZipFiles", "folderString = " + folderString + " fileString = " + fileString);

        if(zipOutputSteam == null)
            return;

        java.io.File file = new java.io.File(folderString+fileString);

        //判断是不是文件
        if (file.isFile()) {

            java.util.zip.ZipEntry zipEntry =  new java.util.zip.ZipEntry(fileString);
            java.io.FileInputStream inputStream = new java.io.FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);

            int len;
            byte[] buffer = new byte[4096];

            while((len=inputStream.read(buffer)) != -1)
            {
                zipOutputSteam.write(buffer, 0, len);
            }

            zipOutputSteam.closeEntry();
        }
        else {

            //文件夹的方式,获取文件夹下的子文件
            String fileList[] = file.list();

            //如果没有子文件, 则添加进去即可
            if (fileList.length <= 0) {
                java.util.zip.ZipEntry zipEntry =  new java.util.zip.ZipEntry(fileString+java.io.File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }

            //如果有子文件, 遍历子文件
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString+java.io.File.separator+fileList[i], zipOutputSteam);
            }//end of for

        }//end of if

    }//end of func


    /**
     * 解压rar文件
     * @param zipFileString
     * @param outPathString
     * @return
     */
    public static String UnRarFolder(String zipFileString, String outPathString) {
        Log.i("UnRarFolder", "start time = " + System.currentTimeMillis() + "");
        Log.i("UnRarFolder", "zipFileString = " + zipFileString + " outPathString = " + outPathString);
        File srcFile = new File(zipFileString);
        if (null == outPathString || "".equals(outPathString)) {
            outPathString = srcFile.getParentFile().getPath();
        }

        FileOutputStream fileOut = null;
        Archive rarfile = null;

        try {
            rarfile = new Archive(srcFile);
            FileHeader fh = null;
            final int total = rarfile.getFileHeaders().size();
            for (int i = 0; i < rarfile.getFileHeaders().size(); i++) {
                fh = rarfile.getFileHeaders().get(i);
                String entrypath = "";
                if (fh.isUnicode()) {//解決中文乱码
                    entrypath = fh.getFileNameW().trim();
                } else {
                    entrypath = fh.getFileNameString().trim();
                }
                entrypath = entrypath.replaceAll("\\\\", "/");

                File file = new File(outPathString, entrypath);
                Log.i("UnRarFolder", "unrar entry file :" + file.getPath());

                if (fh.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    rarfile.extractFile(fh, fileOut);
                    fileOut.close();
                }
            }
            rarfile.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                    fileOut = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rarfile != null) {
                try {
                    rarfile.close();
                    rarfile = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("UnRarFolder", "end time = " + System.currentTimeMillis() + "");
        return outPathString;
    }

}
