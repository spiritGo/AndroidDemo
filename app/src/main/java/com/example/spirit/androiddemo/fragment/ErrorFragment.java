package com.example.spirit.androiddemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.Util;

import java.io.File;

public class ErrorFragment extends Fragment {
    private TextView tvError;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_error);
        initView(view);
        initUI();
        return view;
    }

    private void initView(View view) {
        tvError = view.findViewById(R.id.tv_error);
    }

    private void initUI() {
        if (getActivity() != null) {
            Dialog.createDialog(getActivity());
            new Thread() {
                @Override
                public void run() {
                    final String info = Util.readFile(new File(getActivity().getCacheDir(),
                            "error"));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvError.setText(info);
                            Dialog.dismiss();
                        }
                    });
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dialog.dismiss();
    }
}
