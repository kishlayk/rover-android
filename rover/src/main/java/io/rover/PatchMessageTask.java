package io.rover;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import io.rover.model.Message;
import io.rover.network.HttpResponse;
import io.rover.network.JsonApiPayloadProvider;
import io.rover.network.NetworkTask;

/**
 * Created by chrisrecalis on 2016-11-29.
 */

public class PatchMessageTask extends AsyncTask<Message, Void, Boolean> {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected Boolean doInBackground(Message... messages) {
        
        if (messages.length == 0)
            return false;

        Message message = messages[0];


        NetworkTask networkTask = Router.getPatchMessageNetworkTask(message.getId());

        if (networkTask == null) {
            return false;
        }

        JsonApiPayloadProvider.JsonApiObjectSerializer serializer = new ObjectSerializer(message, null);
        NetworkTask.PayloadProvider payloadProvider = new JsonApiPayloadProvider(serializer);

        networkTask.setPayloadProvider(payloadProvider);

        HttpResponse response = networkTask.run();

        if (response != null) {
            response.close();
        }

        return response != null && response.isSuccessful();
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (mCallback != null) {
            if (success) {
                mCallback.onSuccess();
            } else {
                mCallback.onFailure();
            }
        }
    }
}
