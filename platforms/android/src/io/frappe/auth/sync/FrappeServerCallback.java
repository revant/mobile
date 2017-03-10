package io.frappe.auth.sync;

/**
 * Created by revant on 28/2/17.
 */

import org.json.JSONObject;

public interface FrappeServerCallback{
    public void onSuccessJSONObject(JSONObject result);
}
