package lk.grandhotel.stayease.network;

import java.util.Map;

import lk.grandhotel.stayease.network.models.AdminResponse;
import lk.grandhotel.stayease.network.models.ApiResponse;
import lk.grandhotel.stayease.network.models.AuthResponse;
import lk.grandhotel.stayease.network.models.BookingDetailResponse;
import lk.grandhotel.stayease.network.models.BookingListResponse;
import lk.grandhotel.stayease.network.models.CartResponse;
import lk.grandhotel.stayease.network.models.CheckoutResponse;
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
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.PUT;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import lk.grandhotel.stayease.network.models.AdminBookingDetailResponse;
import lk.grandhotel.stayease.network.models.AdminBookingListResponse;
import lk.grandhotel.stayease.network.models.AdminImageResponse;
import lk.grandhotel.stayease.network.models.AdminRoomDetailResponse;
import lk.grandhotel.stayease.network.models.AdminRoomListResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

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
    Call<BookingListResponse> getMyBookings(@Query("status") String status);

    @GET("bookings/my/{id}")
    Call<BookingDetailResponse> getBookingById(@Path("id") String bookingId);

    @DELETE("bookings/{id}/cancel")
    Call<BookingDetailResponse> cancelBooking(@Path("id") String bookingId);

    @GET("cart")
    Call<CartResponse> getCart();

    @POST("cart/items")
    Call<ApiResponse> addToCart(@Body Map<String, Object> body);

    @DELETE("cart/items/{itemId}")
    Call<ApiResponse> removeFromCart(@Path("itemId") String itemId);

    @DELETE("cart")
    Call<ApiResponse> clearCart();

    @POST("cart/checkout")
    Call<CheckoutResponse> checkoutCart(@Body Map<String, String> body);

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
    @GET("admin/rooms")
    Call<AdminRoomListResponse> getAdminRooms();

    @POST("admin/rooms")
    Call<AdminRoomDetailResponse> createRoom(@Body Map<String, Object> body);

    @PUT("admin/rooms/{id}")
    Call<AdminRoomDetailResponse> updateRoom(@Path("id") String id, @Body Map<String, Object> body);

    @PATCH("admin/rooms/{id}/availability")
    Call<AdminRoomDetailResponse> setRoomAvailability(@Path("id") String id, @Body Map<String, Object> body);

    @Multipart
    @POST("admin/rooms/{id}/images")
    Call<AdminImageResponse> uploadRoomImage(@Path("id") String id,
                                             @Part MultipartBody.Part image,
                                             @Part("isPrimary") RequestBody isPrimary);

    @DELETE("admin/rooms/{id}/images/{imgId}")
    Call<ApiResponse> deleteRoomImage(@Path("id") String id, @Path("imgId") String imgId);

    @GET("admin/bookings")
    Call<AdminBookingListResponse> getAdminBookings(@Query("status") String status, @Query("page") Integer page);

    @GET("admin/bookings/{id}")
    Call<AdminBookingDetailResponse> getAdminBookingById(@Path("id") String id);

    @POST("admin/bookings/walk-in")
    Call<AdminBookingDetailResponse> createWalkIn(@Body Map<String, Object> body);

    @PATCH("admin/bookings/{id}/status")
    Call<AdminBookingDetailResponse> updateBookingStatus(@Path("id") String id, @Body Map<String, Object> body);
}