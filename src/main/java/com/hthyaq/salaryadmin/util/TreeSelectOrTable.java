package com.hthyaq.salaryadmin.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hthyaq.salaryadmin.vo.TreeTableData;
import com.hthyaq.salaryadmin.vo.TreeSelectData;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class TreeSelectOrTable {
    //表格树
    public static <T> List<TreeTableData> getTreeTable(List<T> startData){
        if(!CollectionUtil.isNotNullOrEmpty(startData)) throw new RuntimeException("集合为空！");
        List<TreeTableData> treeTableDatas = Lists.newArrayList();
        HashMap<Integer, TreeTableData> map = Maps.newHashMap();
        String fieldName = "";
        List<Integer> ids = Lists.newArrayList();
        for (T obj : startData) {
            TreeTableData treeTableData=new TreeTableData();
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                fieldName = field.getName();
                setTreeTableData(obj,fieldName,treeTableData);
            }
            Integer pid = (Integer) invokeMethod(obj, "pid");
            Integer id = (Integer) invokeMethod(obj, "id");
            treeTableData.setKey(id);
            treeTableData.setId(id);
            ids.add(id);
            if (pid == 0) {
                treeTableData.setIds(ids);
                treeTableDatas.add(treeTableData);
                map.put(id, treeTableData);
            } else {
                map.get(pid).getChildren().add(treeTableData);
                map.put(id, treeTableData);
            }
        }
        return treeTableDatas;
    }
    private static <T> TreeTableData setTreeTableData(T obj,String fieldName,TreeTableData treeTableData){
        String value="";
        if(fieldName.matches("(name|pname|status|type)")){
            value=(String) invokeMethod(obj,fieldName);
        }
        if("name".equals(fieldName)){
            treeTableData.setName(value);
        }else if("pname".equals(fieldName)){
            treeTableData.setPname(value);
        }else if("status".equals(fieldName)){
            treeTableData.setStatus(value);
        }else if("type".equals(fieldName)){
            treeTableData.setType(value);
        }
        return treeTableData;

    }
    //下拉树
    public static <T> List<TreeSelectData> getTreeSelect(List<T> startData) {
        if(!CollectionUtil.isNotNullOrEmpty(startData)) throw new RuntimeException("集合为空！");
        List<TreeSelectData> treeSelectDatas = Lists.newArrayList();
        HashMap<Integer, TreeSelectData> map = Maps.newHashMap();
        for (T obj : startData) {
            Integer pid = (Integer) invokeMethod(obj, "pid");
            Integer id = (Integer) invokeMethod(obj, "id");
            String name = (String) invokeMethod(obj, "name");
            if (pid == 0) {
                TreeSelectData treeSelectData = new TreeSelectData();
                treeSelectData.setTitle(name);
                treeSelectData.setValue(id);
                treeSelectData.setKey(id);
                treeSelectDatas.add(treeSelectData);
                map.put(id, treeSelectData);
            } else {
                TreeSelectData child = new TreeSelectData();
                child.setTitle(name);
                child.setValue(id);
                child.setKey(id);
                map.get(pid).getChildren().add(child);
                map.put(id, child);
            }
        }
        return treeSelectDatas;
    }

    private static <T> Object invokeMethod(T obj, String fieldName) {
        Object value = null;
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            Method method = pd.getReadMethod();
            value = method.invoke(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
