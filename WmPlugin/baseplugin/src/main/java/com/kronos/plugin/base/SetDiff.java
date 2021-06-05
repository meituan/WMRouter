package com.kronos.plugin.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SetDiff<T> {

    private List<T> addedList = new ArrayList<>();
    private List<T> unchangedList = new ArrayList<>();
    private List<T> removedList = new ArrayList<>();

    public SetDiff(Set<T> beforeList, Set<T> afterList) {
        addedList.addAll(afterList); // Will contain only new elements when all elements in the Before-list are removed.
        beforeList.forEach(e -> {
            boolean b = addedList.remove(e) ? unchangedList.add(e) : removedList.add(e);
        });
    }

    public List<T> getAddedList() {
        return addedList;
    }

    public List<T> getUnchangedList() {
        return unchangedList;
    }

    public List<T> getRemovedList() {
        return removedList;
    }
}
