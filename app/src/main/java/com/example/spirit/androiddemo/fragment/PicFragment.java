package com.example.spirit.androiddemo.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.spirit.androiddemo.ImageShowActivity;
import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.PicBean;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.DataUtil;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class PicFragment extends Fragment {
    final private String PIC = "图片";
    private RecyclerView rvPic;
    private GridLayoutManager gridLayoutManager;
    private int width;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_pic);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initView(View view) {
        rvPic = view.findViewById(R.id.rv_pic);
    }

    private void initVariable() {
        gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL,
                false);
        if (getActivity() != null) {
            width = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        }

    }

    private void initUI() {
        rvPic.setLayoutManager(gridLayoutManager);
        ThreadUtil<PicBean> threadUtil = new ThreadUtil<PicBean>() {
            @Override
            protected ArrayList<PicBean> getList() {
                return DataUtil.getPicBeans(width / 3);
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(getActivity());
            }

            @Override
            protected void updateFinished(ArrayList<PicBean> list) {

                if (list.size() <= 0) {
                    Dialog.dismiss();
                    Util.createDialog(getActivity(), getString(R.string.noPic));
                    return;
                }

                MyAdapter myAdapter = new MyAdapter(list);
                rvPic.setAdapter(myAdapter);
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
        private ArrayList<PicBean> picBeans;

        MyAdapter(ArrayList<PicBean> picBeans) {
            this.picBeans = picBeans;
        }

        @NonNull
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .pic_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.MyViewHolder holder, final int position) {
            ViewGroup.LayoutParams layoutParams = holder.ivImage.getLayoutParams();
            layoutParams.width = width / 3;
            layoutParams.height = width / 3;
            holder.ivImage.setLayoutParams(layoutParams);
            holder.ivImage.setBackgroundColor(Color.GRAY);
            //holder.ivImage.setImageBitmap(Util.getBitMap(picBeans.get(position).getPath()));
            holder.ivImage.setImageBitmap(picBeans.get(position).getBitmap());
            holder.ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ImageShowActivity.class);
                    intent.putExtra(ConstanceField.TOP_TITLE, PIC);
                    intent.putExtra(ConstanceField.PATH, picBeans.get(position).getPath());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return picBeans.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView ivImage;

            MyViewHolder(View itemView) {
                super(itemView);
                ivImage = itemView.findViewById(R.id.iv_image);
            }
        }
    }
}
