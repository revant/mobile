package io.frappe.frappeauthenticator.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.TOKEN_ENDPOINT;
import static io.frappe.frappeauthenticator.authenticator.AccountGeneral.sServerAuthenticate;

/**
 * Created with IntelliJ IDEA.
 * User: Frappe
 * Date: 19/03/13
 * Time: 18:58
 */
public class FrappeAuthenticator extends AbstractAccountAuthenticator {

    private String TAG = "Frappe Authenticator";
    private final Context mContext;

    public FrappeAuthenticator(Context context) {
        super(context);

        // I hate you! Google - set mContext as protected!
        this.mContext = context;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.d("frappe", TAG + "> addAccount");

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        Log.d("frappe", TAG + "> getAuthToken");

        // If the caller requested an authToken type we don't support, then
        // return an error
        if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) && !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
            Bundle result = new Bundle();
            result = getBundle("invalid_token_type",AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,account,response);
            return result;
        }

        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        final AccountManager am = AccountManager.get(mContext);

        String authToken = am.getUserData(account, "authtoken");
        String accessToken = am.getUserData(account, "accessToken");
        String refreshToken = am.getUserData(account, "refreshToken");
        String frappeServer = am.getUserData(account, "frappeServer");
        String CLIENT_ID = am.getUserData(account, "clientId");
        String REDIRECT_URI = am.getUserData(account, "redirectURI");
        JSONObject openIDProfile = sServerAuthenticate.getOpenIDProfile(accessToken,frappeServer+AccountGeneral.OPENID_PROFILE_ENDPOINT);

        //Log.d("frappe", TAG + "> authToken returned - " + authToken);
        Log.d("frappe", TAG + "> openid isnull - " + openIDProfile.isNull("email"));
        Log.d("frappe", TAG + "> at isnull - " + accessToken);

        // Lets give another try to authenticate the user
        if (TextUtils.isEmpty(accessToken) || openIDProfile.isNull("email")) {
            try {
                Log.d("frappe", TAG + "> re-authenticating with the refresh token");
                String TOKEN_URL = frappeServer + TOKEN_ENDPOINT;
                JSONObject authMethod = new JSONObject();
                authMethod.put("type", "refresh");
                authMethod.put("refresh_token", refreshToken);
                authToken = sServerAuthenticate.userSignIn(TOKEN_URL,authMethod,CLIENT_ID,REDIRECT_URI);
                JSONObject bearerToken = new JSONObject(authToken);
                am.setAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, authToken);
                am.setUserData(account, "authtoken", authToken);
                am.setUserData(account, "refreshToken", bearerToken.getString("refresh_token"));
                am.setUserData(account, "accessToken", bearerToken.getString("access_token"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // If we get an authToken - we return it
        if (!TextUtils.isEmpty(authToken)) {
            Bundle result = new Bundle();
            result = getBundle("valid",AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,account,response);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }
        // If we get here, then we couldn't access the user's password - so we
        // need to re-prompt them for their credentials. We do that by creating
        // an intent to display our AuthenticatorActivity.
        Bundle result = getBundle("new_intent",AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,account,response);
        return result;
    }

    public Bundle getBundle(String bundleType, String authTokenType, Account account, AccountAuthenticatorResponse response){
        // bundleType - invalid_token_type, new_intent, valid
        Bundle result = new Bundle();
        if (bundleType == "invalid_token_type"){
            result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
        }else if(bundleType == "new_intent"){
            final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.name);
            result.putParcelable(AccountManager.KEY_INTENT, intent);
        }else if(bundleType == "valid"){
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
        }
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        if (AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
            return AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
        else if (AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
            return AUTHTOKEN_TYPE_READ_ONLY_LABEL;
        else
            return authTokenType + " (Label)";
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }
}
