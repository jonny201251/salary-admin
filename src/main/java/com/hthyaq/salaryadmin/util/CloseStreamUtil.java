package com.hthyaq.salaryadmin.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//关闭流
public class CloseStreamUtil {
    public static void close(Object o) {
        Class clazz = o.getClass();
        if (o != null) {
            new ReflectUtil().invoke(o,"close");
        }

        if (o instanceof InputStream) {
            InputStream inputStream = (InputStream) o;
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (o instanceof OutputStream) {
            OutputStream outputStream = (OutputStream) o;
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
