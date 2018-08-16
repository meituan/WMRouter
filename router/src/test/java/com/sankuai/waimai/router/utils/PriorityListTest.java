package com.sankuai.waimai.router.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class PriorityListTest {

    @Test
    public void testItr() {
        List<Object> list = new ArrayList<>();
        list.add("5");
        ListIterator<Object> iterator = list.listIterator();
        Object next = iterator.next();
        System.out.println(next);
        Object previous = iterator.previous();
        System.out.println(previous);
    }

    @Test
    public void testSort() throws Exception {
        testSort(new int[]{}, new int[]{});
        testSort(new int[]{1}, new int[]{1});
        testSort(new int[]{1, 3}, new int[]{3, 1});
        testSort(new int[]{1, 3, 5}, new int[]{5, 3, 1});
        testSort(new int[]{5, 4}, new int[]{5, 4});
        testSort(new int[]{5, 4, 7}, new int[]{7, 5, 4});
        testSort(new int[]{1, 3, 5, 4, 7, 6}, new int[]{7, 6, 5, 4, 3, 1});
        testSort(new int[]{1, 3, 5, 4, 7, 6, 0}, new int[]{7, 6, 5, 4, 3, 1, 0});
    }

    private void testSort(int[] data, int[] expectedData) {
        PriorityList<Integer> list = new PriorityList<>();
        int length = data.length;
        for (int d : data) {
            list.addItem(d, d);
        }
        final int[] realData = new int[length];
        list.forEach(new Consumer<Integer>() {
            int i = 0;

            @Override
            public void accept(Integer integer) {
                realData[i++] = integer;
            }
        });
        Assert.assertArrayEquals(expectedData, realData);
    }

    @Test
    public void testStable() throws Exception {
        int[] data = {1, 2, 3, 4, 5, 6, 7};
        testStable(data);
    }

    private void testStable(int[] data) {
        PriorityList<Integer> list = new PriorityList<>();
        int length = data.length;
        for (int d : data) {
            list.addItem(d, 0);
        }
        final int[] realData = new int[length];
        list.forEach(new Consumer<Integer>() {
            int i = 0;

            @Override
            public void accept(Integer integer) {
                realData[i++] = integer;
            }
        });
        Assert.assertArrayEquals(data, realData);
    }
}