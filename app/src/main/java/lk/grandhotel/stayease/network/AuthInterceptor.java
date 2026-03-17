package lk.grandhotel.stayease.network;

import android.content.Context;

import java.io.IOException;

import lk.grandhotel.stayease.utils.AdminTokenPrefs;
import lk.grandhotel.stayease.utils.TokenPrefs;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String path = original.url().encodedPath();

        String token;
        if (path.contains("/admin/")) {
            token = AdminTokenPrefs.getAccessToken(context);
        } else {
            token = TokenPrefs.getAccessToken(context);
        }

        if (token == null) {
            return chain.proceed(original);
        }

        Request request = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        return chain.proceed(request);
    }
}