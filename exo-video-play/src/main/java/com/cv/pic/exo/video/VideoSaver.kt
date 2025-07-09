package com.cv.pic.exo.video

//import org.apache.commons.io.FileUtils;

public class VideoSaver {
//    private static final String SAVE_DIRECTORY = "SavedVideos";
//
//    /**
//     * 保存视频文件到应用专属目录
//     */
//    public static File saveVideo(Context context, File sourceFile, String fileName) throws IOException {
//        File saveDir = new File(context.getExternalFilesDir(null), SAVE_DIRECTORY);
//        if (!saveDir.exists()) {
//            saveDir.mkdirs();
//        }
//
//        File destFile = new File(saveDir, fileName);
//        FileUtils.copyFile(sourceFile, destFile);
//        return destFile;
//    }
//
//    /**
//     * 获取所有已保存的视频
//     */
//    public static List<File> getSavedVideos(Context context) {
//        File saveDir = new File(context.getExternalFilesDir(null), SAVE_DIRECTORY);
//        if (saveDir.exists() && saveDir.isDirectory()) {
//            File[] files = saveDir.listFiles();
//            if (files != null) {
//                return Arrays.asList(files);
//            }
//        }
//        return Collections.emptyList();
//    }
//
//    /**
//     * 生成唯一的视频文件名
//     */
//    public static String generateVideoFileName(Uri videoUri, String type) {
//        String baseName = videoUri.getLastPathSegment();
//        if (baseName == null) {
//            baseName = "video";
//        } else {
//            // 移除扩展名
//            int dotIndex = baseName.lastIndexOf('.');
//            if (dotIndex > 0) {
//                baseName = baseName.substring(0, dotIndex);
//            }
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//        String timestamp = sdf.format(new Date());
//
//        return baseName + "_" + timestamp + "." + type;
//    }
}