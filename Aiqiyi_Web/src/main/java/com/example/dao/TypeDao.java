package com.example.dao;

import com.example.domain.Type;
import com.example.utils.HBaseUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TypeDao {

    public List<Type> query(String day) throws IOException {
        List<Type> list = new ArrayList<>();
        Map<String,Long> map = HBaseUtil.getInstance().query("type",day);
        for (Map.Entry<String, Long> entry : map.entrySet()) {
            Type type = new Type();
            type.setName(entry.getKey());;
            type.setValue(entry.getValue());
            list.add(type);
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        TypeDao dao = new TypeDao();
        List<Type> list = dao.query("2018");
        for (Type c : list) {
            System.out.println(c.getName() + "-->" +c.getValue());
            // 20180918_1-->8
        }
    }
}
