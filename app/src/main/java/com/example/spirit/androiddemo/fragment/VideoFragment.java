package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.VideoShowActivity;
import com.example.spirit.androiddemo.modle.VideoBean;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private RecyclerView rvVideo;
    private LinearLayoutManager layoutManager;
    private FragmentActivity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_video);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initView(View view) {
        rvVideo = view.findViewById(R.id.rv_video);
    }

    private void initVariable() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        activity = getActivity();
    }

    private void initUI() {
        ThreadUtil<VideoBean> threadUtil = new ThreadUtil<VideoBean>() {
            @Override
            protected ArrayList<VideoBean> getList() {
                return DataUtil.getVideoBeans();
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(activity);
            }

            @Override
            protected void updateFinished(ArrayList<VideoBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getContext(), getString(R.string.noVideo));
                    return;
                }

                rvVideo.setLayoutManager(layoutManager);
                rvVideo.addItemDecoration(new DividerItemDecoration(activity,
                        DividerItemDecoration.VERTICAL));
                MyAdapter myAdapter = new MyAdapter(list);
                rvVideo.setAdapter(myAdapter);
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

        private ArrayList<VideoBean> videoBeans;

        MyAdapter(ArrayList<VideoBean> videoBeans) {
            this.videoBeans = videoBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .video_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, int position) {
            final VideoBean videoBean = videoBeans.get(position);
            holder.tvTitle.setText(videoBean.getName());
            holder.tvDuration.setText(Util.getTextDuration(Integer.parseInt(videoBean.getDuration
                    ())));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, VideoShowActivity.class);
                    intent.putExtra(ConstanceField.PATH, videoBean.getPath());
                    intent.putExtra(ConstanceField.TITLE, videoBean.getName());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return videoBeans.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle;
            TextView tvDuration;

            MyViewHolder(View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDuration = itemView.findViewById(R.id.tv_duration);
            }
        }
    }
}
