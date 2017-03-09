package io.frappe.frappeauthenticator.sync;

/**
 * Created by revant on 1/3/17.
 */

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.RawContacts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContactsHelper {

    public static void addContact(Account account, JSONObject contactInfo, ContentResolver mContentResolver) {

        String contactName = null;
        String supplierName = null;
        String customerName = null;
        String salePartnerName = null;
        String lastName = null;
        String emailID = null;
        String mobileNo = null;
        String firstName = null;
        String department = null;
        String designation = null;
        String phone = null;
        String displayName = null;

        try {
            contactName = contactInfo.getString("name");
            customerName = contactInfo.getString("customer_name");
            supplierName = contactInfo.getString("supplier_name");
            salePartnerName = contactInfo.getString("sales_partner");
            lastName = contactInfo.getString("last_name");
            emailID = contactInfo.getString("email_id");
            mobileNo = contactInfo.getString("mobile_no");
            firstName = contactInfo.getString("first_name");
            department = contactInfo.getString("department");
            designation = contactInfo.getString("designation");
            phone = contactInfo.getString("phone");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!firstName.equals("null") && !lastName.equals("null")){
            displayName = firstName + " " + lastName;
        }
        else if (!firstName.equals("null") && lastName.equals("null")){
            displayName = firstName;
        }
        else if (!lastName.equals("null") && firstName.equals("null")){
            displayName = lastName;
        }

        //Create our RawContact
        ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
        op_list.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(
                RawContacts.CONTENT_URI, true))
                .withValue(RawContacts.ACCOUNT_TYPE, account.type)
                .withValue(RawContacts.ACCOUNT_NAME, account.name)
                .withValue(RawContacts.RAW_CONTACT_IS_READ_ONLY,"1")
                .withValue(RawContacts.SYNC1,contactName)
                .withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT)
                .build());

        // first and last names

        if (displayName!=null && !displayName.isEmpty()){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0)
                    .withValue(RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, displayName)
                    .build());
        }

        if (!firstName.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0)
                    .withValue(RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
                    .build());
        }

        if (!lastName.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0)
                    .withValue(RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
                    .build());
        }
        // add phone number
        if (!phone.equals("null")) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Phone.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, phone)
                    .withValue(Phone.TYPE, Phone.TYPE_WORK)
                    .build());
        }

        //add mobile number
        if (!mobileNo.equals("null")) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(Phone.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                    .withValue(Phone.NUMBER, mobileNo)
                    .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                    .build());
        }

        //add email
        if (!emailID.equals("null")) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.ADDRESS, emailID)
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        //add Customer
        if(!customerName.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, customerName)
                    .build());
        }

        //add Supplier
        if(!supplierName.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, supplierName)
                    .build());
        }

        //add Sales Partner
        if(!salePartnerName.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, salePartnerName)
                    .build());
        }

        //add Department
        if(!department.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.DEPARTMENT, department)
                    .build());
        }

        //add Designation
        if(!designation.equals("null")){
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.CommonDataKinds.Organization.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, designation)
                    .build());
        }

        op_list.add(ContentProviderOperation.newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.io.frappe.frappeauthenticator.contact")
                .withValue(ContactsContract.Data.DATA1, mobileNo)
                .withValue(ContactsContract.Data.DATA2, displayName)
                .withValue(ContactsContract.Data.DATA3, contactName)
                .withValue(ContactsContract.Data.DATA4, phone)
                .withValue(ContactsContract.Data.DATA5, emailID)
                .withValue(ContactsContract.Data.DATA6, firstName)
                .withValue(ContactsContract.Data.DATA7, lastName)
                .withValue(ContactsContract.Data.DATA8, designation)
                .withValue(ContactsContract.Data.DATA9, department)
                .withValue(ContactsContract.Data.DATA10, customerName)
                .withValue(ContactsContract.Data.DATA11, supplierName)
                .withValue(ContactsContract.Data.DATA12, salePartnerName)
                .build());

        try{
            ContentProviderResult[] results = mContentResolver.applyBatch(ContactsContract.AUTHORITY, op_list);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void updateContact(Account account, JSONObject contactInfo, ContentResolver mContentResolver) {

    }

    public void deleteContact(Account account, String name, ContentResolver mContentResolver) {

        String where = RawContacts.SYNC1 + " = ? AND " + RawContacts.ACCOUNT_TYPE + " = ? AND " + RawContacts.ACCOUNT_NAME + " = ? ";
        String[] params = new String[] {name, account.type, account.name};

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newDelete(addCallerIsSyncAdapterParameter(
                RawContacts.CONTENT_URI, true))
                .withSelection(where, params)
                .build());
        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static Uri addCallerIsSyncAdapterParameter(Uri uri, boolean isSyncOperation) {
        if (isSyncOperation) {
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER,
                            "true").build();
        }
        return uri;
    }

}
