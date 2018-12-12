package com.example.mosaab.orderfoodserver.Service;

import com.example.mosaab.orderfoodserver.Common.Common;
import com.example.mosaab.orderfoodserver.model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class Firebase_Id_Service extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Update_ToServer(refreshedToken);
    }

    private void Update_ToServer(String refreshedToken) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens  =db.getReference("Tokens");
        Token data = new Token(refreshedToken,true);
        tokens.child(Common.current_user.getPhone()).setValue(data);
    }
}
