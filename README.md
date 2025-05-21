# EasyFTP 📡

**An open-source Android app for fast, local-network file sharing using FTP**

## 🚀 Features

- 📁 **Fast, local file sharing** over Wi-Fi or hotspot (no internet required)
- 📷 **QR code-based connection**: Receiver generates a QR code with its IP, sender scans it
- 🔐 **No sign-in required** – Just connect and transfer
- ⚡ **Lightweight and easy to use**

## 🛠️ How It Works

1. **Connect both devices** to the same Wi-Fi or mobile hotspot.
2. **Receiver** opens the app and taps on `Receive`. It starts an FTP server and shows a QR code with its local IP address.
3. **Sender** taps on `Send`, scans the QR code, selects files, and sends them via FTP.
4. The **receiver** can browse and save incoming files via any FTP client (or integrated view, depending on your version).
5. The files are to be stored in the following directory: Android/data/com.ftp/files

## 📱 Screenshots

<!-- Replace with actual screenshots -->
| Main Menu | Receiver | Sender |
|----------|----------|--------|
| ![Main](https://github.com/AnsaryTanvir/EasyFTP/blob/master/assets/main.png) | ![Receiver](https://github.com/AnsaryTanvir/EasyFTP/blob/master/assets/receiver.png) | ![Sender](https://github.com/AnsaryTanvir/EasyFTP/blob/master/assets//sender.png) |

## 📦 Installation

You can install the APK directly from [Releases](https://github.com/AnsaryTanvir/EasyFTP/releases) or build from source:

### 🔧 Build from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/AnsaryTanvir/EasyFTP.git
   cd EasyFTP
   ```

2. Open the project in **Android Studio**.

3. Click **Run** ▶️ or build the APK via:

   ```
   Build > Build Bundle(s)/APK(s) > Build APK(s)
   ```

## 📄 Permissions Used

- `INTERNET`: For FTP transfer
- `ACCESS_WIFI_STATE`: To check network connection
- `READ/WRITE_EXTERNAL_STORAGE`: To access and transfer files *(required for Android 10 and below)*

## 🔍 Technologies Used

- **Java** – Core programming language
- **FTP Server** – Local file server
- **ZXing** – For QR code generation and scanning
- **Android SDK** – Native components and services

## 🤝 Contribution

Contributions, issues, and feature requests are welcome!  
Feel free to [open an issue](https://github.com/AnsaryTanvir/EasyFTP/issues) or submit a pull request.

## 🙏🙏 Dedicated To
[Dr. Khandoker Nadim Parvez](https://cse.bubt.edu.bd/facultydetails/79/)

## 🙏 Gratitude
MD Sakib Khan 

## 📜 License

This project is licensed under the **MIT License** – see the [LICENSE](LICENSE) file for details.

---

Made with ❤️ by [Ansary Tanvir](https://github.com/AnsaryTanvir)
