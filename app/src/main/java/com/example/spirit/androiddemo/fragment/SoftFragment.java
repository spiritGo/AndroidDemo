package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.SoftBean;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class SoftFragment extends Fragment {
    private RecyclerView rvSoft;
    private LinearLayoutManager layoutManager;
    private FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_soft);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initView(View view) {
        rvSoft = view.findViewById(R.id.rv_soft);
    }

    private void initVariable() {
        layoutManager = new LinearLayoutManager(getContext());
        activity = getActivity();
    }

    private void initUI() {
        rvSoft.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvSoft.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration
                .VERTICAL));
        ThreadUtil<SoftBean> threadUtil = new ThreadUtil<SoftBean>() {
            @Override
            protected ArrayList<SoftBean> getList() {
                return DataUtil.getSoftBeans();
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(activity);
            }

            @Override
            protected void updateFinished(ArrayList<SoftBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getContext(), getString(R.string.noSoft));
                    return;
                }

                MyAdapter myAdapter = new MyAdapter(list);
                rvSoft.setAdapter(myAdapter);
                Dialog.dismiss();
            }
        };
        threadUtil.newStartThread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dialog.dismiss();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        private ArrayList<SoftBean> softBeans;

        MyAdapter(ArrayList<SoftBean> softBeans) {
            this.softBeans = softBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .soft_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            final SoftBean softBean = softBeans.get(position);
            holder.tvName.setText(softBean.getName());
            holder.ivIcon.setImageDrawable(softBean.getIcon());
            holder.ivDetail.setColorFilter(R.color.gray);
            holder.llContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", softBean.getPackageName(), null));
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return softBeans.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            ImageView ivDetail;
            LinearLayout llContainer;

            MyViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvName = itemView.findViewById(R.id.tv_name);
                ivDetail = itemView.findViewById(R.id.iv_detail);
                llContainer = itemView.findViewById(R.id.ll_container);
            }
        }
    }
}
