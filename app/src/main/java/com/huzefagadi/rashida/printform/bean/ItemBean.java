package com.huzefagadi.rashida.printform.bean;

import android.view.Menu;

/**
 * Created by Rashida on 06/07/15.
 */
public class ItemBean {

    private Integer id;
    private MenuBean menu;
    private String description;

    public ItemBean() {
    }

    public ItemBean(Integer id, MenuBean menu, String description) {
        this.id = id;
        this.menu = menu;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MenuBean getMenu() {
        return menu;
    }

    public void setMenu(MenuBean menu) {
        this.menu = menu;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
