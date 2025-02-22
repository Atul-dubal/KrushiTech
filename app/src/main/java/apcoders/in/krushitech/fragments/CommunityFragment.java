package apcoders.in.krushitech.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import apcoders.in.krushitech.R;

public class CommunityFragment extends Fragment {
    private static final String ARG_URL = "url";
    private String url;
    private WebView webView;

    public CommunityFragment() {
        // Required empty public constructor
    }

    // Use this method to create a new instance with a URL
    public static CommunityFragment newInstance(String url) {
        CommunityFragment fragment = new CommunityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_community, container, false);
        webView = view.findViewById(R.id.webview);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Open links inside WebView instead of a browser
        webView.setWebViewClient(new WebViewClient());

        // Load URL
        if (url != null && !url.isEmpty()) {
            webView.loadUrl(url);

    } else {
        Log.e("CommunityFragment", "WebView is null. Check fragment_community.xml.");
    }

        return view;
    }
}
