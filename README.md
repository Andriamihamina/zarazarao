# 📲 LAN Image Transfer App (Android)

An Android app for sharing files over a local network using an embedded **NanoHTTPD** server. 
Designed for seamless image browsing and transfer from your Android phone to any device on the same LAN, via a browser, or another android.

---

## 🚀 Features

- 📡 Serves HTTP & WebSocket endpoints using [NanoHTTPD](https://github.com/NanoHttpd/nanohttpd)
- 🔌 Local network access – no internet required
- ⚡ React + TypeScript frontend (served via NanoHTTPD)

---

## 🛠 Tech Stack

### Android App
- Java (Android SDK)
- NanoHTTPD (HTTP + WebSocket server)
- ViewModel & LiveData (MVVM architecture)
- Gson (JSON serialization)

### Frontend
- Vite + React + TypeScript
- Native `<img loading="lazy">` for efficient thumbnail display
- Infinite scroll with `IntersectionObserver`

---

