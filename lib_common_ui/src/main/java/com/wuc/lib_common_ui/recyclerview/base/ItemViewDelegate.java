package com.wuc.lib_common_ui.recyclerview.base;

/**
 * @author: wuchao
 * @date: 2019-10-18 16:23
 * @desciption:
 */
public interface ItemViewDelegate<T> {
    /**
     * 获取item 布局
     *
     * @return layout
     */
    int getItemViewLayoutId();

    /**
     * 是否是同一种类型
     *
     * @param item     item
     * @param position position
     * @return boolean
     */
    boolean isForViewType(T item, int position);

    /**
     * 布局 转换
     *
     * @param holder   holder
     * @param item     item
     * @param position position
     */
    void convert(ViewHolder holder, T item, int position);
}
