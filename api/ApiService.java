package com.saveetha.orderly_book.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("login.php")
    Call<LoginResponse> loginUser(@Body LoginRequest loginRequest);

    @GET("get_products.php")
    Call<ProductResponse> getAllProducts(@Query("owner_id") int ownerId);

    @GET("get_owner_orders.php")
    Call<OwnerOrdersResponse> getOwnerOrders(@Query("owner_id") int ownerId);

    @GET("get_order_products.php")
    Call<OrderProductsResponse> getOrderProducts(@Query("order_id") int orderId);


    @GET("get_owner_products.php")
    Call<OwnerProductsResponse> getOwnerProducts(@Query("owner_id") int ownerId);

    @GET("get_order_details.php")
    Call<OrderDetailsResponse> getOrderDetails(@Query("order_id") int orderId);


    @FormUrlEncoded
        @POST("add_product.php") // filename must match exactly
        Call<GenericResponse> addProduct(
                @Field("owner_id") int ownerId,
                @Field("name") String name,
                @Field("price") double price
        );
    @POST("place_order.php")
    Call<OrderResponse> placeOrder(@Body OrderRequest request);

    @FormUrlEncoded
    @POST("update_order.php")
    Call<OrderResponse> updateOrder(
            @Field("order_id") int orderId,
            @Field("status") String status
    );




    @FormUrlEncoded
    @POST("get_orders.php")
    Call<List<Order>> getOrders(
            @Field("customer_id") int customerId
    );

    @POST("register.php")
    Call<SignupResponse> signupUser(@Body SignupRequest request);

    @GET("get_owner_order_count.php")
    Call<Integer> getOrderCountByOwner(@Query("owner_id") int ownerId);

    // Optional: Fetch orders list for owner
    @GET("get_owner_orders.php")
    Call<List<Order>> getOrdersByOwner(@Query("owner_id") int ownerId);

    @GET("getOwners.php")
    Call<OwnersResponse> getOwners();

    @GET("owners.php")
    Call<Owner> getOwnerByUserId(@Query("id") int userId);

    @GET("getCustomerById.php")
    Call<User> getCustomerById(@Query("id") int customerId);

    @GET("get_customer_orders_for_owner.php") // PHP endpoint
    Call<CustomerOrdersResponse> getCustomerOrdersForOwner(
            @Query("owner_id") int ownerId,
            @Query("customer_id") int customerId
    );

    @GET("get_customer_orders.php")
    Call<OrdersResponse> getCustomerOrders(@Query("customer_id") int customerId);

    @GET("get_owner_customers.php")
    Call<OwnerCustomersResponse> getOwnerCustomers(@Query("owner_id") int ownerId);





}
