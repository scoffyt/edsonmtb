package com.scoffyt.trilhadoedson;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import java.util.HashMap;
import java.util.Map;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * O app abre o jogo publicado no GitHub Pages, então cada atualização do
 * repositório chega sozinha. Sem internet, abre a cópia que vem dentro do APK.
 */
public class MainActivity extends Activity {
    private static final String SITE = "https://scoffyt.github.io/edsonmtb/";
    private static final String COPIA = "file:///android_asset/index.html";
    private WebView web;
    private boolean usandoCopia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        web = new WebView(this);
        web.setBackgroundColor(Color.BLACK);
        setContentView(web);
        telaCheia();

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setAllowFileAccess(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setUserAgentString(s.getUserAgentString() + " TrilhaDoEdsonApp");

        web.setWebViewClient(new WebViewClient() {
            // three.js vem de dentro do APK: abre mais rápido e funciona sem internet
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest req) {
                String u = req.getUrl().toString(), f = null;
                if (u.endsWith("three.min.js")) f = "three.min.js";
                else if (u.endsWith("GLTFLoader.js")) f = "GLTFLoader.js";
                else if (u.endsWith("SkeletonUtils.js")) f = "SkeletonUtils.js";
                if (f == null) return null;
                try {
                    WebResourceResponse r = new WebResourceResponse("text/javascript", "utf-8", getAssets().open("js/" + f));
                    Map<String, String> h = new HashMap<>();
                    h.put("Access-Control-Allow-Origin", "*");
                    r.setResponseHeaders(h);
                    return r;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public void onReceivedError(WebView v, WebResourceRequest req, WebResourceError err) {
                // sem internet na página principal: usa a cópia embutida
                if (req.isForMainFrame() && !usandoCopia) {
                    usandoCopia = true;
                    v.loadUrl(COPIA);
                }
            }
        });

        if (savedInstanceState != null) web.restoreState(savedInstanceState);
        else web.loadUrl(SITE);
    }

    private void telaCheia() {
        View d = getWindow().getDecorView();
        d.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onWindowFocusChanged(boolean focus) {
        super.onWindowFocusChanged(focus);
        if (focus) telaCheia();
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    @Override
    protected void onPause() { super.onPause(); web.onPause(); }

    @Override
    protected void onResume() { super.onResume(); web.onResume(); telaCheia(); }

    @Override
    public void onBackPressed() {
        // o jogo tem o próprio menu de pausa: o voltar do Android pausa em vez de fechar
        web.evaluateJavascript("(function(){var e=new KeyboardEvent('keydown',{code:'Escape',key:'Escape',bubbles:true});window.dispatchEvent(e);return 1;})()", null);
    }
}
