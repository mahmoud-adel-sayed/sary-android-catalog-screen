package com.sary.task

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sary.task.store.ui.StoreFragment
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class MainActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create some empty fragments for presentation purposes
        val storeFragment = StoreFragment()
        val ordersFragment = EmptyFragment()
        val myPageFragment = EmptyFragment()
        replaceFragment(storeFragment)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.store -> replaceFragment(storeFragment)
                R.id.orders -> replaceFragment(ordersFragment)
                R.id.my_page -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment).commit()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}

/**
 * A simple empty [Fragment].
 */
class EmptyFragment : Fragment()