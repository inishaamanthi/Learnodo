package com.tdsoft.learnodo.ui.teacher

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseActivity
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_teacher_main.*


class TeacherMainActivity : BaseActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, TeacherMainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_main)

        navController = findNavController(R.id.nav_host_fragment_teacher)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.teacherHomeFragment,
                R.id.teacherMaterialsFragment,
                R.id.teacherExamsFragment,
            ), drawer_layout_teacher!!
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view_teacher.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { nav: NavController,
                                                        navDestination: NavDestination,
                                                        _: Bundle? ->
            when (navDestination.id) {
                R.id.teacherMaterialsFragment -> {
                    fabMain.show()
                }
                else -> {
                    fabMain.hide()
                }
            }
        }
        fabMain.setOnClickListener {
            val navHostFragment: NavHostFragment? =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment_teacher) as NavHostFragment?
            val currentFrag: BaseFragment =
                navHostFragment!!.childFragmentManager.fragments[0] as BaseFragment
            currentFrag.onFabClick()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            learnodoRepo.logout()
            finish()
            LoginActivity.startActivity(this@TeacherMainActivity)
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}