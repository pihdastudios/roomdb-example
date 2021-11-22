package com.egg.xample

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import xample.R
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var employeeViewModel: EmployeeViewModel

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Creating Notification Channel
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            val dbChannel = NotificationChannel("db_channel",
                "DB Channel",
                NotificationManager.IMPORTANCE_DEFAULT)
            //Register Channel to system
            val notifManager : NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notifManager.createNotificationChannel(dbChannel)
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)
        val adapter = EmployeeListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        employeeViewModel = ViewModelProvider(this).get(EmployeeViewModel::class.java)

        employeeViewModel.allWords.observe(this) { words ->
            words?.let { adapter.setWords(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewEmployeeActivity::class.java)
            getResult.launch(intent)

        }

        // OPTIONS FOR SWIPE RECYCLERVIEW
        val helper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val myWord = adapter.getWordAtPosition(position)

                    if (direction == ItemTouchHelper.LEFT) {
                        launch {
                            employeeViewModel.deleteWord(myWord)
                        }
                        employeeViewModel.allWords
                    } else {

                        val builder = AlertDialog.Builder(this@MainActivity)

                        val userEdit = EditText(this@MainActivity)
                        userEdit.hint = "Enter a new Name"
                        userEdit.gravity = Gravity.CENTER_HORIZONTAL
                        userEdit.ellipsize


                        builder.setTitle("Update")
                            .setView(userEdit)
                            .setMessage("Enter a new \n")
                            .setPositiveButton("Save") { _, _ ->
                                val regionName = userEdit.text.toString()

                                if (regionName.isEmpty()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "The field cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {


                                    val word = Employee(position, regionName, "test", "test")



                                    launch {
                                        employeeViewModel.updateWord(word)
                                    }
                                    employeeViewModel.allWords
                                }
                            }
                            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

                        // MOSTRAR
                        builder.show()
                    }
                }

                // ACTION SWIPE RECYCLERVIEW
                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val icon: Bitmap

                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView

                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        val p = Paint()
                        if (dX > 0) {

                            p.color = Color.parseColor("#1A7DCB")
                            val background = RectF(
                                itemView.left.toFloat(),
                                itemView.top.toFloat(),
                                dX,
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)

                            val left = itemView.left.toFloat() + width
                            val top = itemView.top.toFloat() + width
                            val right = itemView.left.toFloat() + 2 * width
                            val bottom = itemView.bottom.toFloat() - width

                            icon =
                                getBitmapFromVectorDrawable(applicationContext, R.drawable.ic_edit)
                            val iconDest = RectF(left, top, right, bottom)

                            c.drawBitmap(icon, null, iconDest, p)

                        } else {

                            p.color = Color.parseColor("#CB1A1A")

                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)


                            icon = getBitmapFromVectorDrawable(
                                applicationContext,
                                R.drawable.ic_delete_one
                            )

                            val left = itemView.right.toFloat() - 2 * width
                            val top = itemView.top.toFloat() + width
                            val right = itemView.right.toFloat() - width
                            val bottom = itemView.bottom.toFloat() - width
                            val iconDest = RectF(left, top, right, bottom)

                            c.drawBitmap(icon, null, iconDest, p)
                        }

                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    }
                }

            })

        helper.attachToRecyclerView(recyclerView)

    }

    fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private val getResult =

        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            if (it.resultCode == Activity.RESULT_OK) {


                var firstName = it.data?.getStringExtra(NewEmployeeActivity.FIRST_NAME)
                var lastName = it.data?.getStringExtra(NewEmployeeActivity.LAST_NAME)
                var role = it.data?.getStringExtra(NewEmployeeActivity.ROLE)
                val employee = Employee(0, firstName!!, lastName!!, role!!)



                if (employee != null) {
                    employeeViewModel.insert(employee)
                }
            }   else {
                //Notification
                    val intent = Intent(this,MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                val pendingIntent: PendingIntent = PendingIntent.getActivity(this,0,intent,0)


                val mbuilder = NotificationCompat.Builder(this,"db_channel")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("Employee Database")
                        .setContentText("Failed to save Employee data")
                        .setStyle(NotificationCompat.BigTextStyle()
                            .bigText("Data Employee yang baru saja dimasukkan gagal untuk disimpan di database"))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)

                val managerCompat = NotificationManagerCompat.from(this)
                    managerCompat.notify(1,mbuilder.build())
            }

        }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.deleteall -> {
                launch {
                    employeeViewModel.deleteAll()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

