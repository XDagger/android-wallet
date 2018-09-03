package io.xdag.common.tool;

import android.app.Activity;
import java.util.LinkedList;
import java.util.List;

/**
 * created by lxm on 2018/7/30.
 */
public class ActivityStack {

    private static final ActivityStack sInstance = new ActivityStack();
    private final List<Activity> mActivities = new LinkedList<>();


    private ActivityStack() {
    }


    public static ActivityStack getInstance() {
        return sInstance;
    }


    public synchronized void addActivity(Activity activity) {
        mActivities.add(activity);
    }


    public synchronized void removeActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            mActivities.remove(activity);
        }
    }


    public synchronized void exit() {
        for (int i = mActivities.size() - 1; i > -1; i--) {
            Activity activity = mActivities.get(i);
            removeActivity(activity);
            activity.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
