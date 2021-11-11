package com.egg.xample

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
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
    private lateinit var wordViewModel: WordViewModel

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView =
            findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerview)
        val adapter = WordListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        wordViewModel = ViewModelProvider(this).get(WordViewModel::class.java)

        wordViewModel.allWords.observe(this) { words ->
            words?.let { adapter.setWords(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewWordActivity::class.java)
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
                            wordViewModel.deleteWord(myWord)
                        }
                        wordViewModel.allWords
                    } else {

                        val builder = AlertDialog.Builder(this@MainActivity)

                        val userEdit = EditText(this@MainActivity)
                        userEdit.hint = "Introduce un nombre"
                        userEdit.gravity = Gravity.CENTER_HORIZONTAL
                        userEdit.ellipsize

                        // CREAR EL DIALOGO
                        builder.setTitle("Actualizar")
                            .setView(userEdit)
                            .setMessage("Edita tu palabra y actualizala dándole al botón de 'Guardar'\n")
                            .setPositiveButton("Guardar") { _, _ ->
                                val regionName = userEdit.text.toString()

                                if (regionName.isEmpty()) {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "El campo no puede estar vacío",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    val word = Word(position, regionName)
                                    launch {
                                        wordViewModel.updateWord(word)
                                    }
                                    wordViewModel.allWords
                                }
                            }
                            .setNegativeButton("Cancelar") { dialog, _ -> dialog.cancel() }

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
                val word = it.data?.getStringExtra(NewWordActivity.EXTRA_REPLY)?.let { Word(0, it) }
                if (word != null) {
                    wordViewModel.insert(word)
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
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
                    wordViewModel.deleteAll()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

