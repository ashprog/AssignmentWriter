package in.ashprog.assignmentwriter;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class App extends Application {

    static String SPName = "UserSavedValues";
    static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences(SPName, Context.MODE_PRIVATE);
    }
}
