package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private static final String TAG = "MainActivity";

  FirebaseFirestore db;
  Button btAdd, btShow;
  EditText etArticleId, etArticleTitle, etArticleAuthor, etArticleImage, etArticleDescription;
  TextInputLayout tilArticleId, tilArticleTitle, tilArticleAuthor, tilArticleImage, tilArticleDescription;
  TextView txtPlatePreview;
  LinearProgressIndicator progressSaving;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // App luôn ở giao diện tối nên biểu tượng status bar và thanh điều hướng dùng màu sáng
    EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT));
    setContentView(R.layout.activity_main);
    View appBar = findViewById(R.id.app_bar);
    View formScroll = findViewById(R.id.form_scroll);
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
      Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
      appBar.setPadding(bars.left, bars.top, bars.right, 0);
      // Chừa chỗ cho thanh điều hướng và bàn phím để ô nhập cuối không bị che
      formScroll.setPadding(bars.left, 0, bars.right, Math.max(bars.bottom, ime.bottom));
      return insets;
    });

    FirebaseApp.initializeApp(this);
    db = FirebaseFirestore.getInstance();
    btAdd = findViewById(R.id.btAdd);
    btShow = findViewById(R.id.btShow);
    etArticleId = findViewById(R.id.etArticleId);
    etArticleTitle = findViewById(R.id.etArticleTitle);
    etArticleAuthor = findViewById(R.id.etArticleAuthor);
    etArticleImage = findViewById(R.id.etArticleImage);
    etArticleDescription = findViewById(R.id.etArticleDescription);
    tilArticleId = findViewById(R.id.til_article_id);
    tilArticleTitle = findViewById(R.id.til_article_title);
    tilArticleAuthor = findViewById(R.id.til_article_author);
    tilArticleImage = findViewById(R.id.til_article_image);
    tilArticleDescription = findViewById(R.id.til_article_description);
    txtPlatePreview = findViewById(R.id.txt_plate_preview);
    progressSaving = findViewById(R.id.progress_saving);
    btAdd.setOnClickListener(this);
    btShow.setOnClickListener(this);

    // Biển số xem trước đổi theo số đang nhập; sửa ô nào thì xóa lỗi của ô đó
    etArticleId.addTextChangedListener(new AfterTextChanged() {
      @Override
      public void afterTextChanged(Editable s) {
        tilArticleId.setError(null);
        updatePlatePreview();
      }
    });
    clearErrorOnEdit(tilArticleTitle);
    clearErrorOnEdit(tilArticleAuthor);
    clearErrorOnEdit(tilArticleImage);
    clearErrorOnEdit(tilArticleDescription);
    updatePlatePreview();
  }

  @Override
  public void onClick(View view) {
    if (view.getId() == R.id.btAdd) {
      addArticle();
    } else if (view.getId() == R.id.btShow) {
      openList();
    }
  }

  private void addArticle() {
    final int articleId = parseArticleId(text(etArticleId));
    String title = text(etArticleTitle);
    String author = text(etArticleAuthor);
    String image = text(etArticleImage);
    String description = text(etArticleDescription);

    // Kiểm tra mọi ô cùng lúc: lỗi hiện ngay dưới ô sai, con trỏ nhảy tới ô sai đầu tiên
    TextInputLayout firstInvalid = null;
    if (articleId <= 0) {
      firstInvalid = markInvalid(tilArticleId, R.string.error_article_id, firstInvalid);
    }
    if (title.isEmpty()) {
      firstInvalid = markInvalid(tilArticleTitle, R.string.error_title_required, firstInvalid);
    }
    if (author.isEmpty()) {
      firstInvalid = markInvalid(tilArticleAuthor, R.string.error_author_required, firstInvalid);
    }
    // URL ảnh không bắt buộc, nhưng nếu có thì phải là đường dẫn http/https hợp lệ
    if (!image.isEmpty() && !(URLUtil.isNetworkUrl(image) && Patterns.WEB_URL.matcher(image).matches())) {
      firstInvalid = markInvalid(tilArticleImage, R.string.error_image_url, firstInvalid);
    }
    if (description.isEmpty()) {
      firstInvalid = markInvalid(tilArticleDescription, R.string.error_description_required, firstInvalid);
    }
    if (firstInvalid != null) {
      firstInvalid.getEditText().requestFocus();
      return;
    }

    setSaving(true); // khóa nút trong lúc đang lưu để không gửi lặp

    // add(): Firestore tự tạo document ID; article_id chỉ là một trường bên trong document.
    // Bài mới bắt đầu với 0 lượt xem.
    db.collection("articles").add(new Article(articleId, title, image, description, author, 0))
        .addOnSuccessListener(ref -> {
          Log.i(TAG, "Đã lưu bài viết số " + articleId);
          showMessage(getString(R.string.save_success, articleId), true);
          clearForm(); // chỉ xóa form khi đã lưu thành công
        })
        .addOnFailureListener(e -> {
          Log.e(TAG, "Lưu bài viết số " + articleId + " thất bại", e);
          showMessage(getString(FirestoreErrors.messageFor(e, R.string.save_failed)), false);
        })
        .addOnCompleteListener(task -> setSaving(false));
  }

  private void openList() {
    startActivity(new Intent(this, ShowDataActivity.class));
  }

  private void setSaving(boolean saving) {
    btAdd.setEnabled(!saving);
    btAdd.setText(saving ? R.string.saving : R.string.add_article);
    if (saving) {
      progressSaving.show();
    } else {
      progressSaving.hide();
    }
  }

  // Snackbar ngắn gọn; khi lưu thành công có thêm nút "Xem" để mở danh sách
  private void showMessage(String message, boolean offerViewList) {
    Snackbar snackbar = Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG);
    if (offerViewList) {
      snackbar.setAction(R.string.snackbar_view, v -> openList());
    }
    snackbar.show();
  }

  // Nhãn số xem trước: số hợp lệ thì nhãn đỏ "#012", chưa hợp lệ thì nhãn xám "#???"
  private void updatePlatePreview() {
    int articleId = parseArticleId(text(etArticleId));
    boolean valid = articleId > 0;
    txtPlatePreview.setText(valid ? getString(R.string.article_tag, articleId) : getString(R.string.tag_placeholder));
    txtPlatePreview.setTextColor(ContextCompat.getColor(this, valid ? R.color.white : R.color.text_muted));
    txtPlatePreview.setBackgroundTintList(ColorStateList.valueOf(
        ContextCompat.getColor(this, valid ? R.color.accent_fill : R.color.bg_raised)));
    txtPlatePreview.setContentDescription(valid
        ? getString(R.string.plate_description, articleId)
        : getString(R.string.plate_description_empty));
  }

  private TextInputLayout markInvalid(TextInputLayout field, @StringRes int message, TextInputLayout firstInvalid) {
    field.setError(getString(message));
    return firstInvalid != null ? firstInvalid : field;
  }

  private void clearForm() {
    etArticleId.setText("");
    etArticleTitle.setText("");
    etArticleAuthor.setText("");
    etArticleImage.setText("");
    etArticleDescription.setText("");
    etArticleId.requestFocus();
  }

  private void clearErrorOnEdit(TextInputLayout field) {
    field.getEditText().addTextChangedListener(new AfterTextChanged() {
      @Override
      public void afterTextChanged(Editable s) {
        field.setError(null);
      }
    });
  }

  // Rỗng, không phải số hoặc vượt quá phạm vi int đều trả về 0 (không hợp lệ)
  private static int parseArticleId(String text) {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  private static String text(EditText field) {
    return field.getText().toString().trim();
  }

  // TextWatcher chỉ cần afterTextChanged
  private abstract static class AfterTextChanged implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
  }
}
