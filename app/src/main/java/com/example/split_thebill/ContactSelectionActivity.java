package com.example.split_thebill;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import android.content.ClipData;

public class ContactSelectionActivity extends AppCompatActivity {

    public static final int CONTACT_SELECTION_REQUEST = 1001;
    private static final int CONTACT_PICKER_RESULT = 1;
    private List<String> selectedContacts = new ArrayList<>();
    private double amountPerContact = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_selection);

        double totalBill = getIntent().getDoubleExtra("totalBill", 0.0);

        Button btnSelectContacts = findViewById(R.id.btnSelectContacts);
        btnSelectContacts.setOnClickListener(view -> pickContacts());

        Button btnBackToMain = findViewById(R.id.btnBackToMain);
        btnBackToMain.setOnClickListener(view -> navigateBackToMain(totalBill));
    }

    private void pickContacts() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        contactPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CONTACT_PICKER_RESULT && resultCode == RESULT_OK && data != null) {
            handleSelectedContacts(data);
        }
    }

    private void handleSelectedContacts(Intent data) {
        if (data.getData() != null) {
            Uri contactUri = data.getData();
            String contactNumber = getContactNumber(contactUri);

            if (contactNumber != null && !selectedContacts.contains(contactNumber)) {
                selectedContacts.add(contactNumber);
                Log.d("ContactSelection", "Selected Contact: " + contactNumber);
            } else {
                Log.e("ContactSelection", "Invalid contact selection");
            }
        } else if (data.getClipData() != null) {
            ClipData clipData = data.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri contactUri = clipData.getItemAt(i).getUri();
                String contactNumber = getContactNumber(contactUri);

                if (contactNumber != null && !selectedContacts.contains(contactNumber)) {
                    selectedContacts.add(contactNumber);
                    Log.d("ContactSelection", "Selected Contact: " + contactNumber);
                } else {
                    Log.e("ContactSelection", "Invalid contact selection");
                }
            }
        }
    }

    private String getContactNumber(Uri contactUri) {
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    return cursor.getString(numberIndex);
                } else {
                    Log.e("ContactSelection", "Error moving to the first row of the cursor");
                }
            } finally {
                cursor.close();
            }
        } else {
            Log.e("ContactSelection", "Error querying the cursor");
        }

        return null;
    }

    private void navigateBackToMain(double totalBill) {
        if (selectedContacts.size() > 0) {
            amountPerContact = totalBill / (selectedContacts.size()+1);
        }

        StringBuilder contactsStringBuilder = new StringBuilder();
        for (String contact : selectedContacts) {
            contactsStringBuilder.append(contact).append(";");
        }
        String contactsString = contactsStringBuilder.toString();

        String smsMessage = "Please pay your share: ₹" + Math.round(amountPerContact) ;

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("smsto:" + contactsString));
        smsIntent.putExtra("sms_body", smsMessage);

        startActivity(smsIntent);
        finish();
    }
}
