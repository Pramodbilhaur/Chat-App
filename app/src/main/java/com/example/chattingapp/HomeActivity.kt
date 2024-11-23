package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userList: ArrayList<SaveUser>
    private lateinit var userAdapter: UserAdapter
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mdbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userList = ArrayList()
        mdbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        binding.rvUserList.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(this@HomeActivity, userList)
        binding.rvUserList.adapter = userAdapter

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)



        mdbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(SaveUser::class.java)
                    if(mAuth.currentUser?.uid != currentUser?.uid && currentUser != null){
                            userList.add(currentUser)
                    }
                }
                Log.d("User List", "onDataChange: $userList")
                userAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            return true
        }
        return true
    }
}