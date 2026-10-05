package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

// Trang xem chi tiết một bài viết (trang riêng, mở từ danh sách).
// Mỗi lần mở trang, lượt xem của bài viết trên Firestore tăng thêm 1.
public class ArticleDetailActivity extends AppCompatActivity {
  private static final String TAG = "ArticleDetailActivity";
  private static final String EXTRA_DOCUMENT_ID = "document_id";
  private static final String EXTRA_ID = "article_id";
  private static final String EXTRA_TITLE = "article_title";
  private static final String EXTRA_IMAGE = "article_image";
  private static final String EXTRA_DESCRIPTION = "article_description";
  private static final String EXTRA_AUTHOR = "article_author";
  private static final String EXTRA_VIEWS = "article_views";

  private DocumentReference articleRef;          // document của bài viết trên Firestore
  private ListenerRegistration articleListener;  // gỡ ra khi màn hình không còn hiển thị
  private TextView txtId, txtTitle, txtAuthor, txtViews, txtDescription;
  private ImageView imgDetail;

  // Mở trang chi tiết: truyền bài viết qua Intent extras (kèm document ID để tăng lượt xem)
  static void open(Context context, Article article) {
    Intent intent = new Intent(context, ArticleDetailActivity.class);
    intent.putExtra(EXTRA_DOCUMENT_ID, article.getDocumentId());
    intent.putExtra(EXTRA_ID, article.getArticle_id());
    intent.putExtra(EXTRA_TITLE, article.getArticle_title());
    intent.putExtra(EXTRA_IMAGE, article.getArticle_image());
    intent.putExtra(EXTRA_DESCRIPTION, article.getArticle_description());
    intent.putExtra(EXTRA_AUTHOR, article.getArticle_author());
    intent.putExtra(EXTRA_VIEWS, article.getArticle_views());
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
    setContentView(R.layout.activity_article_detail);
    View appBar = findViewById(R.id.app_bar);
    View detailScroll = findViewById(R.id.detail_scroll);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      appBar.setPadding(bars.left, bars.top, bars.right, 0);
      detailScroll.setPadding(bars.left, 0, bars.right, bars.bottom);
      return insets;
    });

    MaterialToolbar toolbar = findViewById(R.id.toolbar);
    toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    txtId = findViewById(R.id.txt_detail_id);
    txtTitle = findViewById(R.id.txt_detail_title);
    txtAuthor = findViewById(R.id.txt_detail_author);
    txtViews = findViewById(R.id.txt_detail_views);
    txtDescription = findViewById(R.id.txt_detail_description);
    imgDetail = findViewById(R.id.img_detail);

    // Hiện ngay dữ liệu nhận từ danh sách, chưa cần chờ Firestore
    Intent intent = getIntent();
    Article article = new Article(
        intent.getIntExtra(EXTRA_ID, 0),
        intent.getStringExtra(EXTRA_TITLE),
        intent.getStringExtra(EXTRA_IMAGE),
        intent.getStringExtra(EXTRA_DESCRIPTION),
        intent.getStringExtra(EXTRA_AUTHOR),
        intent.getIntExtra(EXTRA_VIEWS, 0));
    showArticle(article);

    String documentId = intent.getStringExtra(EXTRA_DOCUMENT_ID);
    if (documentId != null) {
      FirebaseApp.initializeApp(this);
      articleRef = FirebaseFirestore.getInstance().collection("articles").document(documentId);
      // Tăng lượt xem 1 lần khi mở trang (xoay màn hình thì savedInstanceState != null nên không tính lại).
      // increment(1) cộng trên máy chủ nên nhiều người xem cùng lúc vẫn đếm đúng.
      if (savedInstanceState == null) {
        articleRef.update("article_views", FieldValue.increment(1))
            .addOnFailureListener(e -> Log.e(TAG, "Không tăng được lượt xem", e));
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (articleRef == null) {
      return;
    }
    // Nghe document của bài viết: lượt xem / nội dung thay đổi thì trang tự cập nhật
    articleListener = articleRef.addSnapshotListener((snapshot, error) -> {
      if (error != null) {
        Log.e(TAG, "Không đọc được bài viết", error);
        return; // vẫn giữ dữ liệu đang hiển thị
      }
      if (snapshot == null || !snapshot.exists()) {
        Snackbar.make(findViewById(R.id.main), R.string.article_deleted, Snackbar.LENGTH_INDEFINITE).show();
        return;
      }
      try {
        showArticle(snapshot.toObject(Article.class));
      } catch (RuntimeException e) {
        Log.w(TAG, "Bài viết có dữ liệu sai kiểu", e);
      }
    });
  }

  @Override
  protected void onStop() {
    super.onStop();
    if (articleListener != null) {
      articleListener.remove();
      articleListener = null;
    }
  }

  private void showArticle(Article article) {
    txtId.setText(getString(R.string.article_tag, article.getArticle_id()));
    txtId.setContentDescription(getString(R.string.plate_description, article.getArticle_id()));
    txtTitle.setText(article.getArticle_title());
    String author = ArticleViewAdapter.authorOf(this, article);
    txtAuthor.setText(author);
    txtAuthor.setContentDescription(getString(R.string.author_description, author));
    txtViews.setText(getString(R.string.views_count_long, ArticleViewAdapter.formatViews(article.getArticle_views())));
    txtDescription.setText(article.getArticle_description());
    ArticleViewAdapter.loadImage(imgDetail, article);
  }
}
