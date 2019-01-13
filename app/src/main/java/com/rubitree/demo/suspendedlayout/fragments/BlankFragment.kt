package com.rubitree.demo.suspendedlayout.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.rubitree.demo.R

/**
 * >> Description <<
 *
 * >> Attention <<
 *
 * >> Others <<
 *
 * Created by RubiTree ; On 2019-01-13.
 */
private const val ARG_PARAM1 = "param1"

class BlankFragment : Fragment() {
    private var color: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { color = it.getInt(ARG_PARAM1) }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_blank, container, false)
        inflate.findViewById<View>(R.id.vContainer).setBackgroundColor(color!!)
        return inflate
    }

    companion object {
        fun newInstance(color: Int) = BlankFragment().apply { arguments = Bundle().apply { putInt(ARG_PARAM1, color) } }
    }
}