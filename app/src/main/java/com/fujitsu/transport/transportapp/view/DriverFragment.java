package com.fujitsu.transport.transportapp.view;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseFragment;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.model.RoutResponse;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.util.CommonUtils;
import com.fujitsu.transport.transportapp.util.Constants;
import com.fujitsu.transport.transportapp.util.NetworkUtils;
import com.fujitsu.transport.transportapp.viewmodel.DriverViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class DriverFragment extends BaseFragment<DriverViewModel> {

    @BindView(R.id.progress_circular)
    ProgressBar progressCircular;
    Unbinder unbinder;
    @BindView(R.id.spinner_routes)
    Spinner spinnerRoutes;
    @BindView(R.id.btn_select_route)
    Button btnSelectRoute;
    @BindView(R.id.tv_route)
    TextView tvRoute;


    @Inject
    Session session;

    private final String TAG = this.getClass().getSimpleName();
    private RoutResponse routResponse;

    @Override
    protected int layoutRes() {
        return R.layout.fragment_driver;
    }

    @Override
    protected Class<DriverViewModel> viewModelType() {
        return DriverViewModel.class;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        progressCircular.setVisibility(View.VISIBLE);
        showUi(false);
        if(NetworkUtils.isNetworkAvailable(getContext())) {
            viewModel.get_active_route_for_driver(session.getUserId()).observe(this, mutableLiveData -> {
                if (mutableLiveData != null && mutableLiveData.getBody() != null) {
                    if (mutableLiveData.getBody().getSUCCESS() && mutableLiveData.getBody().getRouts().size() > 0) {
                        //driver is already active in a trip, navigate to maps
                        CommonUtils.showToast(getContext(), "You are already in an active trip.");
                        navigateToDriverMaps(mutableLiveData.getBody().getRouts().get(0));
                    } else if (mutableLiveData.getBody().getSUCCESS()) {
                        //user is not in any active trip, show available routes to driver
                        getRoutes();
                    } else {
                        CommonUtils.showToast(getContext(), getContext().getString(R.string.something_wrong));
                    }
                } else {
                    CommonUtils.showToast(getContext(), getContext().getString(R.string.something_wrong));
                }

                progressCircular.setVisibility(View.GONE);
            });
        }
        else {
            progressCircular.setVisibility(View.GONE);
        }
    }

    private void showUi(boolean show){
        if(show){
            spinnerRoutes.setVisibility(View.VISIBLE);
            tvRoute.setVisibility(View.VISIBLE);
            btnSelectRoute.setVisibility(View.VISIBLE);
            btnSelectRoute.setClickable(true);
        }
        else {
            spinnerRoutes.setVisibility(View.GONE);
            tvRoute.setVisibility(View.GONE);
            btnSelectRoute.setVisibility(View.GONE);
            btnSelectRoute.setClickable(false);
        }
    }

    private void getRoutes(){
        if(NetworkUtils.isNetworkAvailable(getContext()))
            viewModel.getRoutes().observe(this, mutableLiveData ->{
                if(mutableLiveData!=null && mutableLiveData.getBody() != null){
                    routResponse = mutableLiveData.getBody();
                    List<String> routeNames = new ArrayList<>();
                    for(int i =0;i<mutableLiveData.getBody().getRouts().size();i++){
                        routeNames.add(mutableLiveData.getBody().getRouts().get(i).getRoute_number());
                    }
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>
                            (getContext(), android.R.layout.simple_spinner_item,routeNames);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout
                            .simple_spinner_dropdown_item);
                    spinnerRoutes.setAdapter(spinnerArrayAdapter);
                    showUi(true);
                }
                else {
                    CommonUtils.showToast(getContext(),getContext().getString(R.string.something_wrong));
                }

                progressCircular.setVisibility(View.GONE);
            });
    }

    private void navigateToDriverMaps(Rout rout){
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("RoutesObject",rout);
        intent.putExtra("Role", Constants.USER_TYPE_DRIVER);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_select_route})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select_route:
                if(routResponse!=null)
                    navigateToDriverMaps(routResponse.getRouts().get(spinnerRoutes.getSelectedItemPosition()));
                break;
        }
    }
}
