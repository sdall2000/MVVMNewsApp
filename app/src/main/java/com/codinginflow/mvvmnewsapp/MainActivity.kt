package com.codinginflow.mvvmnewsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.codinginflow.mvvmnewsapp.databinding.ActivityMainBinding
import com.codinginflow.mvvmnewsapp.features.bookmarks.BookmarksFragment
import com.codinginflow.mvvmnewsapp.features.breakingnews.BreakingNewsFragment
import com.codinginflow.mvvmnewsapp.features.searchnews.SearchNewsFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var breakingNewsFragment: BreakingNewsFragment
    private lateinit var searchNewsFragment: SearchNewsFragment
    private lateinit var bookmarksFragment: BookmarksFragment

    private val fragments: Array<Fragment>
        get() = arrayOf(
            breakingNewsFragment,
            searchNewsFragment,
            bookmarksFragment
        )

    private var selectedIndex = 0

    private val selectedFragment get() = fragments[selectedIndex]

    private fun selectFragment(selectedFragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        fragments.forEachIndexed { index, fragment ->
            if (selectedFragment == fragment) {
                transaction = transaction.attach(fragment)
                selectedIndex = index
            } else {
                transaction = transaction.detach(fragment)
            }
        }

        transaction.commit()

        // Set title based on selected fragment
        title = when (selectedFragment) {
            is BreakingNewsFragment -> getString(R.string.title_breaking_news)
            is SearchNewsFragment -> getString(R.string.title_search_news)
            is BookmarksFragment -> getString(R.string.title_bookmarks)
            else -> ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            // First time activity is created
            breakingNewsFragment = BreakingNewsFragment()
            searchNewsFragment = SearchNewsFragment()
            bookmarksFragment = BookmarksFragment()

            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, breakingNewsFragment, TAG_BREAKING_NEWS_FRAGMENT)
                .add(R.id.fragment_container, searchNewsFragment, TAG_SEARCH_NEWS_FRAGMENT)
                .add(R.id.fragment_container, bookmarksFragment, TAG_BOOKMARKS_FRAGMENT)
                .commit()
        } else {
            // Config change or process death
            // Rebind the fragment variables to the already created fragments
            breakingNewsFragment = supportFragmentManager.findFragmentByTag(
                TAG_BREAKING_NEWS_FRAGMENT
            ) as BreakingNewsFragment
            searchNewsFragment = supportFragmentManager.findFragmentByTag(
                TAG_SEARCH_NEWS_FRAGMENT
            ) as SearchNewsFragment
            bookmarksFragment = supportFragmentManager.findFragmentByTag(
                TAG_BOOKMARKS_FRAGMENT
            ) as BookmarksFragment

            // Get the saved selected index
            selectedIndex = savedInstanceState.getInt(KEY_SELECTED_INDEX, 0)
        }

        selectFragment(selectedFragment)

        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_breaking -> breakingNewsFragment
                R.id.nav_search -> searchNewsFragment
                R.id.nav_bookmarks -> bookmarksFragment
                else -> throw IllegalArgumentException("Unexpected itemId")
            }

            selectFragment(fragment)

            true
        }
    }

    override fun onBackPressed() {
        // If the currently selected fragment is not the first one, then go to the first one
        // which is nav_breaking.  Otherwise, do the default back pressed handling which
        // should exit the application.
        if (selectedIndex != 0) {
            binding.bottomNav.selectedItemId = R.id.nav_breaking
        } else {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Save the selected index
        outState.putInt(KEY_SELECTED_INDEX, selectedIndex)
    }
}

private const val TAG_BREAKING_NEWS_FRAGMENT = "TAG_BREAKING_NEWS_FRAGMENT"
private const val TAG_SEARCH_NEWS_FRAGMENT = "TAG_SEARCH_NEWS_FRAGMENT"
private const val TAG_BOOKMARKS_FRAGMENT = "TAG_BOOKMARKS_FRAGMENT"
private const val KEY_SELECTED_INDEX = "KEY_SELECTED_INDEX"