package com.example.web;

import com.example.dao.TypeDao;
import com.example.domain.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SparkApp {

    private static Map<String,String> courses = new HashMap<>();

    static {
        courses.put("1","电影");       // 400
        courses.put("2","综艺");       // 592
        courses.put("3","动漫");       // 484
        courses.put("4", "娱乐");      // 200
        courses.put("5", "纪录片");    // 127
        courses.put("6", "电视剧");    // 285
    }

    @Autowired
    TypeDao typeDao = new TypeDao();

    @ResponseBody
    @RequestMapping(value = "/typelist", method = RequestMethod.POST)
    public List<Type> query() throws Exception {
        List<Type> list = typeDao.query("20180919");
        for(Type type:list){
            type.setName(courses.get(type.getName().substring(9)));
        }
        return list;
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public ModelAndView echarts(){
        return new ModelAndView("count");
    }

    public static void main(String[] args) throws Exception{
        SparkApp app = new SparkApp();
        List<Type> list = app.query();
        for(Type type : list) {
            System.out.println(type.getName() + "-->" + type.getValue());
        }
    }
    /*  纪录片-->127
        电视剧-->285
        动漫-->484
        娱乐-->200
        电影-->400
        综艺-->592    */
}
