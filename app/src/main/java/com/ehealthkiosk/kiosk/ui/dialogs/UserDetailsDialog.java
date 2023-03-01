package com.ehealthkiosk.kiosk.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.ehealthkiosk.kiosk.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UserDetailsDialog extends DialogFragment {

    public static final String TAG = "UserDetailsDialog";
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.btn_male)
    Button btnMale;
    @BindView(R.id.btn_female)
    Button btnFemale;
    @BindView(R.id.btn_other)
    Button btnOther;
    @BindView(R.id.btn_start_)
    Button btnStart;
    Unbinder unbinder;

    public static UserDetailsDialog display(FragmentManager fragmentManager) {
        UserDetailsDialog userDetailsDialog = new UserDetailsDialog();
        userDetailsDialog.show(fragmentManager, TAG);
        return userDetailsDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            int width = ViewGroup.LayoutParams.MATCH_PARENT;
//            int height = ViewGroup.LayoutParams.MATCH_PARENT;
//            dialog.getWindow().setLayout(width, height);
//            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
//        }
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_userdetails, container, false);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_male, R.id.btn_female, R.id.btn_other, R.id.btn_start_})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_male:
                break;
            case R.id.btn_female:
                break;
            case R.id.btn_other:
                break;
            case R.id.btn_start_:
                break;
        }
    }
}
