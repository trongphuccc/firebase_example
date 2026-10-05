package vn.edu.ueh.thanhdnh.firebase_example;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// Giữ sẵn các View của một dòng bài viết để Adapter không phải findViewById lại mỗi lần
public class ArticleViewHolder extends RecyclerView.ViewHolder {
  private final TextView txtId, txtTitle, txtDescription, txtAuthor, txtViews;
  private final ImageView imgArticle;

  public ArticleViewHolder(@NonNull View itemView) {
    super(itemView);
    txtId = itemView.findViewById(R.id.txt_article_id);
    txtTitle = itemView.findViewById(R.id.txt_article_title);
    imgArticle = itemView.findViewById(R.id.img_article);
    txtDescription = itemView.findViewById(R.id.txt_article_description);
    txtAuthor = itemView.findViewById(R.id.txt_article_author);
    txtViews = itemView.findViewById(R.id.txt_article_views);
  }

  public TextView getTxtId() {
    return txtId;
  }

  public TextView getTxtTitle() {
    return txtTitle;
  }

  public ImageView getImgArticle() {
    return imgArticle;
  }

  public TextView getTxtDescription() {
    return txtDescription;
  }

  public TextView getTxtAuthor() {
    return txtAuthor;
  }

  public TextView getTxtViews() {
    return txtViews;
  }
}
