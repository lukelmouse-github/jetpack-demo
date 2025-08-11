package com.example.core.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.core.webview.databinding.ActivityWebviewBinding
import com.therouter.router.Route

/**
 * WebView页面
 * 支持TheRouter路由跳转
 */
@Route(path = "/webview", description = "WebView页面")
class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private var webTitle: String = ""
    private var webUrl: String = ""

    companion object {
        const val EXTRA_TITLE = "title"
        const val EXTRA_URL = "url"

        /**
         * 启动WebView页面
         */
        fun start(context: Context, title: String, url: String) {
            val intent = Intent(context, WebViewActivity::class.java).apply {
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_URL, url)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化DataBinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview)

        // 获取传递的参数
        webTitle = intent.getStringExtra(EXTRA_TITLE) ?: ""
        webUrl = intent.getStringExtra(EXTRA_URL) ?: ""

        // 设置标题
        binding.title = webTitle

        initView()
        initWebView()

        // 加载URL
        if (webUrl.isNotEmpty()) {
            binding.webview.loadUrl(webUrl)
        }
    }

    private fun initView() {
        // 返回按钮点击事件
        binding.ivBack.setOnClickListener {
            onBackClick()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        val webSettings = binding.webview.settings.apply {
            // 启用JavaScript
            javaScriptEnabled = true
            // 启用DOM存储
            domStorageEnabled = true
            // 允许文件访问
            allowFileAccess = true
            // 允许内容访问
            allowContentAccess = true
            // 设置缓存模式
            cacheMode = WebSettings.LOAD_DEFAULT
            // 支持缩放
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            // 自适应屏幕
            useWideViewPort = true
            loadWithOverviewMode = true
            // 支持多窗口
            setSupportMultipleWindows(true)
            // 允许运行插件
            pluginState = WebSettings.PluginState.ON
        }

        // 设置WebViewClient
        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.url?.toString()?.let { url ->
                    if (url.startsWith("http://") || url.startsWith("https://")) {
                        view?.loadUrl(url)
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // 显示进度条
                binding.progressBar.visibility = View.VISIBLE
                binding.progressBar.progress = 0
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 隐藏进度条
                binding.progressBar.visibility = View.GONE

                // 更新标题
                view?.title?.let { title ->
                    if (title.isNotEmpty() && webTitle.isEmpty()) {
                        binding.title = title
                    }
                }
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                // 隐藏进度条
                binding.progressBar.visibility = View.GONE
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                // 处理SSL错误，这里选择继续加载（生产环境需要更严格的处理）
                handler?.proceed()
            }
        }

        // 设置WebChromeClient
        binding.webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                // 更新进度条
                binding.progressBar.progress = newProgress
                if (newProgress >= 100) {
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                // 更新标题
                title?.let {
                    if (it.isNotEmpty() && webTitle.isEmpty()) {
                        binding.title = it
                    }
                }
            }
        }
    }

    private fun onBackClick() {
        if (binding.webview.canGoBack()) {
            binding.webview.goBack()
        } else {
            finish()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webview.canGoBack()) {
            binding.webview.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理WebView
        binding.webview.apply {
            loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            clearHistory()
            destroy()
        }
    }
}

