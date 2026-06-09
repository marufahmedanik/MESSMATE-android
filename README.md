# MESSMATE 2.0 🍲💰

> A comprehensive Android application designed to simplify hostel and mess management. Built with Java and powered by Firebase, MessMate 2.0 provides a seamless experience for both administrators and regular members to track meals, manage payments, and monitor due amounts.

---

## 🚀 Features

### 🔐 Role-Based Access
- **Admin Dashboard:** Exclusive controls for managers to oversee the mess, add meals, and confirm payments.
- **Member Dashboard:** Personalized view for residents to track their own meals, dues, and transaction history.
- **Secure Authentication:** Email/Password and Google Sign-In support powered by Firebase Auth.

### 🍲 Meal Management
- **Daily Meal Logging:** Keep accurate records of meals consumed per member daily.
- **Meal Lists:** View entire meal histories to ensure transparent tracking for everyone in the mess.

### 💰 Financial Tracking
- **Due Amount Calculation:** Automated, real-time calculations of how much each member owes based on the current meal rate and their deposits.
- **Payment History:** A dedicated ledger for members to view past payments.
- **Transaction Confirmations:** Admins can verify and confirm member payments directly through the app.

### 👥 User Ecosystem
- **Member List view:** Admins can view and manage all active mess members in one place.
- **Profile Management:** Users can update their personal information securely.

---

## 🛠️ Tech Stack & Technologies Used

- **Language:** Java
- **UI/UX:** XML (Android Standard Layouts), Material Design Components
- **Backend/Database:** 
  - Firebase Authentication (Credentials & Google Sign-In)
  - Firebase Realtime Database
  - Firebase Firestore
- **Architecture/Tools:** 
  - Android SDK (Min 24, Target 36)
  - Gradle (KTS)

---

## ⚙️ Prerequisites

To run this project locally, you will need:
1. **Android Studio** (Latest stable version recommended)
2. **Java Development Kit (JDK) 11** or higher.
3. An active **Firebase Project**.

---

## 💻 Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/MESSMATE2.0.git
   ```

2. **Open the Project in Android Studio:**
   - Launch Android Studio.
   - Click on `Open an existing Android Studio project`.
   - Navigate to the cloned `MESSMATE2.0` directory and select it.

3. **Configure Firebase:**
   - Go to your [Firebase Console](https://console.firebase.google.com/).
   - Add a new Android app to your project with the package name `ewubd.edu.messmate`.
   - Download the generated `google-services.json` file.
   - Place the `google-services.json` file inside the `app/` directory of this project.
   - Enable **Authentication** (Email/Password & Google), **Firestore**, and **Realtime Database** in your Firebase console.

4. **Sync and Build:**
   - Let Android Studio sync the Gradle files.
   - Click the **Run** button (green play icon) to build and deploy the app to your emulator or physical Android device.

---

## 📸 Screenshots
*(You can add your app screenshots here by placing images in an `assets` folder and linking them like this: `![Dashboard](assets/dashboard.png)`)*

---

## 🤝 Contributing
Contributions, issues, and feature requests are welcome! 
Feel free to check the [issues page](../../issues).

---

## 📄 License
[MIT](https://choosealicense.com/licenses/mit/) *(Update this if you are using a different license)*
