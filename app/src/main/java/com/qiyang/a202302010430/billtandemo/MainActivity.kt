package com.qiyang.a202302010430.billtandemo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.qiyang.a202302010430.billtandemo.ui.HomeFragment
import com.qiyang.a202302010430.billtandemo.ui.AddBillFragment
import com.qiyang.a202302010430.billtandemo.ui.ReportFragment
import com.qiyang.a202302010430.billtandemo.ui.BudgetFragment
import com.qiyang.a202302010430.billtandemo.ui.MeFragment

class MainActivity : AppCompatActivity() {
    
    private lateinit var bottomNavigationView: BottomNavigationView
    private var currentFragment: Fragment? = null
    
    // Fragment实例
    private val homeFragment = HomeFragment()
    private val addBillFragment = AddBillFragment()
    private val reportFragment = ReportFragment()
    private val budgetFragment = BudgetFragment()
    private val meFragment = MeFragment()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        // 初始化BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        
        // 设置默认显示的Fragment
        showFragment(homeFragment)
        
        // 设置底部导航栏的选择监听器
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    showFragment(homeFragment)
                    true
                }
                R.id.nav_add -> {
                    showFragment(addBillFragment)
                    true
                }
                R.id.nav_report -> {
                    showFragment(reportFragment)
                    true
                }
                R.id.nav_budget -> {
                    showFragment(budgetFragment)
                    true
                }
                R.id.nav_me -> {
                    showFragment(meFragment)
                    true
                }
                else -> false
            }
        }
    }
    
    /**
     * 显示指定的Fragment
     */
    private fun showFragment(fragment: Fragment) {
        if (currentFragment == fragment) return
        
        val transaction = supportFragmentManager.beginTransaction()
        
        // 如果Fragment还没有被添加到容器中，则添加它
        if (!fragment.isAdded) {
            transaction.add(R.id.fragment_container, fragment)
        }
        
        // 隐藏当前显示的Fragment
        currentFragment?.let { transaction.hide(it) }
        
        // 显示新的Fragment
        transaction.show(fragment)
        transaction.commit()
        
        // 更新当前Fragment的引用
        currentFragment = fragment
    }
}