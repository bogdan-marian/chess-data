package eu.chessdata.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class CloudService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    private static final String ACTION_DEVICE_TO_CLOUD = "eu.chessdata.services.ACTION_DEVICE_TO_CLOUD";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "eu.chessdata.services.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "eu.chessdata.services.extra.PARAM2";

    public CloudService() {
        super("CloudService");
    }


    // TODO: Customize helper method
    public static void startActionDeviceToCloud(Context context, String param1, String param2) {
        Intent intent = new Intent(context, CloudService.class);
        intent.setAction(ACTION_DEVICE_TO_CLOUD);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DEVICE_TO_CLOUD.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionFoo(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
