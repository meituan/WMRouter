package com.sankuai.waimai.router.utils;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.annotation.NonNull;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * 按优先级从大到小排列。优先级相同时，后加入的放后面。
 *
 * Created by jzj on 2018/4/27.
 */

public class PriorityList<T> extends AbstractList<T> {

    private final LinkedList<Node<T>> mList;
    private final int mDefaultPriority;

    public PriorityList(int defaultPriority) {
        mList = new LinkedList<>();
        mDefaultPriority = defaultPriority;
    }

    public PriorityList() {
        this(0);
    }

    /**
     * 添加Item并指定优先级
     */
    public boolean addItem(T item, int priority) {
        Node<T> node = new Node<>(item, priority);
        if (mList.isEmpty()) {
            mList.add(node);
            return true;
        }
        // 插入排序，list中的priority从大到小排列
        ListIterator<Node<T>> iterator = mList.listIterator();
        while (iterator.hasNext()) {
            Node<T> next = iterator.next();
            if (next.priority < priority) {
                iterator.previous();
                iterator.add(node);
                return true;
            }
        }
        mList.addLast(node);
        return true;
    }

    /**
     * @deprecated 不支持添加到指定位置
     */
    @Deprecated
    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException("不支持添加到指定位置");
    }

    public boolean addItem(T item) {
        return addItem(item, mDefaultPriority);
    }

    public boolean add(T item) {
        return addItem(item, mDefaultPriority);
    }

    public boolean remove(Object item) {
        Iterator<Node<T>> iterator = mList.iterator();
        while (iterator.hasNext()) {
            Node<T> node = iterator.next();
            if (node.data == item) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public int size() {
        return mList.size();
    }

    public T get(int index) {
        return mList.get(index).data;
    }

    public int getPriory(int index) {
        return mList.get(index).priority;
    }

    @Override
    public T set(int index, T element) {
        Node<T> node = mList.get(index);
        T t = node.data;
        node.data = element;
        return t;
    }

    @NonNull
    @Override
    public Iterator<T> iterator() {
        return new NodeIterator();
    }

    private static class Node<T> {

        int priority;
        T data;

        Node(T data, int priority) {
            this.data = data;
            this.priority = priority;
        }
    }

    private class NodeIterator implements Iterator<T> {

        private final ListIterator<Node<T>> mIterator;

        public NodeIterator() {
            this(0);
        }

        public NodeIterator(int index) {
            mIterator = mList.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            return mIterator.hasNext();
        }

        @Override
        public T next() {
            return mIterator.next().data;
        }

        @Override
        public void remove() {
            mIterator.remove();
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public void forEachRemaining(final Consumer<? super T> action) {
            mIterator.forEachRemaining(new Consumer<Node<T>>() {
                @Override
                public void accept(Node<T> node) {
                    action.accept(node.data);
                }
            });
        }
    }
}
