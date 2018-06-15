package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.TextShowActivity;
import com.example.spirit.androiddemo.interfaces.OnItemClickListener;
import com.example.spirit.androiddemo.modle.SmsBean;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class SmsFragment extends Fragment {
    final private String SMS = "短信";
    private RecyclerView rvSms;
    private ArrayList<SmsBean> smsBeans;
    private MyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_sms);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initUI();
        return view;
    }

    private void initUI() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvSms.setLayoutManager(layoutManager);
        rvSms.setItemAnimator(new DefaultItemAnimator());

        ThreadUtil<SmsBean> threadUtil = new ThreadUtil<SmsBean>() {
            @Override
            protected ArrayList<SmsBean> getList() {
                return DataUtil.getSmsBeans();
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(getActivity());
            }

            @Override
            protected void updateFinished(ArrayList<SmsBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getContext(), getString(R.string.noSms));
                    return;
                }

                MyAdapter myAdapter = new MyAdapter(list);
                setAdapter(myAdapter);
                rvSms.setAdapter(myAdapter);
                Dialog.dismiss();
                smsBeans = list;

                myAdapter.setListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(getContext(), TextShowActivity.class);
                        intent.putExtra(ConstanceField.SMS_BODY, smsBeans.get(position).getBody());
                        intent.putExtra(ConstanceField.ADDRESS, smsBeans.get(position).getAddress
                                ());
                        intent.putExtra(ConstanceField.TOP_TITLE, SMS);
                        startActivity(intent);
                    }
                });
            }
        };
        threadUtil.newStartThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dialog.dismiss();
    }

    private void initView(View view) {
        rvSms = view.findViewById(R.id.rv_sms);
    }

    public void updateSms() {
        smsBeans = DataUtil.addSmsBean();
        adapter.notifyItemChanged(smsBeans.size() - 1);
    }

    public void setAdapter(MyAdapter adapter) {
        this.adapter = adapter;
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private OnItemClickListener listener;
        private ArrayList<SmsBean> smsBeans;

        MyAdapter(ArrayList<SmsBean> smsBeans) {
            this.smsBeans = smsBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .sms_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
            SmsBean smsBean = smsBeans.get(position);
            holder.tvAddress.setText(smsBean.getAddress());
            holder.tvDate.setText(smsBean.getDate());
            holder.tvBody.setText(String.format("%s", smsBean.getBody()));
            holder.cvContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return smsBeans.size();
        }

        void setListener(OnItemClickListener listener) {
            this.listener = listener;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvAddress;
            private TextView tvDate;
            private TextView tvBody;
            private CardView cvContainer;

            MyViewHolder(View itemView) {
                super(itemView);
                cvContainer = itemView.findViewById(R.id.cv_container);
                tvAddress = itemView.findViewById(R.id.tv_address);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvBody = itemView.findViewById(R.id.tv_body);
            }
        }
    }
}
