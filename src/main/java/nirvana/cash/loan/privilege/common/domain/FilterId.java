package nirvana.cash.loan.privilege.common.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/8/13.
 */
public class FilterId {

    private long id;
    private long parentId;
    private String name;

    public static List<FilterId> filterRemoveList(List<FilterId> allList, long id) {
        List<FilterId> resList = new ArrayList<>();
        filterById(resList, allList, id);
        filterSubList(resList, allList, id);
        return resList;
    }


    private static FilterId filterById(List<FilterId> resList, List<FilterId> allList, long id) {
        List<FilterId> list = allList.stream().filter(t -> t.getId() == id).collect(Collectors.toList());
        resList.add(list.get(0));
        return list.get(0);
    }

    private static List<FilterId> filterSubList(List<FilterId> resList, List<FilterId> allList, long id) {
        List<FilterId> subList = new ArrayList<>();
        for (FilterId item : allList) {
            if (item.getParentId() == id) {
                subList.add(item);
            }
        }
        if (subList.size() > 0) {
            resList.addAll(subList);
            for (FilterId sub : subList) {
                filterSubList(resList, allList, sub.getId());
            }
        }
        return subList;
    }


    public FilterId(long id, long parentId, String name) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
