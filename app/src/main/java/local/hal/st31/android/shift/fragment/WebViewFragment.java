package local.hal.st31.android.shift.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import local.hal.st31.android.shift.NewSelfShiftAddActivity;
import local.hal.st31.android.shift.R;
import local.hal.st31.android.shift.beans.SelfScheduleBean;
import local.hal.st31.android.shift.db.DataAccess;
import local.hal.st31.android.shift.utils.DateUtils;

public class WebViewFragment extends Fragment {
    private View fragmentView;
    private WebView webView;
    private static final String URL = "http://flexibleshift.sakura.ne.jp/shift_app_backend/management/shift_release.php";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_web_view,container,false);
        RefreshLayout refreshLayout = fragmentView.findViewById(R.id.refreshLayout);
        webView = fragmentView.findViewById(R.id.web_view);
        reloadData();
        //设置 Header 为 贝塞尔雷达 样式
        refreshLayout.setRefreshHeader(new BezierRadarHeader(getContext()).setEnableHorizontalDrag(true));
//        ClassicsHeader header = fragmentView.findViewById(R.id.header);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                reloadData();
                refreshLayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void reloadData(){
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(URL);
    }

    @Override
    public void onPause() {
        super.onPause();

    }


}
