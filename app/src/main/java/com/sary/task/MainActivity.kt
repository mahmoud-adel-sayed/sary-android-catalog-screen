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

        // We could use the navigation component (Jetpack library) to simplify
        // management of fragments & the app navigation
        var storeFragment: Fragment? = supportFragmentManager.findFragmentByTag(TAG_STORE_FRAGMENT)
        if (storeFragment == null) {
            storeFragment = StoreFragment()
        }
        replaceFragment(fragment = storeFragment, tag = TAG_STORE_FRAGMENT)

        // Create some empty fragments for presentation purposes
        val ordersFragment = EmptyFragment()
        val myPageFragment = EmptyFragment()

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

    private fun replaceFragment(
        fragment: Fragment,
        tag: String? = null
    ) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment, tag).commit()
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
}

/**
 * A simple empty [Fragment].
 */
class EmptyFragment : Fragment()

private const val TAG_STORE_FRAGMENT = "storeFragment"