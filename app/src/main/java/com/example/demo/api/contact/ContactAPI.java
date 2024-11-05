package com.example.demo.api.contact;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import java.util.ArrayList;
import java.util.List;

public class ContactAPI {

    public static class Contact {
        private String name;
        private List<String> phoneNumbers;

        public Contact(String name, List<String> phoneNumbers) {
            this.name = name;
            this.phoneNumbers = phoneNumbers;
        }

        // Getters
        public String getName() {
            return name;
        }

        public List<String> getPhoneNumbers() {
            return phoneNumbers;
        }
    }

    public static List<Contact> getContactList(ContentResolver contentResolver, int offset, int limit) {
        List<Contact> contactList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " ASC LIMIT " + limit + " OFFSET " + offset
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));

                    List<String> phoneNumbers = new ArrayList<>();
                    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor phoneCursor = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null
                        );

                        if (phoneCursor != null) {
                            while (phoneCursor.moveToNext()) {
                                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phoneNumbers.add(phoneNumber);
                            }
                            phoneCursor.close();
                        }
                    }

                    contactList.add(new Contact(name, phoneNumbers));
                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Handle error
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return contactList;
    }
}
