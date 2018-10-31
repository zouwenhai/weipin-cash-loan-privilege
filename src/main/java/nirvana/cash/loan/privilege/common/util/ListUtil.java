package nirvana.cash.loan.privilege.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2018/9/17.
 */
public class ListUtil {

    //判断列表是否为空
    public static <T> boolean isEmpty(List<T> list){
        return  list == null || list.size() == 0;
    }

    //判断列表是否为非空
    public static <T> boolean isNotEmpty(List<T> list){
        return  list != null && list.size() > 0;
    }

    //获取N条记录，并从列表中移除这些订单
    public static <T> List<T> popItemList(List<T> list, int size) {
        List<T> itemList = new ArrayList<>();
        int i = 1;
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            if (i > size) {
                break;
            }
            T item = it.next();
            itemList.add(item);
            it.remove();
            i++;
        }
        return itemList;
    }
}
