package com.rubitree.demo.suspendedlayout

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter

import com.rubitree.demo.R
import com.rubitree.demo.suspendedlayout.fragments.BlankFragment
import kotlinx.android.synthetic.main.activity_suspended_layout.*
import java.util.ArrayList

class SuspendedLayoutActivity : AppCompatActivity() {

    private val fragments = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_suspended_layout)

        initFragments()
        initScrollableLayout()
    }

    private fun initFragments() {
        fragments.add(BlankFragment.newInstance(0xfffdedbc.toInt()))
        fragments.add(BlankFragment.newInstance(0xFFB1F1F5.toInt()))
        fragments.add(BlankFragment.newInstance(0x30303333))
    }

    private fun initScrollableLayout() {
        vViewpager.offscreenPageLimit = 2
        vViewpager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(p0: Int) = fragments[p0]
            override fun getCount() = fragments.size
        }
    }
}
