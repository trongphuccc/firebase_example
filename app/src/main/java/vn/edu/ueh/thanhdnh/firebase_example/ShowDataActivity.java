package vn.edu.ueh.thanhdnh.firebase_example;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity {
  private static final String TAG = "ShowDataActivity";

  FirebaseFirestore db;
  RecyclerView recyclerView;
  MaterialToolbar toolbar;
  View stateView, progressLoading, btStateAdd;
  ImageView imgState;
  TextView txtStateTitle, txtStateMessage;
  List<Article> articles = new ArrayList<>();
  ArticleViewAdapter adapter;
  // Giữ lại đăng ký để gỡ listener khi màn hình không còn hiển thị
  private ListenerRegistration articlesListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // App luôn ở giao diện tối nên biểu tượng status bar và thanh điều hướng dùng màu sáng
    EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
    setContentView(R.layout.activity_show_data);
    View appBar = findViewById(R.id.app_bar);
    recyclerView = findViewById(R.id.reclyclerview);
    int listBottomPadding = recyclerView.getPaddingBottom();
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      appBar.setPadding(bars.left, bars.top, bars.right, 0);
      // Danh sách cuộn tới sát đáy màn hình nhưng thẻ cuối không bị thanh điều hướng che
      recyclerView.setPadding(bars.left, recyclerView.getPaddingTop(), bars.right, listBottomPadding + bars.bottom);
      return insets;
    });

    FirebaseApp.initializeApp(this);

    toolbar = findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    stateView = findViewById(R.id.state_view);
    progressLoading = findViewById(R.id.progress_loading);
    imgState = findViewById(R.id.img_state);
    txtStateTitle = findViewById(R.id.txt_state_title);
    txtStateMessage = findViewById(R.id.txt_state_message);
    btStateAdd = findViewById(R.id.bt_state_add);
    btStateAdd.setOnClickListener(v -> finish()); // quay về form để thêm bài viết

    adapter = new ArticleViewAdapter(this, articles);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(adapter);

    db = FirebaseFirestore.getInstance();
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (articles.isEmpty()) {
      showLoading();
    }
    // Nghe collection "articles": nhận dữ liệu lần đầu và mỗi khi có thêm/sửa/xóa.
    // INCLUDE để biết cả lúc dữ liệu chuyển từ bộ nhớ đệm sang dữ liệu thật từ máy chủ.
    articlesListener = db.collection("articles").orderBy("article_id")
        .addSnapshotListener(MetadataChanges.INCLUDE, (snapshots, error) -> {
          if (error != null) {
            Log.e(TAG, "Không đọc được collection articles", error);
            articles.clear();
            adapter.notifyDataSetChanged();
            toolbar.setSubtitle(null);
            showState(R.drawable.ic_state_error, R.string.load_error_title,
                getString(FirestoreErrors.messageFor(error, R.string.load_failed)), false);
            return;
          }
          articles.clear();
          for (QueryDocumentSnapshot q : snapshots) {
            try {
              articles.add(q.toObject(Article.class));
            } catch (RuntimeException e) {
              // Bài viết sai kiểu dữ liệu (vd. sửa tay trên Console): bỏ qua thay vì làm app crash
              Log.w(TAG, "Bỏ qua một bài viết có dữ liệu sai kiểu", e);
            }
          }
          adapter.update(articles);
          adapter.notifyDataSetChanged();

          boolean offline = snapshots.getMetadata().isFromCache(); // chưa xác nhận được với máy chủ
          if (!articles.isEmpty()) {
            toolbar.setSubtitle(getString(offline ? R.string.article_count_offline : R.string.article_count, articles.size()));
            stateView.setVisibility(View.GONE);
          } else if (offline) {
            toolbar.setSubtitle(null);
            showState(R.drawable.ic_state_offline, R.string.offline_title, getString(R.string.offline_message), false);
          } else {
            toolbar.setSubtitle(null);
            showState(R.drawable.ic_state_empty, R.string.empty_title, getString(R.string.empty_message), true);
          }
        });
  }

  @Override
  protected void onStop() {
    super.onStop();
    // Gỡ listener: không đăng ký trùng khi quay lại và không giữ màn hình đã đóng
    if (articlesListener != null) {
      articlesListener.remove();
      articlesListener = null;
    }
  }

  private void showLoading() {
    progressLoading.setVisibility(View.VISIBLE);
    imgState.setVisibility(View.GONE);
    txtStateTitle.setText(R.string.loading);
    txtStateMessage.setVisibility(View.GONE);
    btStateAdd.setVisibility(View.GONE);
    stateView.setVisibility(View.VISIBLE);
  }

  private void showState(@DrawableRes int icon, @StringRes int title, String message, boolean offerAdd) {
    progressLoading.setVisibility(View.GONE);
    imgState.setImageResource(icon);
    imgState.setVisibility(View.VISIBLE);
    txtStateTitle.setText(title);
    txtStateMessage.setText(message);
    txtStateMessage.setVisibility(View.VISIBLE);
    btStateAdd.setVisibility(offerAdd ? View.VISIBLE : View.GONE);
    stateView.setVisibility(View.VISIBLE);
  }
}
