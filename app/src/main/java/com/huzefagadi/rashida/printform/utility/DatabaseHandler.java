package com.huzefagadi.rashida.printform.utility;

/**
 * Created by Rashida on 06/07/15.
 */

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.huzefagadi.rashida.printform.bean.ItemBean;
import com.huzefagadi.rashida.printform.bean.MenuBean;
import com.huzefagadi.rashida.printform.bean.ServerBean;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "printForm";
    private static final String TABLE_MENU = "Menu";
    private static final String TABLE_ITEM = "Item";
    private static final String TABLE_SERVER = "Server";
    private static final String MENU_DESCRIPTION = "description";
    private static final String MENU_ID = "id";
    private static final String ITEM_DESCRIPTION = "itemDescription";
    private static final String ITEM_ID = "keyId";
    private static final String SERVER_ID = "id";
    private static final String SERVER_NAME = "name";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SERVER_TABLE = "CREATE TABLE " + TABLE_SERVER + "("
                + SERVER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SERVER_NAME + " TEXT"
                + ")";

        String CREATE_MENU_TABLE = "CREATE TABLE " + TABLE_MENU + "("
                + MENU_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MENU_DESCRIPTION + " TEXT"
                + ")";

        String CREATE_ITEM_TABLE = "CREATE TABLE " + TABLE_ITEM + "("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + MENU_ID + " INTEGER,"
                + MENU_DESCRIPTION + " TEXT,"
                + ITEM_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(CREATE_MENU_TABLE);
        db.execSQL(CREATE_ITEM_TABLE);
        db.execSQL(CREATE_SERVER_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MENU);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SERVER);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addMenu(MenuBean menu) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MENU_DESCRIPTION, menu.getDescription()); // Contact Name
        // Inserting Row
        db.insert(TABLE_MENU, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    public void addServer(String server) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server); // Contact Name
        // Inserting Row
        db.insert(TABLE_SERVER, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }
    public ArrayList<ServerBean> getAllServers() {
        ArrayList<ServerBean> serverList = new ArrayList<ServerBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SERVER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ServerBean server = new ServerBean();
                server.setId(Integer.parseInt(cursor.getString(0)));
                server.setServerName(cursor.getString(1));
                serverList.add(server);
            } while (cursor.moveToNext());
        }

        // return contact list
        return serverList;
    }

    public int updateServer(ServerBean server) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SERVER_NAME, server.getServerName());
        // updating row
        return db.update(TABLE_SERVER, values, SERVER_ID + " = ?",
                new String[]{String.valueOf(server.getId())});
    }

    public void deleteServer(ServerBean server) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SERVER, SERVER_ID + " = ?",
                new String[]{String.valueOf(server.getId())});
        db.close();
    }


    // code to get all contacts in a list view
    public ArrayList<MenuBean> getAllMenus() {
        ArrayList<MenuBean> menuList = new ArrayList<MenuBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_MENU;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                MenuBean menu = new MenuBean();
                menu.setId(Integer.parseInt(cursor.getString(0)));
                menu.setDescription(cursor.getString(1));
                menuList.add(menu);
            } while (cursor.moveToNext());
        }

        // return contact list
        return menuList;
    }

    // code to update the single contact
    public int updateMenu(MenuBean menu) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MENU_DESCRIPTION, menu.getDescription());


        // updating row
        return db.update(TABLE_MENU, values, MENU_ID + " = ?",
                new String[]{String.valueOf(menu.getId())});
    }

    public void deleteMenu(MenuBean menu) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MENU, MENU_ID + " = ?",
                new String[]{String.valueOf(menu.getId())});
        db.close();
    }

    public void addItem(ItemBean item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (item.getMenu() != null) {
            values.put(MENU_DESCRIPTION, item.getMenu().getDescription());
            values.put(MENU_ID, item.getMenu().getId());
        } else {
            values.put(MENU_DESCRIPTION, "");
            values.put(MENU_ID, "");
        }

        values.put(ITEM_DESCRIPTION, item.getDescription());
        db.insert(TABLE_ITEM, null, values);
        db.close();
    }


    // code to get all contacts in a list view
    public ArrayList<ItemBean> getAllItems() {
        ArrayList<ItemBean> itemList = new ArrayList<ItemBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEM;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemBean item = new ItemBean();
                item.setId(Integer.parseInt(cursor.getString(0)));
                String idTemp = cursor.getString(1);
                int id;
                if(idTemp.equals(""))
                {
                    item.setMenu(null);
                }
                else
                {
                    id=Integer.parseInt(idTemp);
                    item.setMenu(new MenuBean(id, cursor.getString(2)));
                }
                item.setDescription(cursor.getString(3));
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemList;
    }

    // code to get all contacts in a list view
    public ArrayList<ItemBean> getAllNonMenuItems() {
        ArrayList<ItemBean> itemList = new ArrayList<ItemBean>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ITEM;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_ITEM, null, MENU_ID + " =?", new String[]{""}, null, null, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemBean item = new ItemBean();
                item.setId(Integer.parseInt(cursor.getString(0)));
                String idTemp = cursor.getString(1);
                int id;
                if(idTemp.equals(""))
                {
                    item.setMenu(null);
                }
                else
                {
                    id=Integer.parseInt(idTemp);
                    item.setMenu(new MenuBean(id, cursor.getString(2)));
                }

                item.setDescription(cursor.getString(3));
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        // return contact list
        return itemList;
    }

    // code to get all contacts in a list view
    public ArrayList<ItemBean> getAllItemsForMenu(int id) {
        ArrayList<ItemBean> itemList = new ArrayList<ItemBean>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.query(TABLE_ITEM, null, MENU_ID + " =?", new String[]{String.valueOf(id)}, null, null, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ItemBean item = new ItemBean();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setMenu(new MenuBean(Integer.parseInt(cursor.getString(1)), cursor.getString(2)));
                item.setDescription(cursor.getString(3));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        return itemList;
    }

    // code to update the single contact
    public int updateItem(ItemBean item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_DESCRIPTION, item.getDescription());
        if(item.getMenu()==null)
        {
            values.put(MENU_ID, "");
            values.put(MENU_DESCRIPTION, "");
        }
        else
        {
            values.put(MENU_ID, item.getMenu().getId());
            values.put(MENU_DESCRIPTION, item.getMenu().getDescription());
        }

        // updating row
        return db.update(TABLE_ITEM, values, ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Deleting single contact
    public void deleteItem(ItemBean item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEM, ITEM_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }


}
