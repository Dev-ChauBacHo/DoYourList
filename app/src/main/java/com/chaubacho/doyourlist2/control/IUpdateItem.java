package com.chaubacho.doyourlist2.control;

import com.chaubacho.doyourlist2.data.model.Item;

public interface IUpdateItem {
    void addItem(Item item);
    void updateItem(Item item);
    void deleteItem(Item item);
}
