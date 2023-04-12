package com.example.adutucartrider.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.adutucartrider.db.FirebaseRepo;
import com.example.adutucartrider.models.PendingOrderList;
import com.example.adutucartrider.models.PendingOrders;
import com.example.adutucartrider.models.PickupOrderList;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class FirebaseViewModel extends ViewModel implements FirebaseRepo.OnRealTimeDbTaskComplete {

    private MutableLiveData<List<PickupOrderList>> orderMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<List<PendingOrderList>> riderOrderMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<List<PickupOrderList>> riderPickupMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DatabaseError> databaseErrorMutableLiveData = new MutableLiveData<>();
    private FirebaseRepo firebaseRepo;

    public MutableLiveData<List<PendingOrderList>> getOrderMutableLiveDataRider() {
        return riderOrderMutableLiveData;
    }

    public MutableLiveData<List<PickupOrderList>> getOrderMutableLiveDataPickup() {
        return riderPickupMutableLiveData;
    }

    public MutableLiveData<List<PickupOrderList>> getOrderMutableLiveData() {
        return orderMutableLiveData;
    }

    public MutableLiveData<DatabaseError> getDatabaseErrorMutableLiveData() {
        return databaseErrorMutableLiveData;
    }

    public FirebaseViewModel(){
        firebaseRepo = new FirebaseRepo(this);
    }

    public void getAllData(){
        firebaseRepo.getAllOrderData();
    }
    public void getAllDataRider(){
        firebaseRepo.getAllOrderDataRider();
    }

    @Override
    public void onSuccess(List<PickupOrderList> pickupOrderLists) {
        orderMutableLiveData.setValue(pickupOrderLists);
    }
    public void onSuccessRider(List<PendingOrderList> pendingOrderListList){
        riderOrderMutableLiveData.setValue(pendingOrderListList);
    }

    public void onSuccessPickup(List<PickupOrderList> pickupOrderLists){
        riderPickupMutableLiveData.setValue(pickupOrderLists);
    }

    @Override
    public void onFailure(DatabaseError error) {
        databaseErrorMutableLiveData.setValue(error);
    }

}