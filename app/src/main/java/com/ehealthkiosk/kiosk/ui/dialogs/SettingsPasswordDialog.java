package com.ehealthkiosk.kiosk.ui.dialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.activities.SettingsTabActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class SettingsPasswordDialog extends DialogFragment {

    public static final String TAG = "SettingsPasswordDialog";
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_enter)
    Button btnEnter;
    Unbinder unbinder;

    public static SettingsPasswordDialog display(FragmentManager fragmentManager) {
        SettingsPasswordDialog mSettingsPasswordDialog = new SettingsPasswordDialog();
        mSettingsPasswordDialog.show(fragmentManager, TAG);
        return mSettingsPasswordDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_settings_password, container, false);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btn_enter)
    public void onViewClicked() {

        this.dismiss();
        Intent i = new Intent(getActivity(), SettingsTabActivity.class);
        startActivity(i);
    }
}
