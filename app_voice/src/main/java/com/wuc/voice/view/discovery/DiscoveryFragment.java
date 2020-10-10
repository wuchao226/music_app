package com.wuc.voice.view.discovery;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.wuc.lib_common_ui.recyclerview.CommonAdapter;
import com.wuc.lib_common_ui.recyclerview.base.ViewHolder;
import com.wuc.lib_common_ui.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.wuc.lib_common_ui.recyclerview.wrapper.LoadMoreWrapper;
import com.wuc.lib_image_loader.ImageLoaderManager;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;
import com.wuc.lib_network.okhttp.utils.ResponseEntityToModule;
import com.wuc.voice.R;

import androidx.fragment.app.Fragment;
import com.wuc.voice.api.MockData;
import com.wuc.voice.api.RequestCenter;
import com.wuc.voice.model.discory.BaseRecommandModel;
import com.wuc.voice.model.discory.BaseRecommandMoreModel;
import com.wuc.voice.model.discory.RecommandBodyValue;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wuchao
 * @date: 2019-09-17 15:14
 * @desciption: 首页发现Fragment
 */
public class DiscoveryFragment extends Fragment
    implements SwipeRefreshLayout.OnRefreshListener, LoadMoreWrapper.OnLoadMoreListener {

  public static DiscoveryFragment newInstance() {
    DiscoveryFragment fragment = new DiscoveryFragment();
    return fragment;
  }

  private Context mContext;
  /*
   *  UI
   */
  private SwipeRefreshLayout mSwipeRefreshLayout;
  private RecyclerView mRecyclerView;
  private CommonAdapter mAdapter;
  private HeaderAndFooterWrapper mHeaderWrapper;
  private LoadMoreWrapper mLoadMoreWrapper;
  /*
   *
   */
  private BaseRecommandModel mRecommandData;
  private List<RecommandBodyValue> mDatas = new ArrayList<>();

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_discovery_layout, null);
    mSwipeRefreshLayout = rootView.findViewById(R.id.refresh_layout);
    mSwipeRefreshLayout.setColorSchemeColors(
        getResources().getColor(android.R.color.holo_red_light));
    mSwipeRefreshLayout.setOnRefreshListener(this);

    mRecyclerView = rootView.findViewById(R.id.recyclerview);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    //发请求更新UI
    requestData();
  }

  //下拉刷新接口
  @Override
  public void onRefresh() {
    requestData();
  }

  //加载更多接口
  @Override
  public void onLoadMoreRequested() {
    loadMore();
  }

  private void loadMore() {
    RequestCenter.requestRecommandMore(new DisposeDataListener() {
      @Override
      public void onSuccess(Object responseObj) {
        BaseRecommandMoreModel moreData = (BaseRecommandMoreModel) responseObj;
        //追加数据到adapter中
        mDatas.addAll(moreData.data.list);
        mLoadMoreWrapper.notifyDataSetChanged();
      }

      @Override
      public void onFailure(Object reasonObj) {
        //显示请求失败View,显示mock数据
        onSuccess(ResponseEntityToModule.parseJsonToModule(MockData.HOME_MORE_DATA,
            BaseRecommandMoreModel.class));
      }
    });
  }

  //请求数据
  private void requestData() {
    RequestCenter.requestRecommandData(new DisposeDataListener() {
      @Override
      public void onSuccess(Object responseObj) {
        mRecommandData = (BaseRecommandModel) responseObj;
        //更新UI
        updateView();
      }

      @Override
      public void onFailure(Object reasonObj) {
        //显示请求失败View,显示mock数据
        onSuccess(
            ResponseEntityToModule.parseJsonToModule(MockData.HOME_DATA, BaseRecommandModel.class));
      }
    });
  }

  //更新UI
  private void updateView() {
    mSwipeRefreshLayout.setRefreshing(false); //停止刷新
    mDatas = mRecommandData.data.list;
    mAdapter = new CommonAdapter<RecommandBodyValue>(mContext, R.layout.item_discory_list_picture_layout, mDatas) {
      @Override
      protected void convert(ViewHolder holder, RecommandBodyValue recommandBodyValue, int position) {
        TextView titleView = holder.getView(R.id.title_view);
        if (TextUtils.isEmpty(recommandBodyValue.title)) {
          titleView.setVisibility(View.GONE);
        } else {
          titleView.setVisibility(View.VISIBLE);
          titleView.setText(recommandBodyValue.title);
        }
        holder.setText(R.id.name_view, recommandBodyValue.text);
        holder.setText(R.id.play_view, recommandBodyValue.play);
        holder.setText(R.id.time_view, recommandBodyValue.time);
        holder.setText(R.id.zan_view, recommandBodyValue.zan);
        holder.setText(R.id.message_view, recommandBodyValue.msg);
        ImageView logo = holder.getView(R.id.logo_view);
        ImageLoaderManager.getInstance().displayImageForView(logo, recommandBodyValue.logo);
        ImageView avatar = holder.getView(R.id.author_view);
        ImageLoaderManager.getInstance().displayImageForCircle(avatar, recommandBodyValue.avatr);
      }
    };
    //头部view初始化
    mHeaderWrapper = new HeaderAndFooterWrapper(mAdapter);
    DiscoveryBannerView bannerView = new DiscoveryBannerView(mContext, mRecommandData.data.head);
    mHeaderWrapper.addHeaderView(bannerView);
    DiscoveryFunctionView functionView = new DiscoveryFunctionView(mContext);
    mHeaderWrapper.addHeaderView(functionView);
    DiscoveryRecommandView recommandView =
        new DiscoveryRecommandView(mContext, mRecommandData.data.head);
    mHeaderWrapper.addHeaderView(recommandView);
    DiscoveryNewView newView = new DiscoveryNewView(mContext, mRecommandData.data.head);
    mHeaderWrapper.addHeaderView(newView);
    //加载更多初始化
    mLoadMoreWrapper = new LoadMoreWrapper(mHeaderWrapper);
    mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
    mLoadMoreWrapper.setOnLoadMoreListener(this);
    mRecyclerView.setAdapter(mLoadMoreWrapper);
  }
}
