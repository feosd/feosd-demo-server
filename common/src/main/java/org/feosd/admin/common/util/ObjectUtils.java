package org.feosd.admin.common.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectUtils {

    /**
     * 删除数组中的指定值  或者数组中的元素包含指定值
     * @param oldArray       原数组
     * @param removeArray    需删除的值
     * @return
     */
    public static String[] removeArray(String[] oldArray, String[] removeArray){
        String[] res = null;
        if(oldArray.length > 0) {
            List<String> tempList = Arrays.asList(oldArray);
            //Arrays.asList(oldArray) 迭代器实现类 不支持remove() 删除，多一步转化
            List<String> arrList = new ArrayList<>(tempList);
            Iterator<String> it = arrList.iterator();
            while(it.hasNext()) {
                String x = it.next();
                for(String str : removeArray){
                    if(x.indexOf(str) != -1) {
                        it.remove();
                    }
                }
            }
            res = new String[arrList.size()];
            arrList.toArray(res);
        }
        return res;
    }

    public static String[] addArray(String[] oldArray, String[] add){
        List<String> list = new ArrayList(Arrays.asList(oldArray));
        list.addAll(Arrays.asList(add));
        String[] res =  list.toArray(new String[list.size()]);
        return res;
    }

    /**
     * 通过HashSet踢除List中的重复元素
     * @param list
     * @return
     */
    public static List removeDuplicate(List list) {
        LinkedHashSet h = new LinkedHashSet(list);
        list.clear();
        list.addAll(h);
        return list;
    }

    public static String getAdName(String adLevel){
        String adName = null;
        if("2".equals(adLevel)){
            adName = "市";
        }else if("3".equals(adLevel)){
            adName = "县";
        }else if("4".equals(adLevel)){
            adName = "乡";
        }else if("5".equals(adLevel)){
            adName = "村";
        }
        return adName;
    }

    /**
     * 判断是否为整数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public final static boolean isNumeric(String str) {
        return isMatch("^[0-9]*$",str);
    }

    /**
     * 判断是否为正小数
     * @param str 传入的字符串
     * @return 是整数返回true,否则返回false
     */
    public static boolean isPositiveDecimal(String str){
        return isMatch("\\+{0,1}[0]\\.[1-9]*|\\+{0,1}[1-9]\\d*\\.\\d*", str);
    }

    private static boolean isMatch(String regex, String str){
        if (str == null || str.trim().equals("")) {
            return false;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static void main(String[] args) {
        String[] oldStrs = new String[]{"1","2","3","1"};
        String[] newStrs = addArray(oldStrs,new String[]{"1","3"});
        List<Map<String,String>> list = new ArrayList<>();
        Map<String,String> map = new HashMap<>();
        map.put("code","1");
        map.put("name","111");
        list.add(map);
        list.add(map);
        Map<String,String> map2 = new HashMap<>();
        map2.put("code","2");
        map2.put("name","222");
        list.add(map2);

        System.out.println(list);
        System.out.println(removeDuplicate(list));
        System.out.println(isPositiveDecimal("12.1"));
    }
}
