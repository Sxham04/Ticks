Built to remind me of the passing days, one dot at a time. Each filled dot a day you won't get back.  
days pass. you don't notice. then suddenly you're out of them.

# Ticks

A minimalist Android home screen widget for tracking date ranges — built around a dot-grid visualization inspired by GitHub's contribution graph.

---

## What it does

Ticks lets you define a start and end date, then renders a compact dot matrix on your home screen where each dot represents one day. Completed days are filled, future days are dimmed, and today is highlighted in green.

The full-screen view renders the entire range as a month-by-month calendar grid, similar to a LeetCode activity matrix.

---

## Features

- **Dot grid widget** — compact, resizable home screen widget showing your date range at a glance
- **Today indicator** — green dot marking the current day in both widget and full-screen view
- **Month-grid calendar** — full-screen view with month labels, scrollable for long ranges
- **Midnight updates** — widget redraws automatically at midnight, keeping the grid current
- **Resizable** — drag to resize the widget horizontally or vertically; grid redraws to fit
- **Multiple widgets** — place several Ticks widgets simultaneously, each with its own date range
- **Progress footer** — shows days passed, total days, and percentage complete

---

## Screenshots



---

## Installation

1. Download the latest APK from [Releases](https://github.com/Sxham04/Ticks/releases)
2. On your Android device, enable **Install unknown apps** for your browser or file manager
3. Open the APK and install
4. Long-press your home screen → Widgets → find **Ticks** → drag to place
5. Set your start and end date when prompted

**Minimum Android version:** Android 8.0 (API 26)

---

## Built with

- Kotlin
- Android Canvas API for dot grid rendering
- RecyclerView + GridLayoutManager for the full-screen calendar
- SharedPreferences for per-widget data storage
- AppWidgetProvider for home screen widget lifecycle

---

## License

MIT — use it, fork it, build on it.
