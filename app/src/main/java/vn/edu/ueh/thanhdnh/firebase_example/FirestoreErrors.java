package vn.edu.ueh.thanhdnh.firebase_example;

import androidx.annotation.StringRes;

import com.google.firebase.firestore.FirebaseFirestoreException;

// Đổi lỗi Firestore thành câu thông báo cho người dùng.
// Chi tiết kỹ thuật (mã lỗi, document ID...) chỉ ghi vào Logcat, không hiện lên màn hình.
final class FirestoreErrors {
  private FirestoreErrors() {
  }

  @StringRes
  static int messageFor(Exception e, @StringRes int fallback) {
    if (e instanceof FirebaseFirestoreException) {
      switch (((FirebaseFirestoreException) e).getCode()) {
        case PERMISSION_DENIED:
          return R.string.error_permission;   // Rules của database đang chặn
        case UNAVAILABLE:
        case DEADLINE_EXCEEDED:
          return R.string.error_network;      // không tới được máy chủ
        default:
          break;
      }
    }
    return fallback;
  }
}
