# 文件压缩和解压的使用

* [解压](#解压)
* [压缩](#压缩)


## 解压
ZipUtil类中UnZipFolder个方法，对zip压缩文件进行解压
```
/**
 * 解压一个压缩文档 到指定位置
 * @param zipFileString 压缩包的名字
 * @param outPathString 指定的路径
 * @throws Exception
 */
public static void UnZipFolder(String zipFileString, String outPathString)throws Exception {
    android.util.Log.i("XZip", "UnZipFolder(String, String)");
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
```
其中第一个参数传要解压的zip文件路径，第二个参数传解压位置的文件夹路径（可以不存在）
```
private String compressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_compress";//压缩文件后的文件夹名称
private String decompressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_decompress";//解压文件后的文件夹名称
private String compressZipName = "dawn.zip";//压缩zip的名称
new Thread(){
    @Override
    public void run() {
        super.run();
        try {
            ZipUtil.UnZipFolder(compressDirName + File.separator + compressZipName, decompressDirName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}.start();
```
注：第二次解压，如果压缩包里面的名称相同会自动覆盖，不同会创建
支持中文


## 压缩
ZipUtil类中ZipFolder个方法，对文件夹或者文件进行压缩
```
/**
 * 压缩文件,文件夹
 * @param srcFileString 要压缩的文件/文件夹名字
 * @param zipFileString 指定压缩的目的和名字
 * @throws Exception
 */
public static void ZipFolder(String srcFileString, String zipFileString)throws Exception {
    android.util.Log.v("XZip", "ZipFolder(String, String)");

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
    android.util.Log.v("XZip", "ZipFiles(String, String, ZipOutputStream)");

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
```
其中第一个参数是要压缩的文件或者文件夹的地址，第二个参数是压缩后的文件地址（名称，不仅仅可以用zip格式）
```
private String compressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_compress";//压缩文件后的文件夹名称
private String decompressDirName = Environment.getExternalStorageDirectory().getPath() + "/dawn_decompress";//解压文件后的文件夹名称
private String compressZipName = "dawn.zip";//压缩zip的名称
new Thread(){
    @Override
    public void run() {
        super.run();
        try {
            ZipUtil.ZipFolder(decompressDirName, compressDirName + File.separator + compressZipName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}.start();
```
注：两个参数都是完整路径，并且压缩的类型不仅限于zip格式
支持中文
