package com.hthyaq.salaryadmin.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionUtil {
    //集合是否为空
    public static boolean isNullOrEmpty(Collection c) {
        if (c == null || c.size() == 0) {
            return true;
        }
        return false;
    }

    //集合是否不为空
    public static boolean isNotNullOrEmpty(Collection c) {
        if (c != null && c.size() > 0) {
            return true;
        }
        return false;
    }

    //2个逗号分隔的字符串是否相同，例如，A,B与B,A是相同的
    public static boolean isCollectionItemEqual(String oldString, String newString) {
        List<String> oldList = Arrays.asList(oldString.split(","));
        List<String> newList = Arrays.asList(newString.split(","));
        Collection<String> newCollection = new ArrayList(newList);
        newCollection.removeAll(oldList);
        if (newCollection.size() == 0) {
            return true;
        }
        return false;
    }

    //取出oldString中的不在newString中的元素
    public static List<String> differentList(String oldString, String newString) {
        List<String> oldList = Arrays.asList(oldString.split(","));
        List<String> newList = Arrays.asList(newString.split(","));
        return differentList(oldList, newList);
    }

    public static List<String> differentList(List<String> oldList, List<String> newList) {
        Collection<String> oldCollection = new ArrayList(oldList);
        oldCollection.removeAll(newList);
        return (List<String>) oldCollection;
    }
}
