package vn.edu.ueh.thanhdnh.firebase_example;

import com.google.firebase.firestore.DocumentId;

// Một bài viết trong collection "articles" của Firestore.
// Firestore đặt tên key theo getter (getArticle_id -> "article_id", ...), nên tên getter/setter
// phải giữ đúng như dưới đây thì key trên Firestore mới là article_id, article_title, ...
// article_id là mã bài viết do người dùng nhập, KHÁC với document ID mà Firestore tự tạo khi add().
public class Article {
  private int article_id;
  private String article_title;
  private String article_image;       // URL ảnh trên Internet
  private String article_description;
  private String article_author;      // tác giả
  private int article_views;          // lượt xem, tăng 1 mỗi lần mở trang chi tiết

  // Document ID do Firestore tạo: tự điền khi đọc (toObject), KHÔNG được ghi lên Firestore
  // và không hiển thị cho người dùng; chỉ dùng để biết cần tăng lượt xem cho document nào.
  @DocumentId
  private String documentId;

  // Firestore cần constructor rỗng để chuyển document thành đối tượng (toObject)
  public Article() {
  }

  public Article(int article_id, String article_title, String article_image, String article_description,
                 String article_author, int article_views) {
    this.article_id = article_id;
    this.article_title = article_title;
    this.article_image = article_image;
    this.article_description = article_description;
    this.article_author = article_author;
    this.article_views = article_views;
  }

  public int getArticle_id() {
    return article_id;
  }

  public void setArticle_id(int article_id) {
    this.article_id = article_id;
  }

  public String getArticle_title() {
    return article_title;
  }

  public void setArticle_title(String article_title) {
    this.article_title = article_title;
  }

  public String getArticle_image() {
    return article_image;
  }

  public void setArticle_image(String article_image) {
    this.article_image = article_image;
  }

  public String getArticle_description() {
    return article_description;
  }

  public void setArticle_description(String article_description) {
    this.article_description = article_description;
  }

  public String getArticle_author() {
    return article_author;
  }

  public void setArticle_author(String article_author) {
    this.article_author = article_author;
  }

  public int getArticle_views() {
    return article_views;
  }

  public void setArticle_views(int article_views) {
    this.article_views = article_views;
  }

  public String getDocumentId() {
    return documentId;
  }

  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }
}
