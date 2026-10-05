---
name: Bài viết Firestore
description: Follows the user's GameNexus site. Dark news cards, red pill labels, Exo 2 headlines.
colors:
  bg-nav: "#0A0B0D"
  bg-dark: "#0D0E11"
  bg-card: "#161820"
  bg-raised: "#1F2230"
  border: "#14FFFFFF"
  field-outline: "#6E7385"
  text-primary: "#EAEAEA"
  text-muted: "#A1A6B3"
  accent: "#E63946"
  accent-fill: "#D62839"
  accent-pressed: "#C1121F"
  error: "#EF4444"
typography:
  toolbar:
    fontFamily: "Exo 2 (Google Fonts, downloadable)"
    fontSize: "22sp"
    fontWeight: 700
  detail-title:
    fontFamily: "Exo 2"
    fontSize: "24sp"
    fontWeight: 900
  heading:
    fontFamily: "Exo 2"
    fontSize: "17sp"
    fontWeight: 700
  chip:
    fontFamily: "Exo 2, uppercase"
    fontSize: "12sp"
    fontWeight: 700
    letterSpacing: "0.06em"
  body:
    fontFamily: "Roboto"
    fontSize: "14-16sp"
    fontWeight: 400
  meta:
    fontFamily: "Roboto"
    fontSize: "13sp"
    fontWeight: 400
rounded:
  control: "12dp"
  card: "16dp"
  chip: "999dp"
spacing:
  xs: "4dp"
  sm: "8dp"
  md: "16dp"
  lg: "24dp"
components:
  button-primary:
    backgroundColor: "{colors.accent-fill}"
    textColor: "#FFFFFF"
    rounded: "{rounded.control}"
    height: "52dp"
  button-outlined:
    textColor: "{colors.text-primary}"
    rounded: "{rounded.control}"
    height: "52dp"
  text-field:
    backgroundColor: "{colors.bg-card}"
    rounded: "{rounded.control}"
  chip:
    backgroundColor: "{colors.accent-fill}"
    textColor: "#FFFFFF"
    typography: "{typography.chip}"
    rounded: "{rounded.chip}"
  article-card:
    backgroundColor: "{colors.bg-card}"
    rounded: "{rounded.card}"
---

# DESIGN.md

Android, Material 3 views (Java + XML), always dark. The visual system is taken from the user's own website GameNexus (its CSS variables), so app and site read as one product. Tokens: `res/values/colors.xml`, `res/values/themes.xml`; fonts: `res/font/exo_2_bold.xml`, `exo_2_black.xml` (Google Fonts downloadable fonts, no files or libraries; system fallback without Play services).

## Overview

A dark game-news look: near-black ground, slightly lighter cards with a hairline border, a red pill label on every article ("#012", the user's article number), Exo 2 headlines, Roboto reading text, and a meta row with author and view count. Layout, navigation and controls are standard Material 3.

## Colors

- Site tokens: bg-nav, bg-dark, bg-card, border, text-primary, text-muted, accent.
- `accent` (#E63946) is used where no white text sits on it: focused field outline, progress, small rules. Filled red surfaces with white text use `accent-fill` (#D62839, 4.97:1); white on #E63946 is only 4.17:1.
- Field outline #6E7385 (3.8–4.1:1) replaces the site's 8% white border on inputs so field edges stay visible.
- Measured: text-primary on bg 16.0:1, text-muted on card 7.3:1, error on bg 5.1:1.

## Typography

Exo 2 bold for app bar titles, card titles, captions and buttons; Exo 2 black for the detail title and the large number preview; uppercase tracked Exo 2 for pill labels; Roboto for descriptions, helper text and meta. All in sp.

## Layout

- Edge-to-edge with light system-bar icons; insets (status, navigation, cutout, keyboard) applied in Java.
- Form: 16dp gutters (24dp landscape, two columns, identical view IDs). Fields: number, title, author, image URL, content.
- List: one column of cards, 16dp margins, 8dp gaps, sorted by article number. Card: 16:9 cover image (ConstraintLayout dimension ratio + Picasso fit/centerCrop), 16dp content padding.
- Detail: 16:9 image rounded 12dp, label, title, meta, 1dp rule, full description at 1.4 line spacing.

## Elevation & Depth

Flat. Cards are lighter tone plus a 1dp 8%-white border; no shadows or glows.

## Shapes

Rounded, from the site: 12dp controls and images, 16dp cards, full pill for labels.

## Components

- **Pill label**: `#%03d` of article_id, screen readers hear "Bài viết số N". On the form it previews the typed number, grey `#???` until valid.
- **Meta row**: person icon + author ("Chưa rõ tác giả" when missing), eye icon + views formatted vi-VN ("32.125"); detail adds "lượt xem".
- **Views**: opening the detail page runs `FieldValue.increment(1)` on `article_views` once (not on rotation); the detail page listens to its document so the number updates live, and the list updates when you return.
- **Feedback**: Snackbar only; no Firestore document IDs or raw exceptions on screen.
- **States**: loading, empty (with add button), offline, read error; image states loading / no URL / failed differ by icon shape.
- **Author field**: plain text with word capitalisation and autofill disabled, so the device account name is not offered over the next field.

## Do's and Don'ts

- Do keep the app aligned with the GameNexus tokens; do keep all text in Vietnamese.
- Don't put white text on #E63946; use accent-fill.
- Don't show document IDs; don't add cut corners, glows or a second accent colour.
