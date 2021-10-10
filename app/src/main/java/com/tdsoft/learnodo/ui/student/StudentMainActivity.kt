package com.tdsoft.learnodo.ui.student

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseActivity
import com.tdsoft.learnodo.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_student_main.*

class StudentMainActivity : BaseActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, StudentMainActivity::class.java)
            context.startActivity(intent)
        }
    }
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)

        navController = findNavController(R.id.nav_host_fragment_student)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.studentHomeFragment,
                R.id.teachersListFragment,
                R.id.studentProfileFragment,
            ), drawer_layout!!
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view_student.setupWithNavController(navController)

    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_logout) {
            learnodoRepo.logout()
            finish()
            LoginActivity.startActivity(this@StudentMainActivity)
        }
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}