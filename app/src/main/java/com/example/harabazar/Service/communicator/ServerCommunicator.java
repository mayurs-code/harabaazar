package com.example.harabazar.Service.communicator;


import com.example.harabazar.Service.request.AddRemoveProductRequest;
import com.example.harabazar.Service.request.ApplyCouponRequest;
import com.example.harabazar.Service.request.BannersRequest;
import com.example.harabazar.Service.request.CancelOrderRequest;
import com.example.harabazar.Service.request.DeleteAddressRequest;
import com.example.harabazar.Service.request.GetInventoryRequest;
import com.example.harabazar.Service.request.GetOrdersRequest;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.GetSettingsRequest;
import com.example.harabazar.Service.request.GetUsersRequest;
import com.example.harabazar.Service.request.GiveOrderRatingRequest;
import com.example.harabazar.Service.request.HandpickRequest;
import com.example.harabazar.Service.request.OTPRequest;
import com.example.harabazar.Service.request.OrderDetailRequest;
import com.example.harabazar.Service.request.PlaceOrderRequest;
import com.example.harabazar.Service.request.ProductListRequest;
import com.example.harabazar.Service.request.RemoveFromCartRequest;
import com.example.harabazar.Service.request.SaveUserRequest;
import com.example.harabazar.Service.request.UpdateAddressRequest;
import com.example.harabazar.Service.request.UpdateCoordinatesRequest;
import com.example.harabazar.Service.request.UpdateInventoryRequest;
import com.example.harabazar.Service.request.UpdateOrderStatusRequest;
import com.example.harabazar.Service.request.UpdateProfilePhotoRequest;
import com.example.harabazar.Service.request.UpdateProfileRequest;
import com.example.harabazar.Service.request.VerifyOtpRequest;
import com.example.harabazar.Service.response.AddRemoveProductResponse;
import com.example.harabazar.Service.response.ApplyCouponResponse;
import com.example.harabazar.Service.response.BannersResponse;
import com.example.harabazar.Service.response.CancelOrderResponse;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.CategoryResponse;
import com.example.harabazar.Service.response.CreateProfilePhotoResponse;
import com.example.harabazar.Service.response.CreateProfileResponse;
import com.example.harabazar.Service.response.DeleteAddressResponse;
import com.example.harabazar.Service.response.GetHandpickOffersResponse;
import com.example.harabazar.Service.response.GetInventoryResponse;
import com.example.harabazar.Service.response.GetOrdersResponse;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetSavedUsersResponse;
import com.example.harabazar.Service.response.GetSettingsResponse;
import com.example.harabazar.Service.response.GetUsersResponse;
import com.example.harabazar.Service.response.GiveOrderRatingResponse;
import com.example.harabazar.Service.response.HandpickResponse;
import com.example.harabazar.Service.response.NotificationResponse;
import com.example.harabazar.Service.response.OTPResponseBody;
import com.example.harabazar.Service.response.OffersResponse;
import com.example.harabazar.Service.response.OrderDetailResponse;
import com.example.harabazar.Service.response.PlaceOrderResponse;
import com.example.harabazar.Service.response.ProductListResponse;
import com.example.harabazar.Service.response.RemoveFromCartResponse;
import com.example.harabazar.Service.response.SaveUserResponse;
import com.example.harabazar.Service.response.UpdateAddressResponse;
import com.example.harabazar.Service.response.UpdateCoordinatesResponse;
import com.example.harabazar.Service.response.UpdateInventoryResponse;
import com.example.harabazar.Service.response.UpdateOrderStatusResponse;
import com.example.harabazar.Service.response.VerifyResponse;

public class ServerCommunicator {

    public static void loginWithOtp(Connector connector, OTPRequest request ) {
        connector.callServer("/send_otp", Connector.METHOD_POST,
                Utils.getGson().toJson(request), "", OTPResponseBody.class);
    }

    public static void verifyOtp(Connector connector, VerifyOtpRequest request) {
        connector.callServer("/verify_otp", Connector.METHOD_POST,
                Utils.getGson().toJson(request), "", VerifyResponse.class);
    }
    public static void updateOrderStatus(Connector connector , UpdateOrderStatusRequest request, String key) {
        connector.callServer("/change_order_status", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, UpdateOrderStatusResponse.class);
    }
    public static void getAllInventory(Connector connector , GetInventoryRequest request, String key) {
        connector.callServer("/get_all_inventory_products", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetInventoryResponse.class);
    }

    public static void getCategory(Connector connector, String sessionKey) {
        connector.callServer("/get_category_list", Connector.METHOD_POST,
                Utils.getGson().toJson(null), sessionKey, CategoryResponse.class);
    }
    public static void updateInventory(Connector connector  , UpdateInventoryRequest request, String key) {
        connector.callServer("/update_invetory", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, UpdateInventoryResponse.class);
    }
    public static void getProducts(Connector connector, ProductListRequest request, String sessionKey) {
        connector.callServer("/get_product_list", Connector.METHOD_POST,
                Utils.getGson().toJson(request), sessionKey, ProductListResponse.class);
    }

    public static void getBanner(Connector connector, BannersRequest request,String sessionKey) {
        connector.callServer("/get_banners", Connector.METHOD_POST,
                Utils.getGson().toJson(request), sessionKey, BannersResponse.class);
    }

    public static void getOffers(Connector connector, String sessionKey) {
        connector.callServer("/get_offers", Connector.METHOD_POST,
                Utils.getGson().toJson(null), sessionKey, OffersResponse.class);
    }  public static void getOffersHandpick(Connector connector, String sessionKey) {
        connector.callServer("/get_handpick_offers", Connector.METHOD_POST,
                Utils.getGson().toJson(null), sessionKey, GetHandpickOffersResponse.class);
    }

    public static void updateProfile(Connector connector, UpdateProfileRequest request, String sessionKey) {
        connector.callServer("/update_profile", Connector.METHOD_POST,
                Utils.getGson().toJson(request), sessionKey , CreateProfileResponse.class);
    }
    public static void updateProfilePhoto(Connector connector, UpdateProfilePhotoRequest request, String sessionKey) {
        connector.callServer("/upload_base64_img", Connector.METHOD_POST,
                Utils.getGson().toJson(request), sessionKey , CreateProfilePhotoResponse.class);
    }

    public static void readNotification(Connector connector  , String sessionKey) {
        connector.callServer("/read_notification", Connector.METHOD_POST,
                Utils.getGson().toJson(null), sessionKey , CreateProfilePhotoResponse.class);
    }






    public static void getCartProductList(Connector connector, String key) {
        connector.callServer("/cart_product_list", Connector.METHOD_POST,
                Utils.getGson().toJson(null), key, CartResponse.class);
    }

    public static void addRemoveProducts(Connector connector, AddRemoveProductRequest request, String key) {
        connector.callServer("/toggle_cart", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, AddRemoveProductResponse.class);
    } public static void removeFromCart(Connector connector, RemoveFromCartRequest request, String key) {
        connector.callServer("/remove_from_cart", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, RemoveFromCartResponse.class);
    }

    public static void applyCouponCode(Connector connector, ApplyCouponRequest request, String key) {
        connector.callServer("/apply_coupon_code", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, ApplyCouponResponse.class);
    }

    public static void placeOrder(Connector connector, PlaceOrderRequest request, String key) {
        connector.callServer("/place_order", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, PlaceOrderResponse.class);
    }public static void cancelOrder(Connector connector, CancelOrderRequest request, String key) {
        connector.callServer("/cancel_order", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, CancelOrderResponse.class);
    }

    public static void getOrdersList(Connector connector, GetOrdersRequest request, String key) {
        connector.callServer("/get_order_list", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetOrdersResponse.class);
    }

    public static void giveOrderRating(Connector connector, GiveOrderRatingRequest request, String key) {
        connector.callServer("/give_rating", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GiveOrderRatingResponse.class);
    }

    public static void giveOrderDetails(Connector connector, OrderDetailRequest request, String key) {
        connector.callServer("/get_order_detail", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, OrderDetailResponse.class);
    }

    public static void getProfile(Connector connector, GetProfileRequest request, String key) {
        connector.callServer("/get_profile", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetProfileResponse.class);
    }

    public static void addUpdateAddress(Connector connector, UpdateAddressRequest request, String key) {
        connector.callServer("/save_address", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, UpdateAddressResponse.class);
    }
    public static void deleteAddress(Connector connector, DeleteAddressRequest request, String key) {
        connector.callServer("/delete_address", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, DeleteAddressResponse.class);
    }
    public static void getUsers(Connector connector , GetUsersRequest request, String key) {
        connector.callServer("/get_all_users", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetUsersResponse.class);
    }
    public static void updateCoordinates(Connector connector , UpdateCoordinatesRequest request, String key) {
        connector.callServer("/update_coordinate", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, UpdateCoordinatesResponse.class);
    }
    public static void getInventory(Connector connector , GetInventoryRequest request, String key) {
        connector.callServer("/get_inventory_list", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetInventoryResponse.class);
    }
    public static void saveUser(Connector connector , SaveUserRequest request, String key) {
        connector.callServer("/toggle_save_user", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, SaveUserResponse.class);
    }
    public static void getSavedUser(Connector connector  , String key) {
        connector.callServer("/get_saved_users", Connector.METHOD_POST,
                Utils.getGson().toJson(null), key, GetSavedUsersResponse.class);
    }
    public static void requestHandpick(Connector connector  , HandpickRequest request, String key) {
        connector.callServer("/request_handpick", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, HandpickResponse.class);
    }
    public static void getNotifications(Connector connector  , String key) {
        connector.callServer("/get_notifications", Connector.METHOD_POST,
                Utils.getGson().toJson(null), key, NotificationResponse.class);
    }
    public static void getSettings(Connector connector   , GetSettingsRequest request, String key) {
        connector.callServer("/get_settings", Connector.METHOD_POST,
                Utils.getGson().toJson(request), key, GetSettingsResponse.class);
    }
}
