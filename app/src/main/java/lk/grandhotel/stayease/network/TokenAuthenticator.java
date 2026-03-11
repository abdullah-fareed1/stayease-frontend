package lk.grandhotel.stayease.network;

import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import lk.grandhotel.stayease.activities.LoginActivity;
import lk.grandhotel.stayease.network.models.RefreshResponse;
import lk.grandhotel.stayease.utils.TokenPrefs;
import lk.grandhotel.stayease.utils.UserPrefs;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Authenticator {

    private final Context context;
    private final String baseUrl;

    public TokenAuthenticator(Context context, String baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) >= 2) {
            forceLogout();
            return null;
        }

        String refreshToken = TokenPrefs.getRefreshToken(context);
        if (refreshToken == null) {
            forceLogout();
            return null;
        }

        ApiService refreshService = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build()
                .create(ApiService.class);

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        try {
            retrofit2.Response<RefreshResponse> refreshResponse =
                    refreshService.refresh(body).execute();

            if (refreshResponse.isSuccessful()
                    && refreshResponse.body() != null
                    && refreshResponse.body().status
                    && refreshResponse.body().data != null) {

                String newAccessToken = refreshResponse.body().data.accessToken;
                String newRefreshToken = refreshResponse.body().data.refreshToken;

                TokenPrefs.saveTokens(context, newAccessToken, newRefreshToken);

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccessToken)
                        .build();
            } else {
                forceLogout();
                return null;
            }
        } catch (Exception e) {
            forceLogout();
            return null;
        }
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) {
            count++;
        }
        return count;
    }

    private void forceLogout() {
        TokenPrefs.clearTokens(context);
        UserPrefs.clear(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}