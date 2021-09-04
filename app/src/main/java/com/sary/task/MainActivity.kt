package com.sary.task

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sary.task.databinding.MainActivityBinding
import com.sary.task.store.ui.StoreFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.store -> replaceFragment(storeFragment)
                R.id.orders -> replaceFragment(ordersFragment)
                R.id.my_page -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment, tag: String? = null) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment, tag).commit()
    }
}

/**
 * A simple empty [Fragment].
 */
@AndroidEntryPoint
class EmptyFragment : Fragment()

private const val TAG_STORE_FRAGMENT = "storeFragment"