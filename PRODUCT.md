# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

## Users
Primary: the student author, who demos the app on an Android emulator for a lecturer grading the "Phát triển ứng dụng mobile" Firestore assignment (enter a few articles, show the list, prove the data lives in Cloud Firestore). Secondary (confirmed): the same app should be good enough for a person who writes or edits short articles and rereads the list regularly.

## Product Purpose
Enter an article (numeric article ID, title, optional image URL, description) and store it in the Cloud Firestore collection `articles`; read the collection back live and show every article with its ID, title, image and description. Success: a save is acknowledged clearly, the list updates by itself, and every failure (invalid input, offline, permission rules, broken image URL) is explained without crashing.

## Operating Context
Coursework fork of the lecturer's sample repo (Activity + Adapter + ViewHolder structure). Demonstrated on a Pixel 7 emulator; data checked in the Firebase Console (Firestore Data tab, project ContactCloud). Three screens: entry form (MainActivity, portrait and landscape layouts), live list (ShowDataActivity with RecyclerView), and a separate article detail page (ArticleDetailActivity) opened from the list. The user decided list and detail must be separate pages, matching the earlier PhotoApp assignment (grid + ViewArticleActivity).

## Capabilities and Constraints
- Java + XML only; no new libraries. Available: AndroidX AppCompat, Material Components 1.12 (Material 3 theme), ConstraintLayout, RecyclerView, Cloud Firestore 25.1.1, Picasso 2.8.
- Firestore keys: `article_id`, `article_title`, `article_image`, `article_description`, plus `article_author` and `article_views` (added at the user's request). `article_id` is user-entered and distinct from the Firestore document ID; the document ID is read via @DocumentId only to increment views and is never stored as a field or shown.
- Opening an article's detail page increments `article_views` by 1 (FieldValue.increment), not on rotation.
- List and detail are separate pages (user decision). No edit/delete, search, sign-in, or image upload (assignment scope).
- User-facing feedback: Material Snackbar, short and friendly; never show the Firestore document ID or raw exception text. Technical details go to Logcat only.
- All UI text in Vietnamese.

## Brand Commitments
The app follows the user's own website GameNexus (gamenexus.great-site.net): its CSS tokens (bg #0D0E11, card #161820, accent #E63946, text #EAEAEA / #A1A6B3), Exo 2 display + Roboto body, 12/16px radii, red pill labels, 16:9 cover images, author + views meta row. Always dark. Earlier pastel/cobalt and cut-corner esports directions were superseded.

## Evidence on Hand
Real articles are whatever the user enters; image URLs come from the web (e.g. picsum.photos). No logo or brand assets exist.

## Product Principles
1. The state of the data is always legible: saving, saved, offline, denied, empty are each said plainly.
2. The form forgives: errors sit next to the field that caused them and input is never lost on failure.
3. The list is for reading: title and image lead, the article ID is reference information.
4. Stay inside the course toolkit: everything is achievable with the libraries already in the project.
