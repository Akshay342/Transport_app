package com.fujitsu.transport.transportapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseFragment;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.util.CommonUtils;
import com.fujitsu.transport.transportapp.util.Constants;
import com.fujitsu.transport.transportapp.util.NetworkUtils;
import com.fujitsu.transport.transportapp.viewmodel.UserHomeViewModel;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserHomeFragment extends BaseFragment<UserHomeViewModel> {
    @BindView(R.id.tv_route)
    TextView tvRoute;
    @BindView(R.id.tv_no_routes)
    TextView tv_no_routes;
    @BindView(R.id.rv_user)
    RecyclerView rvUser;
    @BindView(R.id.swiperefresh)
    SwipeRefreshLayout swiperefresh;
    Unbinder unbinder;


    private final String TAG = this.getClass().getSimpleName();
    @BindView(R.id.progress_circular)
    ProgressBar progressCircular;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_user_home;
    }

    @Override
    protected Class<UserHomeViewModel> viewModelType() {
        return UserHomeViewModel.class;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        progressCircular.setVisibility(View.VISIBLE);

        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tv_no_routes.setVisibility(View.GONE);
                getActiveRoutes();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActiveRoutes();
    }

    private void getActiveRoutes() {
        if (NetworkUtils.isNetworkAvailable(getContext())) {
            viewModel.getActiveRoutes().observe(this, mutableLiveData -> {
                if (mutableLiveData != null && mutableLiveData.getBody() != null) {
                        tv_no_routes.setVisibility(View.GONE);
                        rvUser.setLayoutManager(new LinearLayoutManager(getContext()));
                        rvUser.setItemAnimator(new DefaultItemAnimator());
                        rvUser.setAdapter(new RoutesRecyclerAdapter(mutableLiveData.getBody()));
                    if(mutableLiveData.getBody().size()==0){
                        tv_no_routes.setVisibility(View.VISIBLE);
                    }
                } else {
                    CommonUtils.showToast(getContext(), getContext().getString(R.string.something_wrong));
                }
                swiperefresh.setRefreshing(false);
                progressCircular.setVisibility(View.GONE);
            });
        }
        else
            swiperefresh.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public class RoutesRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<Rout> routList;
        RouteViewHolder routeViewHolder;

        public RoutesRecyclerAdapter(List<Rout> routList) {
            this.routList = routList;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.rv_routes, viewGroup, false);
            return new RouteViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return routList.size();
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            routeViewHolder = (RouteViewHolder) holder;

            routeViewHolder.tv_routes.setText(routList.get(position).getRoute_number());
        }

        class RouteViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_routes)
            TextView tv_routes;

            Unbinder unbinder;

            public RouteViewHolder(@NonNull View itemView) {
                super(itemView);
                unbinder = ButterKnife.bind(this, itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        navigateToUserMap(routList.get(getAdapterPosition()));
                    }
                });
            }

            private void navigateToUserMap(Rout rout) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("RoutesObject", rout);
                intent.putExtra("Role", Constants.USER_TYPE_USER);
                startActivity(intent);
            }
        }
    }
}
