package com.example.gptsi.roadie.Util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by GPTSI on 17-02-2018.
 */

public class SQLite extends SQLiteOpenHelper {

    private String mDBname;
    private String mTBname;
    private int mDBver;
    private Context mContext;
    private Class mClass;
    private ArrayList<ObjectUtils.Obj> mFields;
    private String id = "id";
    private String TAG = SQLite.class.getSimpleName();

    public SQLite(Context context, String dbname, int dbver, String tbname, Class cls) {
        super(context, dbname, null, dbver);
        mDBname = dbname;
        mDBver = dbver;
        mTBname = tbname;
        mClass = cls;
        mContext = context;
        ObjectUtils object = new ObjectUtils();
        mFields = object.getFieldNames(cls);

//        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("delete from "+ mTBname);

        Log.d(TAG,"database created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TypeUtil type = new TypeUtil();

        String query = "create table " + mTBname + " (" + id + " integer primary key autoincrement,";

        for (int i = 0; i < mFields.size(); i++) {
            ObjectUtils.Obj o = mFields.get(i);
            query += o.field + " " + type.getSQLType(o.type) + ",";
        }
        query = query.substring(0, query.lastIndexOf(",")) + ");";
        Log.d(TAG, query + "\ntable created");
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(Object object) {
        if(object!=null) {
            SQLiteDatabase db = this.getWritableDatabase();
            TypeUtil type = new TypeUtil();
            ContentValues cv = type.getSQLContentValues(object);
            db.insert(mTBname, null, cv);
            Log.d("TAG", "inserted");
        }
    }

    public void delete(String sid) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sql = "delete from " + mTBname + " where " + id + "=" + sid;

        //  db.execSQL(sql);

        db.delete(mTBname, id + "=" + sid, null);

    }

    public void update(String id, Object object) {
        SQLiteDatabase db = this.getWritableDatabase();
        TypeUtil type = new TypeUtil();
        ContentValues cv = type.getSQLContentValues(object);
        db.update(mTBname, cv, id + "=" + id, null);
    }

    public boolean isTable(String tableName) {
        boolean isExist = false;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isExist = true;
            }
            cursor.close();
        }
        return isExist;
    }

    public ArrayList<Object> getdata() {

        ArrayList<Object> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        if(isTable(mTBname)) {

            String sql = "select * from " + mTBname;

            Cursor c = db.rawQuery(sql, null);

            if (c.getCount() == 0)
                return null;

            while (c.moveToNext()) {
                Object obj = new TypeUtil().getSQLObject(c, mClass);
                list.add(obj);
            }
        }

        return list;
    }

    public class TypeUtil {

        public String getSQLType(Class<?> type) {
            Log.d("data123", type.getSimpleName().toLowerCase());
            switch (type.getSimpleName().toLowerCase()) {

                case "integer":
                case "int":
                    return "INT";

                case "float":
                    return "FLOAT";

                case "double":
                    return "DOUBLE";

                case "boolean":
                    return "BOOLEAN";

                case "string":
                    return "VARCHAR(255)";

                default:
                    return "null";
            }
        }

        public ContentValues getSQLContentValues(Object object) {

            ContentValues cv = new ContentValues();
            ObjectUtils obj = new ObjectUtils();

            try {
                mFields = obj.getFieldNamesAndValues(object);

                for (int i = 0; i < mFields.size(); i++) {
                    ObjectUtils.Obj o = mFields.get(i);

                    switch (o.type.getSimpleName().toLowerCase()) {

                        case "integer":
                        case "int":
                            cv.put(o.field, (int) o.value);
                            break;

                        case "float":
                            cv.put(o.field, (float) o.value);
                            break;

                        case "double":
                            cv.put(o.field, (double) o.value);
                            break;

                        case "boolean":
                            cv.put(o.field, (boolean) o.value);
                            break;

                        case "string":
                            cv.put(o.field, (String) o.value);
                            break;
                    }
                }
                return cv;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return cv;
        }

        public Object getSQLObject(Cursor c, Object refObj) {

            Object obj = null;

            try {
                Class cls = Class.forName(mClass.getName());
                Constructor[] constructors = cls.getDeclaredConstructors();
                int j;
                for (j = 0; j < constructors.length; j++) {
                    Log.d(TAG,"constructor : "+j+" Params : "+constructors[j].getParameterTypes().length);
                    if(constructors[j].getParameterTypes().length == 0)
                        break;
                }
                constructors[j].setAccessible(true);
                obj = cls.newInstance();

                Field[] fields = obj.getClass().getDeclaredFields();

                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);

                    if(fields[i].getName().contains("$")||fields[i].getName().contains("serialVersionUID")
                            || java.lang.reflect.Modifier.isStatic(fields[i].getModifiers()))
                        continue;


                    if((i+1) <= c.getColumnNames().length)
                        Log.d(TAG,"DB Name: "+c.getColumnName(i+1));

                    Log.d(TAG,"Obj Name: "+fields[i].getName());

                    switch (fields[i].getType().getSimpleName().toLowerCase()) {

                        case "integer":
                        case "int":
                            fields[i].set(obj, c.getInt(i+1));
                            break;

                        case "float":
                            fields[i].set(obj, c.getFloat(i+1));
                            break;

                        case "double":
                            fields[i].set(obj,c.getDouble(i+1));
                            break;

                        case "boolean":
                            fields[i].set(obj, c.getShort(i+1)==1? true : false);
                            break;

                        case "string":
                            fields[i].set(obj, c.getString(i+1));
                            break;

                            default:
                                fields[i].set(obj, 0);
                    }
                }
            } catch (IllegalAccessException e) {
                Log.e(TAG,e.getMessage(),e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                Log.e(TAG,e.getMessage(),e);
                e.printStackTrace();
            } catch (InstantiationException e) {
                Log.e(TAG,e.getMessage(),e);
                e.printStackTrace();
            }

            return obj;
        }
    }
}