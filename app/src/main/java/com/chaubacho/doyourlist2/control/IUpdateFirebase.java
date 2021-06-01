package com.chaubacho.doyourlist2.control;

import com.chaubacho.doyourlist2.data.model.Item;

public interface IUpdateFirebase {
    void getDataFromFirebase();
    void addDataToFirebase(Item item);
    void updateDataToFirebase(Item item);
    void deleteDataInFirebase(Item item);
}
