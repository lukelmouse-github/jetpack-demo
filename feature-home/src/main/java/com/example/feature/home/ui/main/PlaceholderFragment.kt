package com.example.feature.home.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.feature.home.databinding.FragmentPlaceholderBinding

/**
 * 占位Fragment - 临时用于显示各个Tab内容
 */
class PlaceholderFragment : Fragment() {

    private var _binding: FragmentPlaceholderBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TAB_NAME = "tab_name"

        fun newInstance(tabName: String): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TAB_NAME, tabName)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaceholderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabName = arguments?.getString(ARG_TAB_NAME) ?: "未知"
        binding.tvTitle.text = "${tabName}页面"
        binding.tvContent.text = "这里是${tabName}的内容区域\n\n后续会替换为真实的Fragment"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

