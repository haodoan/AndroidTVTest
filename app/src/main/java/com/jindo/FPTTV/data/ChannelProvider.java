package com.jindo.FPTTV.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.jindo.FPTTV.data.ChannelContracts.ItemTable;

import com.jindo.FPTTV.BuildConfig;

import java.util.ArrayList;

public class ChannelProvider extends ContentProvider {

    private static final String LOG_TAG = ChannelProvider.class.getSimpleName();

    public static final String AUTHORITY =
            BuildConfig.APPLICATION_ID + "." + ChannelProvider.class.getSimpleName();

    private static final String DATABASE_NAME = "channel_list.db";

    private static final int DATABASE_VERSION_CURRENT = 3;

    private DatabaseHelper openHelper;
    private final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MATCHER_CONTAINER = 1;
    private static final int MATCHER_CONTAINER_ID = 2;
    private static final int MATCHER_ITEM = 3;
    private static final int MATCHER_ITEM_ID = 4;
    private static final int MATCHER_BROWSE = 5;
    private static final int MATCHER_BROWSE_ID = 6;

    private void initMatcher(String authority) {

        matcher.addURI(authority, ItemTable.PATH, MATCHER_ITEM);
        matcher.addURI(authority, ItemTable.PATH + "/#", MATCHER_ITEM_ID);

    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String miner;
        switch (matcher.match(uri)) {
            case MATCHER_CONTAINER:
            case MATCHER_CONTAINER_ID:

                break;
            case MATCHER_ITEM:
            case MATCHER_ITEM_ID:
                break;
            case MATCHER_BROWSE:
            case MATCHER_BROWSE_ID:
                break;
            default:
                throw new IllegalArgumentException("uri " + uri + " not matched.");
        }
        String major;
        if (isCollectionUri(uri)) {
            major = "vnd.android.cursor.dir";
        } else {
            major = "vnd.android.cursor.item";
        }
        return major + "/";
    }
    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        // ChannelProvider.AUTHORITY と、 AndroidManifest.xml の authority が一致していることを確認。
        if (!AUTHORITY.equals(info.authority)) {
            throw new RuntimeException(
                    "authority not matched. Expected: " + AUTHORITY + ", Actual:" + info.authority);
        }
        super.attachInfo(context, info);
    }

    @Override
    public boolean onCreate() {
        initMatcher(AUTHORITY);
        openHelper = new DatabaseHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION_CURRENT);
        return true;
    }

    private SQLiteDatabase getReadableDatabase() {
        return openHelper.getReadableDatabase();
    }

    private SQLiteDatabase getWritableDatabase() {
        return openHelper.getWritableDatabase();
    }

    private boolean isCollectionUri(Uri uri) {
        switch (matcher.match(uri)) {
            case MATCHER_CONTAINER:
            case MATCHER_ITEM:
            case MATCHER_BROWSE:
                return true;
            case MATCHER_CONTAINER_ID:
            case MATCHER_ITEM_ID:
            case MATCHER_BROWSE_ID:
                return false;
            default:
                break;
        }
        throw new IllegalArgumentException("uri " + uri + " not matched.");
    }

    private String getTableName(Uri uri) {
        switch (matcher.match(uri)) {
            case MATCHER_ITEM:
            case MATCHER_ITEM_ID:
                return ItemTable.PATH;
            default:
                break;
        }
        throw new IllegalArgumentException("uri " + uri + " not matched.");
    }

    private ContentResolver getContentResolver() {
        Context context = getContext();
        if (context == null) {
            throw new RuntimeException("context not initialized.");
        }
        return context.getContentResolver();
    }

    private void notifyChange(Uri itemUri) {
        getContentResolver().notifyChange(itemUri, null);
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String tableName = getTableName(uri);

        qb.setTables(tableName);
        if (!isCollectionUri(uri)) {
            qb.appendWhere(BaseColumns._ID + " = " + ContentUris.parseId(uri));
        }


        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = null;
        } else {
            orderBy = sortOrder;
        }

        // logd("query = " + qb.buildQuery(projection, selection, null, null, sortOrder, null));

        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        if (c != null) {
            c.setNotificationUri(getContentResolver(), uri);
        }
        return c;
    }



    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(uri);
        try {
            long rowID = db.insertOrThrow(tableName, null, values);
            Uri itemUri = ContentUris.withAppendedId(uri, rowID);
            notifyChange(itemUri);
            return itemUri;
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final String tableName = getTableName(uri);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ContentValues val : values) {
                db.insertWithOnConflict(tableName, null, val, SQLiteDatabase.CONFLICT_IGNORE);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
        notifyChange(uri);
        return values.length;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(
            @NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            ContentProviderResult[] result = super.applyBatch(operations);
            db.setTransactionSuccessful();
            return result;
        } catch (SQLException e) {
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        final String tableName = getTableName(uri);
        int ret;
        if (!isCollectionUri(uri)) {
            ret = db.delete(tableName, BaseColumns._ID + " = " + ContentUris.parseId(uri), null);
        } else {
            ret = db.delete(tableName, selection, selectionArgs);
        }
        if (ret > 0) {
            notifyChange(uri);
        }
        return ret;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values,
                      String selection, String[] selectionArgs) {
        SQLiteDatabase db = getWritableDatabase();
        String tableName = getTableName(uri);
        int ret;
        if (!isCollectionUri(uri)) {
            ret = db.update(tableName, values,
                    BaseColumns._ID + " = " + ContentUris.parseId(uri), null);
        } else {
            ret = db.update(tableName, values, selection, selectionArgs);
        }
        if (ret > 0) {
            notifyChange(uri);
        }
        return ret;
    }

    private static void execSQL(SQLiteDatabase db, String sql) {
        db.execSQL(sql);
    }

    private static void createTables(SQLiteDatabase db) {


        final String ItemTableSQL = "CREATE TABLE IF NOT EXISTS " + ItemTable.PATH + " ( "
                + ItemTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ItemTable.mChannelName + " TEXT NOT NULL, "
                + ItemTable.mGroupTitle + " TEXT NOT NULL, "
                + ItemTable.mLogoURL + " TEXT, "
                + ItemTable.mStreamURL + " TEXT, "
                + ItemTable.mType + " TEXT, "
                + ItemTable.flag_groupChanel + " INTEGER ) ";
   //             + ItemTable.mDuration + " TEXT ) ";
        execSQL(db, ItemTableSQL);

//        execSQL(db, "CREATE INDEX IF NOT EXISTS 'mChannelName_index' ON "
//                + ItemTable.PATH + " (" + ItemTable.mChannelName);
//        execSQL(db, "CREATE INDEX IF NOT EXISTS 'container_row_id_index' ON "
//                + ItemTable.PATH + " (" + ItemTable.mGroupTitle
//                + ")");
//        execSQL(db, "CREATE INDEX IF NOT EXISTS 'title_index' ON "
//                + ItemTable.PATH + " (" + ItemTable.mLogoURL + ")");
//        execSQL(db, "CREATE INDEX IF NOT EXISTS 'date_index' ON "
//                + ItemTable.PATH + " (" + ItemTable.mStreamURL + ")");
    }

    private static void dropTables(SQLiteDatabase db) {
        String[] tables = { ItemTable.PATH };
        for (String table : tables) {
            execSQL(db, "DROP TABLE IF EXISTS " + table);
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // このDBはキャッシュとして使用しているため、upgrade時は単純に削除して作り直している。
            dropTables(db);
            createTables(db);
        }
    }
}
