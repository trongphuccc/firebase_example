package vn.edu.ueh.thanhdnh.firebase_example;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ArticleViewAdapter extends RecyclerView.Adapter<ArticleViewHolder> {
  private LayoutInflater mInflater;
  private List<Article> articles;

  public ArticleViewAdapter(Context context, List<Article> articles) {
    this.mInflater = LayoutInflater.from(context);
    this.articles = articles;
  }

  public void update(List<Article> articles) {
    this.articles = articles;
  }

  @NonNull
  @Override
  public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View customView = mInflater.inflate(R.layout.article_item, parent, false);
    return new ArticleViewHolder(customView);
  }

  @Override
  public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
    Article article = articles.get(position);
    Context context = holder.itemView.getContext();
    // Thẻ số bài viết "#012"; trình đọc màn hình đọc rõ "Bài viết số 12"
    holder.getTxtId().setText(context.getString(R.string.article_tag, article.getArticle_id()));
    holder.getTxtId().setContentDescription(context.getString(R.string.plate_description, article.getArticle_id()));
    holder.getTxtTitle().setText(article.getArticle_title());
    holder.getTxtDescription().setText(article.getArticle_description()); // danh sách chỉ hiện 2 dòng đầu
    holder.getTxtAuthor().setText(authorOf(context, article));
    holder.getTxtAuthor().setContentDescription(context.getString(R.string.author_description, authorOf(context, article)));
    String views = formatViews(article.getArticle_views());
    holder.getTxtViews().setText(views);
    holder.getTxtViews().setContentDescription(context.getString(R.string.views_description, views));
    loadImage(holder.getImgArticle(), article);

    // Bấm vào một bài viết -> mở trang chi tiết riêng
    holder.itemView.setOnClickListener(v -> ArticleDetailActivity.open(v.getContext(), article));
  }

  // Bài cũ chưa có tác giả thì hiện "Chưa rõ tác giả"
  static String authorOf(Context context, Article article) {
    String author = article.getArticle_author();
    return (author == null || author.trim().isEmpty()) ? context.getString(R.string.unknown_author) : author;
  }

  // Định dạng số kiểu Việt Nam: 32125 -> "32.125"
  static String formatViews(int views) {
    return NumberFormat.getIntegerInstance(new Locale("vi", "VN")).format(views);
  }

  // Dùng chung cho danh sách và trang chi tiết: tải ảnh từ URL, xử lý URL rỗng / đang tải / lỗi
  static void loadImage(ImageView img, Article article) {
    img.setContentDescription(img.getContext().getString(R.string.article_image_description, article.getArticle_title()));
    String url = article.getArticle_image();
    if (url == null || url.trim().isEmpty()) {
      // Không có URL: hủy lượt tải cũ của View đang được tái sử dụng rồi hiện biểu tượng "không có ảnh"
      Picasso.get().cancelRequest(img);
      img.setImageResource(R.drawable.ic_image_none);
    } else {
      // into() tự hủy lượt tải cũ và hiện placeholder ngay, nên không lộ ảnh của item trước
      Picasso.get()
          .load(url.trim())
          .placeholder(R.drawable.ic_image_loading) // đang tải
          .error(R.drawable.ic_image_error)         // URL sai hoặc tải lỗi
          .fit()
          .centerCrop()
          .into(img);
    }
  }

  @Override
  public int getItemCount() {
    return articles.size();
  }
}
