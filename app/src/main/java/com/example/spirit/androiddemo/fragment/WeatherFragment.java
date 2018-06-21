package com.example.spirit.androiddemo.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.spirit.androiddemo.MyApplication;
import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.modle.WeatherBean;
import com.example.spirit.androiddemo.utils.Dialog;
import com.example.spirit.androiddemo.utils.HttpUtil;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.ThreadUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.io.IOException;
import java.util.ArrayList;

public class WeatherFragment extends Fragment {
    private RecyclerView rvWeather;
    private LinearLayoutManager layoutManager;
    private FragmentActivity activity;
    private String city;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = Util.inflateView(R.layout.fragment_weather);
        MyApplication.setName(getClass().getSimpleName());
        initView(view);
        initVariable();
        initUI();
        return view;
    }

    private void initView(View view) {
        rvWeather = view.findViewById(R.id.rv_weather);
    }

    private void initVariable() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        activity = getActivity();
    }

    private void initUI() {
        updateData();
    }

    private void updateData() {
        city = SPUtil.getString(SPUtil.CITY, "北京");
        ThreadUtil<WeatherBean> threadUtil = new ThreadUtil<WeatherBean>() {

            private MyAdapter myAdapter;

            @Override
            protected ArrayList<WeatherBean> getList() {
                try {
                    return HttpUtil.getHttpUtil().getNetData(WeatherBean.class, city);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void updateStart() {
                Dialog.createDialog(activity);
            }

            @Override
            protected void updateFinished(ArrayList<WeatherBean> list) {

                if (list == null || list.size() <= 0 || list.get(0).getStatus() == 304) {
                    if (list != null && list.get(0).getStatus() == 304) {
                        Dialog.dismiss();
                        Util.createDialog(activity, "数据访问达上限，明天再来吧");
                        return;
                    }

                    if (SPUtil.getBoolean(SPUtil.FAILED, false)) {
                        Dialog.dismiss();
                        Util.toast("刷新数据失败！请重试");
                        SPUtil.putBoolean(SPUtil.FAILED, false);
                    }

                    Dialog.dismiss();
                    Util.createDialog(activity, "加载失败");
                    return;
                }

                WeatherBean weatherBean = list.get(0);
                if (myAdapter == null) {
                    myAdapter = new MyAdapter(list);
                    rvWeather.setLayoutManager(layoutManager);
                    rvWeather.addItemDecoration(new DividerItemDecoration(activity,
                            DividerItemDecoration.VERTICAL));

                    View view = LayoutInflater.from(activity).inflate(R.layout
                            .weather_header_layout, rvWeather, false);
                    TextView tvTime = view.findViewById(R.id.tv_time);
                    final EditText etCity = view.findViewById(R.id.et_city);
                    TextView tvType = view.findViewById(R.id.tv_type);
                    ImageView ivRefresh = view.findViewById(R.id.iv_refresh);
                    ivRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String s = etCity.getText().toString();
                            startAnimation(v);
                            if (!s.equals(city)) {
                                SPUtil.putString(SPUtil.CITY, s);
                                updateData();
                            }
                        }
                    });
                    etCity.setText(weatherBean.getCity());
                    tvTime.setText(weatherBean.getDate());
                    tvType.setText(weatherBean.getData().getForecast().get(0).getType());
                    myAdapter.setmViewHeader(view);
                    rvWeather.setAdapter(myAdapter);
                    Dialog.dismiss();
                } else {
                    myAdapter.setWeatherBeans(list);
                    myAdapter.notifyDataSetChanged();
                    Dialog.dismiss();
                }
            }
        };
        threadUtil.newStartThread();
    }

    private void startAnimation(final View view) {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.2f, 1f, 1.2f, Animation
                .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(350);
        scaleAnimation.setFillAfter(true);
        view.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.2f, 1f, 1.2f, 1f, Animation
                        .RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(350);
                scaleAnimation.setFillAfter(true);
                view.startAnimation(scaleAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Dialog.dismiss();
    }

    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

        final private int VIEW_HEADER = 0;
        final private int VIEW_NORMAL = 1;
        private ArrayList<WeatherBean> weatherBeans;
        private View mViewHeader;

        MyAdapter(ArrayList<WeatherBean> weatherBeans) {
            this.weatherBeans = weatherBeans;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (mViewHeader != null && viewType == VIEW_HEADER)
                return new MyViewHolder(mViewHeader);

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout
                    .weather_item_layout, parent, false);
            return new MyViewHolder(view);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (getItemViewType(position) == VIEW_HEADER) return;

            WeatherBean.DataBean.ForecastBean forecastBean = weatherBeans.get(0).getData()
                    .getForecast().get(position - 1);
            holder.tvAqi.setText(String.format("%s", forecastBean.getAqi()));
            holder.tvDate.setText(String.format("%s", forecastBean.getDate()));
            holder.tvFl.setText(String.format("%s", forecastBean.getFl()));
            holder.tvFx.setText(String.format("%s", forecastBean.getFx()));
            holder.tvHigh.setText(String.format("%s", forecastBean.getHigh()));
            holder.tvLow.setText(String.format("%s", forecastBean.getLow()));
            holder.tvNotice.setText(String.format("%s", forecastBean.getNotice()));
            holder.tvSunrise.setText(String.format("日出：%s", forecastBean.getSunrise()));
            holder.tvSunset.setText(String.format("日落：%s", forecastBean.getSunset()));
            holder.tvType.setText(String.format("%s", forecastBean.getType()));
            holder.tvNotice.setText(String.format("建议：%s", forecastBean.getNotice()));
            holder.ivTemper.setColorFilter(Color.WHITE);

            holder.llContainer.measure(0, 0);
            Bitmap bitmap = Util.compressBySize(R.mipmap.weather_item_bg, holder.llContainer
                    .getMeasuredWidth(), holder.llContainer.getMeasuredHeight());
            holder.llContainer.setBackgroundDrawable(new BitmapDrawable(bitmap));
        }

        public void setWeatherBeans(ArrayList<WeatherBean> weatherBeans) {
            this.weatherBeans = weatherBeans;
        }

        @Override
        public int getItemCount() {
            return weatherBeans.get(0).getData().getForecast().size() + 1;
        }

        @Override
        public int getItemViewType(int position) {
            if (mViewHeader == null) return VIEW_NORMAL;
            if (position == 0) return VIEW_HEADER;
            return VIEW_NORMAL;
        }

        private Drawable getDrawble(int id) {
            int size = Util.sp2px(activity, 18);
            return Util.getScaleDrawble(id, size, size);
        }

        public void setmViewHeader(View mViewHeader) {
            this.mViewHeader = mViewHeader;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView tvDate;
            private TextView tvSunrise;
            private TextView tvSunset;
            private TextView tvLow;
            private TextView tvHigh;
            private TextView tvType;
            private TextView tvFx;
            private TextView tvFl;
            private TextView tvAqi;
            private TextView tvNotice;
            private LinearLayout llContainer;
            private ImageView ivTemper;

            MyViewHolder(View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvSunrise = itemView.findViewById(R.id.tv_sunrise);
                tvSunset = itemView.findViewById(R.id.tv_sunset);
                tvLow = itemView.findViewById(R.id.tv_low);
                tvHigh = itemView.findViewById(R.id.tv_high);
                tvType = itemView.findViewById(R.id.tv_type);
                tvFx = itemView.findViewById(R.id.tv_fx);
                tvFl = itemView.findViewById(R.id.tv_fl);
                tvAqi = itemView.findViewById(R.id.tv_aqi);
                tvNotice = itemView.findViewById(R.id.tv_notice);
                llContainer = itemView.findViewById(R.id.ll_container);
                ivTemper = itemView.findViewById(R.id.iv_temper);
            }
        }
    }
}
