package in.ashprog.assignmentwriter;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;

class CreditDialog {
    void showDialog(Activity activity) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.credits_dialog);

        dialog.findViewById(R.id.closeIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
