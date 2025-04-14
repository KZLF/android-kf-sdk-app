package com.m7.imkfsdk.chat.model;

import java.util.ArrayList;

/**
 * @Description:
 * @Author: R-D
 * @Date: 2019-12-31
 */
public class OrderBaseDataBean {
    private String empty_message;
    private String message;
    private String list_title;
    private String list_num;
    private ArrayList<OrderInfoBean> shop_list;
    private ArrayList<OrderInfoBean> item_list;
    private int shop_list_show;//几条之后 显示查看更多
    public int getShop_list_show() {
        return shop_list_show;
    }

    public void setShop_list_show(int shop_list_show) {
        this.shop_list_show = shop_list_show;
    }



    public String getList_title() {
        return list_title;
    }

    public OrderBaseDataBean setList_title(String list_title) {
        this.list_title = list_title;
        return this;
    }

    public String getList_num() {
        return list_num;
    }

    public OrderBaseDataBean setList_num(String list_num) {
        this.list_num = list_num;
        return this;
    }

    public ArrayList<OrderInfoBean> getItem_list() {
        return item_list;
    }

    public OrderBaseDataBean setItem_list(ArrayList<OrderInfoBean> item_list) {
        this.item_list = item_list;
        return this;
    }

    public String getEmpty_message() {
        return empty_message;
    }

    public OrderBaseDataBean setEmpty_message(String empty_message) {
        this.empty_message = empty_message;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public OrderBaseDataBean setMessage(String message) {
        this.message = message;
        return this;
    }

    public ArrayList<OrderInfoBean> getShop_list() {
        return shop_list;
    }

    public OrderBaseDataBean setShop_list( ArrayList<OrderInfoBean> shop_list) {
        this.shop_list = shop_list;
        return this;
    }
}
