# 🚜 Farm Equipment Sharing Mobile App

Farm Equipment Sharing is an Android app designed to bridge the gap between farmers and vendors by providing a platform where farmers can rent, lease, or purchase farming equipment with ease. This app allows farmers to rent out their equipment to other farmers, and vendors can list equipment for rent or sale. Users can also find equipment based on type, location, price, and availability.

## 📱 Features

- **Location-based Equipment Search**: Users can search for equipment by type, location, name, description, price, and available date range.
- **Nearby Equipment Suggestions**: The app suggests available equipment within a 10-20 km radius.
- **User Role Options**: Allows two primary user roles - **Farmers** and **Vendors**.
    - Farmers can:
      - Rent equipment from other farmers.
      - List their equipment for rent.
    - Vendors can:
      - Rent or sell new equipment.
- **Order and Transaction History**: Users can view order details and transaction history within the app.
- **Notifications**: Users receive real-time notifications for new listings, order updates, and messages.
- **Security and Authentication**: Users authenticate with a secure system.
- **Multiple Language Support**: The app supports multiple languages to cater to a diverse user base.

## 🛠️ Tech Stack

- **Frontend**: Java, XML (Android)
- **Backend**: Firebase Firestore, Firebase Authentication, Firebase Cloud Storage
- **Payment Processing**: Razorpay integration
- **Location Services**: Google Maps API for selecting and storing equipment location
- **Notification Management**: Firebase with custom NotificationModel class

## ⚙️ Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/farm-equipment-sharing-app.git
   cd farm-equipment-sharing-app
   ```

2. **Set up Firebase**:
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Create a new project and add an Android app to it.
   - Download the `google-services.json` file and place it in your project's `app/` directory.
   - Enable **Firestore**, **Authentication**, **Cloud Storage**, and **Realtime Database** if using for notifications.

3. **Configure Google Maps API**:
   - Go to the [Google Cloud Console](https://console.cloud.google.com/).
   - Enable the **Maps SDK for Android** and **Geocoding API**.
   - Obtain an API key and add it to your `AndroidManifest.xml` file:
     ```xml
     <meta-data
         android:name="com.google.android.geo.API_KEY"
         android:value="YOUR_GOOGLE_MAPS_API_KEY" />
     ```

4. **Set up Razorpay for Payment Processing**:
   - Register at [Razorpay](https://razorpay.com/) and get your API key.
   - Add the API key in your payment configuration file or environment variables.

5. **Build and Run**:
   - Connect your Android device or use an emulator.
   - Build and run the app from Android Studio.

## 🚀 Usage

1. **Sign Up/Login**: Users sign up and select a role as a farmer or vendor.
2. **Add Equipment**: Farmers and vendors can list their equipment by adding details such as name, type, location, price, and available dates.
3. **Search and Rent Equipment**: Farmers can search for equipment based on various criteria and book equipment for rental.
4. **Transaction and Order Management**: The app maintains a record of orders and payment transactions for easy reference.
5. **Notifications**: Users receive updates on their transactions, equipment availability, and other relevant activities.

## 📝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the project.
2. Create a feature branch (`git checkout -b feature-name`).
3. Commit your changes (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature-name`).
5. Open a pull request.

## 🛡️ License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## 📧 Contact

For any questions, feedback, or suggestions, please reach out to:

- **Author**: Atul Dubal
- **Email**: [atuldubal199@gmail.com](mailto:atuldubal199@gmail.com)
