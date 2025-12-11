package com.saveetha.orderly_book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.saveetha.orderly_book.api.Order;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "orders_db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ID = "id"; // orderId
    private static final String COLUMN_CUSTOMER_NAME = "customer_name";
    private static final String COLUMN_TOTAL_PRICE = "total_price";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_ORDER_DATE = "order_date"; // yyyy-MM-dd

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ORDERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUSTOMER_NAME + " TEXT,"
                + COLUMN_TOTAL_PRICE + " REAL,"
                + COLUMN_STATUS + " TEXT,"
                + COLUMN_ORDER_DATE + " TEXT"
                + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // Get all orders (useful for owner, excludes rejected if needed)
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_ID + " DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME));
                double totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_PRICE));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                String orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));

                // ✅ Use setters instead of missing constructor
                Order order = new Order();
                order.setOrderId(id);
                order.setCustomerName(name);
                order.setTotalPrice(totalPrice);
                order.setStatus(status);
                order.setOrderDate(orderDate);

                orders.add(order);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return orders;
    }


    // Update order status
    public void updateOrderStatus(int orderId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        db.update(TABLE_ORDERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(orderId)});
    }

    // Insert a new order
    public long insertOrder(String customerName, double totalPrice, String status, String orderDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, customerName);
        values.put(COLUMN_TOTAL_PRICE, totalPrice);
        values.put(COLUMN_STATUS, status);
        values.put(COLUMN_ORDER_DATE, orderDate);
        return db.insert(TABLE_ORDERS, null, values);
    }
}
