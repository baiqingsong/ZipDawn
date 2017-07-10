package com.dawn.zipdawn;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private String compressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_compress";//压缩文件后的文件夹名称
    private String decompressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_decompress";//解压文件后的文件夹名称
    private String compressZipName = "dawn.zip";//压缩zip的名称
    private String compressRarName = "dawn.rar";//压缩rar的名称

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void FileUnzip(View view){

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        ZipUtil.UnZipFolder(compressDirName + File.separator + compressZipName, decompressDirName);
                        toast("解压完成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
    }
    public void FileZip(View view){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ZipUtil.ZipFolder(decompressDirName, compressDirName + File.separator + compressZipName);
                    toast("压缩完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void FileUnrar(View view){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    ZipUtil.UnRarFolder(compressDirName + File.separator + compressRarName, decompressDirName);
                    toast("解压完成");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    private void toast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
