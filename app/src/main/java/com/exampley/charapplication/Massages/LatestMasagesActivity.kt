package com.exampley.charapplication.Massages

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import com.exampley.charapplication.LatestMassageRow
import com.exampley.charapplication.R
import com.exampley.charapplication.RegisterActivity
import com.exampley.charapplication.models.User
import com.exampley.charapplication.models.chatMassage
import com.exampley.charapplication.newMassageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_latest_masages.*

class LatestMasagesActivity : AppCompatActivity() {
    companion object{
        var currentUser : User? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_masages)
        listnerForLatestMassage()
        recyclerView_latest_massage.adapter = adapter

        recyclerView_latest_massage.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->

        val intent = Intent(this,ChatLogActivity::class.java)
        val row = item as LatestMassageRow
        Log.d("ERROR",row.chatPartnerUser.toString()+" "+newMassageActivity.USER_KEY)
        intent.putExtra(newMassageActivity.USER_KEY,row.chatPartnerUser)
        startActivity(intent)

        }
        featchCurrentUser()
        verifyUserLoginIn()
    }





    val latestMassageMap = HashMap<String,chatMassage>()

    private fun refreshRecyclerVIewMessage() {
        adapter.clear()
        latestMassageMap.values.forEach{
            adapter.add(LatestMassageRow(it))
        }
    }
    val adapter = GroupAdapter<GroupieViewHolder>()

    private fun listnerForLatestMassage() {
        val fromid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-massage/$fromid")
        ref.addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val charMessage = snapshot.getValue(chatMassage::class.java) ?: return
                latestMassageMap[snapshot.key!!] = charMessage
                refreshRecyclerVIewMessage()
//                adapter.add(LatestMassageRow(charMessage))
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val charMessage = snapshot.getValue(chatMassage::class.java) ?: return
                latestMassageMap[snapshot.key!!] = charMessage
                refreshRecyclerVIewMessage()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR",error.toString())
            }

        })
    }




    private fun featchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
                Log.d("Latest","CurrentUsser  ${currentUser?.username}")

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun verifyUserLoginIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            R.id.newMassage -> {
                val intent = Intent(this, newMassageActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)

    }
}