package com.example.gptsi.roadie.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by GPTSI on 17-02-2018.
 */

public class ObjectUtils {

    class Obj {
        public String field;
        public Class<?> type;
        public Object value;
    }

    public ArrayList<Obj> getFieldNames(Class c) {

        ArrayList<Obj> ret = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {

            if(fields[i].getName().contains("$")||fields[i].getName().contains("serialVersionUID")
                    || java.lang.reflect.Modifier.isStatic(fields[i].getModifiers()))
                continue;

            Obj o = new Obj();
            o.field = fields[i].getName();
            o.type = fields[i].getType();
            ret.add(o);
        }
        return ret;
    }

    public ArrayList<Obj> getFieldNamesAndValues(final Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        Class<? extends Object> c = obj.getClass();
        ArrayList<Obj> ret = new ArrayList<>();
        Field[] fields = c.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {

            if(fields[i].getName().contains("$")||fields[i].getName().contains("serialVersionUID")
                    || java.lang.reflect.Modifier.isStatic(fields[i].getModifiers()))
                continue;

            Obj o = new Obj();
            o.field = fields[i].getName();
            o.type = fields[i].getType();
            o.value = fields[i].get(obj);
            ret.add(o);
        }
        return ret;
    }
}

//            if (publicOnly) {
//                if (Modifier.isPublic(fields[i].getModifiers())) {
//                    Object value = fields[i].get(obj);
//                    map.put(name, value);
//                }
//            } else {
//                fields[i].setAccessible(true);

/**
 * @param args
 */
//    public static void main(String[] args) throws Exception {
//
//        Dummy dummy = new Dummy();
//        System.out.println(ObjectUtils.getFieldNamesAndValues(dummy, true));
//        System.out.println(ObjectUtils.getFieldNamesAndValues(dummy, false));
//}

/*
     * output :
     *  {value3=43, value1=foo, value2=42}
     *  {value3
     *
*/