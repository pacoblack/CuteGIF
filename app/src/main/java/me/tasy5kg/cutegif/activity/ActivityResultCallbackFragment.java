package me.tasy5kg.cutegif.activity;

import android.net.Uri;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import java.util.List;

public class ActivityResultCallbackFragment extends Fragment {
  void aaa (){
    ActivityResultLauncher<String> pickMultipleImagesLauncher = registerForActivityResult(
      new ActivityResultContracts.GetMultipleContents(),
      new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> result) {
          if (result != null && !result.isEmpty()) {
            for (Uri uri : result) {
              // 处理每个选中的图片 Uri
              System.out.println("Selected Image URI: " + uri.toString());
              // 你可以在这里加载图片或传递给其他 Activity
            }
          }
        }
      }
    );

  }

}
