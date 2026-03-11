# StayEase — Grand Horizon Hotels

> Your Perfect Stay Awaits

A full-featured Android hotel room booking M-commerce application developed for **Grand Horizon Hotels (Pvt) Ltd** by **Rahdel Digital Solutions**.

This is a university project for the **Handheld Device Programming II (JIAT/HHDPII)** unit at **Java Institute for Advanced Technology**, under the BEng in Software Engineering programme.

---

## About the App

StayEase allows hotel guests to browse available rooms, make reservations in real time, and process payments securely — all from their Android device. It also provides hotel staff with an administrative panel to manage rooms, bookings, and guest communications.

---

## Features

### Guest (Customer)
- User registration, login, and profile management
- Browse and search available rooms with filters (type, price, amenities)
- Room detail screen with image gallery, amenity chips, and guest reviews
- Date range selection with guest count for booking
- Partial payment (50% advance) or full payment via Stripe
- Cart — add multiple rooms and checkout together
- My Bookings — view upcoming, past, and cancelled bookings
- Cancel bookings and view payment history
- Digital PDF receipt saved to device storage
- Post-stay room reviews and ratings
- Push notifications for booking confirmations, payment receipts, and check-in reminders
- Google Maps showing hotel location with get directions and one-tap call
- Shake-to-refresh room availability (accelerometer sensor)
- Dark mode support
- Offline mode with cached room data

### Admin Panel (Backend)
- Room inventory management (add, edit, delete, toggle availability)
- Image upload via Cloudinary
- View and manage all bookings, walk-in bookings, update booking status
- Process Stripe refunds
- Send push notifications to all guests or specific users
- Dashboard with revenue stats, occupancy, and best-performing rooms
- Hotel configuration management

---

## Tech Stack

### Android App
- **Language:** Java
- **Architecture:** MVVM (ViewModel + LiveData + Repository)
- **UI:** Material Design 3, ViewBinding
- **Networking:** Retrofit2 + OkHttp + AuthInterceptor (JWT auto-refresh)
- **Local Storage:** Room Database (offline cache), EncryptedSharedPreferences (tokens)
- **Image Loading:** Glide
- **Maps:** Google Maps SDK + Directions API
- **Payments:** Stripe PaymentSheet SDK
- **Notifications:** Firebase Cloud Messaging (FCM)
- **PDF Generation:** Android PdfDocument API
- **Sensors:** Accelerometer (shake-to-refresh)

### Backend (Separate Repo)
- **Runtime:** Node.js + Express.js (TypeScript)
- **Database:** PostgreSQL via Neon (serverless)
- **ORM:** Prisma v7
- **Auth:** JWT (access 15min + refresh 7 days)
- **Payments:** Stripe
- **Email:** Resend
- **Image Storage:** Cloudinary
- **Deployment:** Vercel

---

## Project Setup

### Prerequisites
- Android Studio (latest)
- JDK 11+
- Android device or emulator (API 28+)

### Steps

1. Clone the repository
```bash
   git clone https://github.com/abdullah-fareed1/stayease-frontend.git
```

2. Open the project in Android Studio

3. Create a `local.properties` file in the project root and add your keys:
```
   MAPS_API_KEY=your_google_maps_api_key
   STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key
   BASE_URL=http://10.0.2.2:3000/api/
```

4. Add `google-services.json` from your Firebase console into the `app/` folder

5. Sync Gradle and run on your device or emulator

---

## Security Notes

- `local.properties` is gitignored — never commit it
- `google-services.json` is gitignored — never commit it
- All tokens stored in `EncryptedSharedPreferences`
- All API communication over HTTPS in production
- Stripe handles all card data — no card details touch our server

---

## Colour Palette

| Role | Hex |
|------|-----|
| Primary | `#4F46E5` |
| Secondary | `#7C3AED` |
| Background | `#F8F9FA` |
| Surface | `#FFFFFF` |
| Error | `#EF4444` |

---

## License

MIT