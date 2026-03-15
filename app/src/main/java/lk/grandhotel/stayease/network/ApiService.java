package lk.grandhotel.stayease.network;

import java.util.Map;

import lk.grandhotel.stayease.network.models.AdminResponse;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.AuthResponse;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.DashboardResponse;
import lk.grandhotel.stayease.network.models.HotelConfigResponse;
import lk.grandhotel.stayease.network.models.PaymentInitiateResponse;
import lk.grandhotel.stayease.network.models.RefreshResponse;
import lk.grandhotel.stayease.network.models.ReviewListResponse;
import lk.grandhotel.stayease.network.models.RoomDetailResponse;
import lk.grandhotel.stayease.network.models.RoomListResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("auth/register")
    Call<AuthResponse> register(@Body Map<String, String> body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    @POST("auth/refresh")
    Call<RefreshResponse> refresh(@Body Map<String, String> body);

    @POST("auth/logout")
    Call<ApiResponse> logout(@Body Map<String, String> body);

    @POST("auth/forgot-password")
    Call<ApiResponse> forgotPassword(@Body Map<String, String> body);

    @POST("auth/reset-password")
    Call<ApiResponse> resetPassword(@Body Map<String, String> body);

    @PATCH("users/fcm-token")
    Call<ApiResponse> updateFcmToken(@Body Map<String, String> body);

    @GET("rooms")
    Call<RoomListResponse> getRooms(
            @Query("category") String category,
            @Query("available") Boolean available,
            @Query("minPrice") Double minPrice,
            @Query("maxPrice") Double maxPrice,
            @Query("maxGuests") Integer maxGuests,
            @Query("page") Integer page,
            @Query("pageSize") Integer pageSize
    );

    @GET("rooms/{id}")
    Call<RoomDetailResponse> getRoomById(@Path("id") String roomId);

    @GET("rooms/{id}/reviews")
    Call<ReviewListResponse> getRoomReviews(@Path("id") String roomId);

    @POST("bookings")
    Call<BookingDetailResponse> createBooking(@Body Map<String, Object> body);

    @GET("bookings/my")
    Call<BookingListResponse> getMyBookings();

    @GET("bookings/my/{id}")
    Call<BookingDetailResponse> getBookingById(@Path("id") String bookingId);

    @DELETE("bookings/{id}/cancel")
    Call<BookingDetailResponse> cancelBooking(@Path("id") String bookingId);

    @GET("cart")
    Call<CartResponse> getCart();

    @POST("cart/items")
    Call<CartResponse> addToCart(@Body Map<String, Object> body);

    @DELETE("cart/items/{itemId}")
    Call<CartResponse> removeFromCart(@Path("itemId") String itemId);

    @DELETE("cart")
    Call<ApiResponse> clearCart();

    @POST("cart/checkout")
    Call<BookingListResponse> checkoutCart();

    @POST("payments/initiate")
    Call<PaymentInitiateResponse> initiatePayment(@Body Map<String, String> body);

    @POST("reviews")
    Call<ApiResponse> createReview(@Body Map<String, Object> body);

    @GET("hotel/config")
    Call<HotelConfigResponse> getHotelConfig();

    @POST("admin/auth/login")
    Call<AdminResponse> adminLogin(@Body Map<String, String> body);

    @POST("admin/auth/refresh")
    Call<RefreshResponse> adminRefresh(@Body Map<String, String> body);

    @POST("admin/auth/logout")
    Call<ApiResponse> adminLogout(@Body Map<String, String> body);

    @POST("admin/auth/forgot-password")
    Call<ApiResponse> adminForgotPassword(@Body Map<String, String> body);

    @POST("admin/auth/reset-password")
    Call<ApiResponse> adminResetPassword(@Body Map<String, String> body);

    @GET("admin/dashboard/overview")
    Call<DashboardResponse> getAdminDashboard();
}