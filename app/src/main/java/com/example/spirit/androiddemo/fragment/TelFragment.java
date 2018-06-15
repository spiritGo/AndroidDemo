package com.example.spirit.androiddemo.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.interfaces.OnItemClickListener;
import com.example.spirit.androiddemo.modle.PersonBean;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class TelFragment extends Fragment {

    private RecyclerView rvTel;
    private ArrayList<PersonBean> phoneContacts;
    private LinearLayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_tel);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initVariable() {
        layoutManager = new LinearLayoutManager(getContext());
    }

    private void initUI() {
        rvTel.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ThreadUtil<PersonBean> threadUtil = new ThreadUtil<PersonBean>() {
            @Override
            protected ArrayList<PersonBean> getList() {
                return DataUtil.getPhoneContacts();
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(getActivity());
            }

            @Override
            protected void updateFinished(ArrayList<PersonBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getContext(), getString(R.string.noTel));
                    return;
                }

                phoneContacts = list;
                MyAdapter myAdapter = new MyAdapter(list);
                rvTel.setAdapter(myAdapter);
                Dialog.dismiss();

                myAdapter.setListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        final PersonBean personBean = phoneContacts.get(position);
                        builder.setPositiveButton("拨打", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + personBean.getNumber()));
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });

                        builder.setNegativeButton("取消", null);
                        builder.setTitle("拨打电话").setIcon(R.mipmap.tel).setMessage("是否要拨打此电话！")
                                .show();

                    }
                });
            }
        };
        threadUtil.newStartThread();
    }

    private void initView(View view) {
        rvTel = view.findViewById(R.id.rv_tel);
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private OnItemClickListener listener;
        private ArrayList<PersonBean> phoneContacts;

        MyAdapter(ArrayList<PersonBean> phoneContacts) {
            this.phoneContacts = phoneContacts;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .constact_item_layout, parent, false);//解决宽度不能铺满
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
            PersonBean personBean = phoneContacts.get(position);
            holder.tvName.setText(String.format("姓名：%s", personBean.getName()));
            holder.tvNumber.setText(String.format("电话：%s", personBean.getNumber()));
            holder.tvEmail.setText(String.format("Email：%s", personBean.getEmail()));
            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public int getItemCount() {
            return phoneContacts.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvName;
            private TextView tvNumber;
            private TextView tvEmail;
            private LinearLayout llContainer;

            MyViewHolder(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_name);
                tvNumber = itemView.findViewById(R.id.tv_number);
                tvEmail = itemView.findViewById(R.id.tv_email);
                llContainer = itemView.findViewById(R.id.ll_container);
            }
        }
    }
}
